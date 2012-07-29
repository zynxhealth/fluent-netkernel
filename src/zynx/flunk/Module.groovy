package zynx.flunk

import groovy.xml.MarkupBuilder

class Module {
    String uri
    String version
    String name
    String description
    boolean isOnFrontEnd = false
    List<Exposure> exposures = [ ]

    public String buildModuleXml() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        xml.module(version : '2.0') {
            buildModuleMeta(xml)
            rootspace {
                buildFrontEndFulcrumHook(xml)
                buildSimpleExposures(xml)
                mapper {
                    config {
                        buildEndpoints(xml)
                    }
                    buildInnerSpace(xml)
                }
            }
        }

        writer.toString()
    }

    private def buildFrontEndFulcrumHook(xml) {
        if (this.isOnFrontEnd) {
            xml.fileset {
                regex('res:/etc/system/SimpleDynamicImportHook.xml')
            }
        }
    }

    private def buildModuleMeta(xml) {
        xml.meta {
            identity(null) {
                uri this.uri
                version this.version
            }
            info {
                name this.name
                description this.description
            }
        }
        xml.system {
            dynamic()
        }
    }

    private def buildSimpleExposures(xml) {
        Exposure thisExposure

        this.exposures.findAll { it.filePath }.each {
            thisExposure = it
            xml.fileset {
                regex (thisExposure.filePath)
                if (thisExposure.rewriteUri) {
                    rewrite (thisExposure.rewriteUri)
                }
            }
        }
    }

    private def buildEndpoints(xml) {
        Resource thisResource

        this.exposures.findAll { it.resource }.each {
            thisResource = it.resource
            xml.endpoint {
                grammar {
                    if (thisResource.uri) {
                        simple(thisResource.uri)
                    }
                    else {
                        active {
                            identifier('active:' + thisResource.identifier)
                            thisResource.getArguments().each {
                                argument(name: it.name, min: it.min, max: it.max)
                            }
                            if (thisResource.varArgs) {
                                varargs()
                            }
                        }
                    }
                }
                buildRequest(xml, thisResource.request)
                buildDPMLSequence(xml, thisResource)
            }
        }
    }

    private def buildRequest(xml, Request thisRequest) {
        if(thisRequest) {

            xml.request {
                identifier(thisRequest.identifier)
                thisRequest.arguments.each {
                    switch (it.passBy) {
                        case 'value':
                            argument(name: it.name, method: "as-string", it.value)
                            break
                        default:
                            argument(name: it.name, it.value)
                            break
                    }
                }
            }
        }
    }

    private def buildInnerSpace(xml) {
        Resource thisResource, innerResource
        Request thisRequest
        Import thisImport

        xml.space {
            this.exposures.findAll { it.resource }.each {
                thisResource = it.resource
                thisRequest = thisResource.request

                if (thisRequest) {
                    thisRequest.imports.each {
                        thisImport = it
                        'import' {
                            'uri' (thisImport.uri)
                        }
                    }

                    if (thisRequest.resourcePath) {
                        fileset {
                            regex (thisRequest.resourcePath)
                        }
                    }
                }

                if (thisResource.sequence) {
                    'import' {
                        'uri' ('urn:org:netkernel:lang:dpml')
                    }
                    thisResource.sequence.requests.each {
                        innerResource = it
                        'import' {
                            'uri' ('urn:org:netkernel:lang:' + innerResource.getLanguage())
                        }
                        fileset {
                            regex (innerResource.scriptPath)
                        }
                    }
                }
            }
        }
    }

    private def buildDPMLSequence(xml, thisResource) {
        Resource innerResource
        Argument thisArgument

        if (thisResource.sequence) {

            xml.request {
                identifier('active:dpml')
                argument(name:'operator') {
                    sequence {
                         thisResource.sequence.requests.each {
                             innerResource = it
                             request (assignment: innerResource.identifier) {
                                 identifier('active:' + innerResource.getLanguage())
                                 argument(name:'operator', innerResource.scriptPath)
                                 innerResource.getArguments().each {
                                     thisArgument = it
                                     argument(name: thisArgument.name) {
                                         if (thisArgument.value) {
                                             literal (type: 'string', thisArgument.value)
                                         }
                                     }
                                 }
                             }
                         }
                    }
                }
            }
        }
    }

    public void setAttributes(Map attrs) {
        attrs.each { key, value ->
            switch (key) {
                case 'name':
                    name = value
                    break
                case 'uri':
                    uri = value
                    break
                case 'version':
                    version = value
                    break
                case 'description':
                    description = value
                    break
            }
        }
    }
}
