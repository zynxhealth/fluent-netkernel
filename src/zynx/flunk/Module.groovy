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

    public String buildModuleXml() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

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

                this.endpoints.each {
                    def that = it
                    mapper {
                        config {
                            endpoint {
                                grammar {
                                    simple(that.grammar)
                                }
                                if (!that.groovyScript.isEmpty()) {
                                    request {
                                        identifier('active:groovy')
                                        argument(name: 'operator'){
                                            script(that.groovyScript)
                                        }
                                        if (that.getArguments().size() > 0) {
                                            that.getArguments().each {
                                                argument(name: it, "[[arg:$it]]")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!that.groovyScript.isEmpty()) {
                            space {
                                'import' {
                                    'uri' ('urn:org:netkernel:lang:groovy')
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
