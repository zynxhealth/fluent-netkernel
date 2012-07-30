import zynx.flunk.*
import java.util.regex.Pattern

class XUnitTest extends GroovyTestCase {
    private NetKernelBuilder builder

    protected void setUp() {
        builder = new NetKernelBuilder()
    }

    void testCreateXUnitModule() {
        def mutName = 'My Module'
        def moduleVersion = '1.0.0'
        def mutUri = 'urn:my:module:impl'

        TestList xut = builder.tests(mutName: mutName, version: moduleVersion, mutUri: mutUri) {}

        String xml = xut.buildModuleXml()

        assertTrue("Module has invalid uri",
                (xml.replace('\n', ' ') =~
                /<meta.*>.*<identity.*>.*<uri>/ + mutUri + /:tests/).find())

        assertTrue("Module has invalid version",
                (xml.replace('\n', ' ') =~
                /<meta.*>.*<identity.*>.*<version>/ + moduleVersion).find())

        assertTrue("Module has invalid name",
                (xml.replace('\n', ' ') =~
                /<meta.*>.*<info.*>.*<name>/ + mutName + / - Tests/).find())
    }

    void testCreateXUnitModuleWithTestSpace() {
        def mutName = 'My Module'
        def moduleVersion = '1.0.0'
        def mutUri = 'urn:my:module:impl'

        TestList xut = builder.tests(mutName: mutName, version: moduleVersion, mutUri: mutUri) {}

        String xml = xut.buildModuleXml()

        assertTrue("Test space is missing",
                (xml.replace('\n', ' ') =~
                /<rootspace.*name='/ + mutName + / - Tests'/).find())

        assertTrue("Test space has invalid uri",
                (xml.replace('\n', ' ') =~
                /<rootspace.*uri='/ + mutUri + /:tests'/).find())

        assertTrue("Test space is missing fileset for Tests.xml",
                (xml.replace('\n', ' ') =~
                /<fileset>.*<regex>res:\/etc\/system\/Tests.xml/).find())

        assertTrue("Test space is missing fileset for test/.*",
                (xml.replace('\n', ' ') =~
                /<fileset>.*<regex>res:\/resources\/test/).find())

        assertTrue("Test space is missing Limiter endpoint",
                (xml.replace('\n', ' ') =~
                /<endpoint>.*<prototype>Limiter/).find())

    }

    void testCreateXUnitModuleWhoseTestSpaceImportsMockSpace() {
        def mutName = 'My Module'
        def moduleVersion = '1.0.0'
        def mutUri = 'urn:my:module:impl'

        TestList xut = builder.tests(mutName: mutName, version: moduleVersion, mutUri: mutUri) {}

        String xml = xut.buildModuleXml()

        assertTrue("Test space is missing",
                (xml.replace('\n', ' ') =~
                /<rootspace.*name='/ + mutName + / - Tests'/).find())

        assertTrue("Mock space is not imported",
                (xml.replace('\n', ' ') =~
                /<rootspace.*uri='/ + mutUri + /:tests'.*<import>.*<uri>/ + mutUri + /:mocks/).find())
    }

    void testCreateXUnitModuleWithMockSpace() {
        def mutName = 'My Module'
        def moduleVersion = '1.0.0'
        def mutUri = 'urn:my:module:impl'

        TestList xut = builder.tests (mutName: mutName, version: moduleVersion, mutUri: mutUri) {}

        String xml = xut.buildModuleXml()

        assertTrue("Mock space is missing",
                (xml.replace('\n', ' ') =~
                /<rootspace.*name='/ + mutName + / - Mocks'/).find())

        assertTrue("Mock space has invalid uri",
                (xml.replace('\n', ' ') =~
                /<rootspace.*uri='/ + mutUri + /:mocks'/).find())
    }

    void testCreateTest() {
        def testName = 'my test'

        TestList xut = builder.tests() {
            test (name: testName) {
            }
        }

        String xml = xut.buildTestListXml()

        assertTrue("Test '$testName' is missing from the test list",
                (xml.replace('\n', ' ') =~
                /<testlist.*>.*<test name='/ + testName + /'/).find())
    }

    void testCreateTestWithRequest() {
        def testName = 'my test'
        def requestedResource = 'active:myResource'

        TestList xut = builder.tests() {
            test (name: testName) {
                make_request_to (requestedResource) {
                }
            }
        }

        String xml = xut.buildTestListXml()

        assertTrue("Test '$testName' is missing from the test list",
                (xml.replace('\n', ' ') =~
                /<testlist.*>.*<test name='/ + testName + /'/).find())

        assertTrue("Test '$testName' is missing request to $requestedResource",
                (xml.replace('\n', ' ') =~
                /<testlist.*>.*<test.*>.*<request>.*<identifier>/ + requestedResource).find())
    }

    void testCreateTestWithRequestWithArgument() {
        def testName = 'my test'
        def requestedResource = 'active:myResource'
        def argumentName = 'myarg'
        def argumentValue = 'argValue'

        TestList xut = builder.tests() {
            test (name: testName) {
                make_request_to (requestedResource) {
                    with_argument (name: argumentName, value: argumentValue)
                }
            }
        }

        String xml = xut.buildTestListXml()

        assertTrue("Test '$testName' is missing request to $requestedResource",
                (xml.replace('\n', ' ') =~
                /<testlist.*>.*<test.*>.*<request>.*<identifier>/ + requestedResource).find())

        assertTrue("Test '$testName' is missing argument $argumentName",
                (xml.replace('\n', ' ') =~
                /<request>.*<argument name='/ + argumentName + /'>/).find())

        assertTrue("Argument $argumentName has incorrect value",
                (xml.replace('\n', ' ') =~
                /<argument name='/ + argumentName + /'>/ + argumentValue).find())
    }

    void testCreateTestWithAssertResponse() {
        def testName = 'my test'
        def requestedResource = 'active:myResource'
        def responseString = 'match me'
        def min = 10
        def max = 20

        TestList xut = builder.tests() {
            test (name: testName) {
                make_request_to (requestedResource)

                assert_response {
                    stringEquals responseString
                    minTime min
                    maxTime max
                }
            }
        }

        String xml = xut.buildTestListXml()

        assertTrue("Test '$testName' is missing from the test list",
                (xml.replace('\n', ' ') =~
                /<testlist.*>.*<test name='/ + testName + /'/).find())

        assertTrue("Test '$testName' is missing an assert for StringEquals with '$responseString'",
                (xml.replace('\n', ' ') =~
                /<testlist.*>.*<test.*>.*<assert>.*<stringEquals>/ + responseString).find())

        assertTrue("Test '$testName' is missing an assert for minTime = $min",
                (xml.replace('\n', ' ') =~
                /<testlist.*>.*<test.*>.*<assert>.*<minTime>/ + min).find())

        assertTrue("Test '$testName' is missing an assert for maxTime = $max",
                (xml.replace('\n', ' ') =~
                /<testlist.*>.*<test.*>.*<assert>.*<maxTime>/ + max).find())

    }

    void testCreateTestWithMock() {
        def testName = 'my test'
        def mockedResource = 'mymock'
        def argName = 'argName'

        TestList xut = builder.tests() {
            test (name: testName) {
                mock_resource (mockedResource) {
                    with_argument (name: argName)
                }
            }
        }

        String xml = xut.buildModuleXml()

        assertTrue("Endpoint for '$mockedResource' doesn't exist",
                (xml.replace('\n', ' ') =~
                /<endpoint.*>.*<grammar>.*<active>.*<identifier>/ + mockedResource).find())

        assertTrue("Endpoint '$mockedResource' is missing argument '$argName'",
                (xml.replace('\n', ' ') =~
                /<endpoint.*>.*<grammar>.*<active>.*<argument name='/ + argName + /'/).find())

    }

//    void testCreateTestWithMockAndResponse() {
//        def testName = 'my test'
//        def mockedResource = 'mymock'
//        def argName = 'argName'
//
//        TestList xut = builder.tests() {
//            test (name: testName) {
//                mock_resource (mockedResource) {
//                    with_argument (name: argName)
//
//                    respond_with {
//                        resource ('res:/test/hello/{name}')
//                        use_script 'res:/resources/scripts/sayHello.ftl' {
//                            with_argument (name: 'name', value: '[[arg:name]]', pass_by: 'value')
//                        }
//                    }
//                }
//            }
//        }
//
//        String xml = xut.buildModuleXml()
//        println xml
//
//        assertTrue("Endpoint for '$mockedResource' doesn't exist",
//                (xml.replace('\n', ' ') =~
//                        /<endpoint.*>.*<grammar>.*<active>.*<identifier>/ + mockedResource).find())
//
//        assertTrue("Endpoint '$mockedResource' is missing argument '$argName'",
//                (xml.replace('\n', ' ') =~
//                        /<endpoint.*>.*<grammar>.*<active>.*<argument name='/ + argName + /'/).find())
//
//    }

}