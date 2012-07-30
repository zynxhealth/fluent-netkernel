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

        TestList xut = builder.tests(mutName: mutName, version: moduleVersion, mutUri: mutUri) {}

        String xml = xut.buildModuleXml()

        println xml

        assertTrue("Mock space is missing",
                (xml.replace('\n', ' ') =~
                        /<rootspace.*name='/ + mutName + / - Mocks'/).find())

        assertTrue("Mock space has invalid uri",
                (xml.replace('\n', ' ') =~
                        /<rootspace.*uri='/ + mutUri + /:mocks'/).find())
    }

}