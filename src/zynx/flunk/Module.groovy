package zynx.flunk

import groovy.xml.MarkupBuilder

/**
 * Created with IntelliJ IDEA.
 * User: WinslowRi01
 * Date: 6/29/12
 * Time: 10:20 PM
 * To change this template use File | Settings | File Templates.
 */
class Module {
    String uri
    String version
    String name
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
                    description this.name
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
                                    simple(thisEndpoint.grammar)
                                }
                                if (thisEndpoint.scriptPath) {
                                    request {
                                        identifier('active:' + thisEndpoint.getLanguage())
                                        argument(name: 'operator'){
                                            script(thisEndpoint.scriptPath)
                                        }
                                        thisEndpoint.getArguments().each {
                                            argument(name: it, "[[arg:$it]]")
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
                    name = value;
                    break;
                case 'uri':
                    uri = value;
                    break;
                case 'version':
                    version = value;
                    break;
            }
        }
    }
}
