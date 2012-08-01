import zynx.flunk.Generate
import zynx.flunk.Module
import zynx.flunk.NetKernelBuilder

class GenerateTest extends GroovyTestCase {

    void testAcceptModuleFileAndReturnXML() {
        def userDirectory = System.getProperty("user.dir")
        String modulePath = "$userDirectory/test/test-data/SampleModule.groovy"

        Generate sut = new Generate()
        NetKernelBuilder builder = new NetKernelBuilder()

        String expectedXml = getExpectedXml(builder)

        ByteArrayOutputStream output = new ByteArrayOutputStream()
        System.setOut(new PrintStream(output))
        sut.main(modulePath)
        String actualXml = output.toString().trim()
        System.setOut(null)

        assertEquals(expectedXml, actualXml)
    }

    private String getExpectedXml(NetKernelBuilder builder) {
        Module module = builder.module(uri: 'urn:flunk:testmodule', name: 'my test module', version: '1.0.0',) {
            expose_to 'http'
            expose {
                file_path 'res:/get-doc-file/(.*)'
                via_rewrite 'res:/resources/doc/$1'
            }
            expose {
                resource('res:/test/hello/{user}')
                use_script 'res:/resources/scripts/test.ftl'
                with_argument (name: 'user', pass_by: 'argument-as-string')
            }
            expose {
                resource('res:/test/hello2/{user}')
                use_script 'res:/resources/scripts/myscript.groovy'
            }
        }

        module.buildModuleXml()
    }
}
