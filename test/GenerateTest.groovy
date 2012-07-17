import zynx.flunk.Generate
import zynx.flunk.Module
import org.junit.Test
import zynx.flunk.NetKernelBuilder

class GenerateTest extends GroovyTestCase {

    void testAcceptModuleFileAndReturnXML() {

        ByteArrayOutputStream output = new ByteArrayOutputStream()

        String modulePath = '/home/pair/dev/fluent-netkernel/src/zynx/flunk/test/SampleModule.groovy'
        Generate gut = new Generate()
        NetKernelBuilder builder = new NetKernelBuilder()

        Module module = builder.module (uri: 'urn:alex:test:module', name: 'my test module', version: '1.0.0', ) {
            expose_to 'http'
            expose {
                file_path 'res:/get-doc-file/(.*)'
                via_rewrite 'res:/resources/doc/$1'
            }
            expose {
                resource ('res:/test/hello/{user}')
                use_script 'res:/resources/scripts/test.ftl'
                with_argument (name: 'user', pass_by: 'value')
            }
            expose {
                resource ('res:/test/hello2/{user}')
                use_script 'res:/resources/scripts/myscript.groovy'
            }
        }

        String expectedXml = module.buildModuleXml()

        System.setOut(new PrintStream(output))

        gut.Main(modulePath)
        String xml = output.toString()
        System.setOut(null)

        assertTrue('XML returned from Main() is not valid', expectedXml == xml)
    }
}
