package zynx.flunk

import groovy.xml.MarkupBuilder

class Module {
    String uri
    String version
    String name
    String descrption
    boolean isOnFrontEnd = false
    List<Endpoint> endpoints = [ ]
    List<Exposure> exposures = [ ]

    public String buildModuleXml() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def thisEndpoint

        xml.module(version : '2.0') {
            meta {
                identity(null) {
                    uri this.uri
                    version this.version
                }
                info {
                    name this.name
                    description this.descrption
                }
            }
            system {
                dynamic()
            }
            rootspace {
                if (this.isOnFrontEnd) {
                    fileset {
                        regex('res:/etc/system/SimpleDynamicImportHook.xml')
                    }
                }

                this.exposures.each {
                    def exposure = it
                    fileset {
                        regex(exposure.filePath)
                        rewrite(exposure.rewriteUri)
                    }
                }

                mapper {
                    config {
                        this.endpoints.each {
                            thisEndpoint = it
                            endpoint {
                                grammar {
                                    if (thisEndpoint.simpleGrammar) {
                                        simple(thisEndpoint.simpleGrammar)
                                    }
                                    else {
                                        if (thisEndpoint.resource) {
                                            active {
                                                identifier('active:' + thisEndpoint.resource.identifier)

                                                thisEndpoint.resource.getArguments().each {
                                                    argument(name: it.name, min: it.min, max: it.max)
                                                }

                                                if (thisEndpoint.resource.varArgs) {
                                                    varargs()
                                                }
                                            }
                                        }
                                    }
                                }

                                if (thisEndpoint.scriptPath) {
                                    request {
                                        identifier('active:' + thisEndpoint.getLanguage())
                                        argument(name: 'operator'){
                                            script(thisEndpoint.scriptPath)
                                        }
                                        thisEndpoint.getArguments().each {
                                            switch (it.passBy) {
                                                case 'value':
                                                    argument(name: it.name, method: "as-string", "[[arg:$it.name]]")
                                                    break
                                                default:
                                                    argument(name: it.name, "[[arg:$it.name]]")
                                                    break
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    space {
                        this.endpoints.each {
                            thisEndpoint = it
                            if (thisEndpoint.scriptPath) {
                                'import' {
                                    'uri' ('urn:org:netkernel:lang:' + thisEndpoint.getLanguage())
                                }
                                fileset {
                                    regex (thisEndpoint.scriptPath)
                                }
                            }
                        }
                    }
                }
            }
        }

        writer.toString()
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
                    descrption = value
                    break
            }
        }
    }
}
