package zynx.flunk

import groovy.xml.MarkupBuilder

class TestList {
    final static String DefaultTestListFileUri = "res:/resources/test/testlist.xml"

    String mutName
    String version
    String mutUri
    String description

    List<Test> tests = []

    public String buildModuleXml() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        xml.module(version: '2.0') {
            buildModuleMeta(xml)
            buildTestSpace(xml)
            buildMockSpace(xml)
        }

        writer.toString()
    }

    String buildTestReferencesXml() {
        StringWriter writer = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(writer)

        // tests(mutName: 'Flunk Sample Module', version: '1.0.0', mutUri: 'urn:flunk:sample:module:impl') {

        builder.tests {
            test {
                id("$mutUri:test")
                name("$mutName tests")
                desc("$mutName tests")
                uri(DefaultTestListFileUri)
            }
        }

        return writer.toString()
    }

    public String buildTestListXml() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        Test thisTest

        xml.testlist {
            tests.each {
                thisTest = it
                test(name: thisTest.name) {
                    if (thisTest.request) {
                        request {
                            identifier(thisTest.request.identifier)
                            thisTest.request.arguments.each {
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
                        buildAsserts(xml, thisTest)
                    }
                }
            }
        }

        writer.toString()
    }

    private def buildAsserts(xml, test) {
        Assert thisAssert

        test.asserts.each {
            thisAssert = it
            xml.'assert' {
                if (thisAssert.stringEquals) {
                    stringEquals(thisAssert.stringEquals)
                }
                if (thisAssert.minTime) {
                    minTime(thisAssert.minTime)
                }
                if (thisAssert.maxTime) {
                    maxTime(thisAssert.maxTime)
                }
            }
        }

    }

    private def buildTestSpace(xml) {
        xml.rootspace(name: "$mutName - Tests", uri: "$mutUri:tests") {

            fileset {
                regex('res:/etc/system/Tests.xml')
            }
            fileset {
                regex('res:/resources/test/.*')
            }
            endpoint {
                prototype('Limiter') {
                    grammar('res:/etc/') {
                        regex(type: "anything")
                    }
                }
            }

            'import' {
                'uri'("$mutUri:mocks")
            }

            'import' {
                'uri'(mutUri)
            }

            'import' {
                'uri'('urn:org:netkernel:ext:layer1')
                'private'()
            }
        }
    }

    private def buildEndpoints(xml) {
        Resource thisResource

        tests.each {
            it.mocks.each {
                thisResource = it
                xml.endpoint {
                    grammar {
                        if (thisResource.uri) {
                            simple(thisResource.uri)
                        }
                        else {
                            active {
                                identifier(thisResource.identifier)
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
                }
            }
        }
    }

    private def buildMockSpace(xml) {
        xml.rootspace(name: "$mutName - Mocks", uri: "$mutUri:mocks") {
            if (tests.any { it.mocks }) {
                mapper {
                    config {
                        buildEndpoints(xml)
                    }
                    space {
                        buildDependencies(xml)
                    }
                }
            }
        }
    }

    private def buildDependencies(xml) {
        Resource thisResource, innerResource
        Request thisRequest
        Import thisImport

        tests.each {
            it.mocks.each {
                thisResource = it
                thisRequest = thisResource.request

                if (thisRequest) {
                    thisRequest.imports.each {
                        thisImport = it
                        xml.'import' {
                            'uri'(thisImport.uri)
                        }
                    }

                    if (thisRequest.resourcePath) {
                        xml.fileset {
                            regex(thisRequest.resourcePath)
                        }
                    }
                }
            }
        }
    }


    private def buildRequest(xml, Request thisRequest) {
        if (thisRequest) {

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

