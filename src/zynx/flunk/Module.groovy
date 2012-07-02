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