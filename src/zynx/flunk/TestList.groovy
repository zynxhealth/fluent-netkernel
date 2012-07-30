package zynx.flunk

import groovy.xml.MarkupBuilder

class TestList {
    String mutName
    String version
    String mutUri
    String description

    public String buildModuleXml() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        xml.module(version : '2.0') {
            buildModuleMeta(xml)
            buildTestSpace(xml)
            buildMockSpace(xml)
        }

        writer.toString()
    }

    public String buildTestListXml() {
        return ''
    }


    private def buildTestSpace(xml)  {
        xml.rootspace (name: "$mutName - Tests", uri: "$mutUri:tests") {

            fileset {
                regex('res:/etc/system/Tests.xml')
            }
            fileset {
                regex('res:/resources/test/.*')
            }
            endpoint {
                prototype ('Limiter') {
                    grammar ('res:/etc/') {
                        regex (type: "anything")
                    }
                }
            }

            'import' {
                'uri' ("$mutUri:mocks")
            }

            'import' {
                'uri' (mutUri)
            }

            'import' {
                'uri' ('urn:org:netkernel:ext:layer1')
                'private' ()
            }
        }
    }

    private def buildMockSpace(xml) {
        xml.rootspace (name: "$mutName - Mocks", uri: "$mutUri:mocks") {

        }
    }

    private def buildModuleMeta(xml) {
        xml.meta {
            identity(null) {
                uri this.mutUri + ':tests'
                version this.version
            }
            info {
                name this.mutName + ' - Tests'
                description this.description
            }
        }
        xml.system {
            dynamic()
        }
    }

    public void setAttributes(Map attrs) {
        attrs.each { key, value ->
            switch (key) {
                case 'mutName':
                    mutName = value
                    break
                case 'mutUri':
                    mutUri = value
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

