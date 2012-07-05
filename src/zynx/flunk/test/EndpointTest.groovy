package zynx.flunk.test

import zynx.flunk.NetKernelBuilder
import zynx.flunk.Module

class EndpointTest extends GroovyTestCase {
    private NetKernelBuilder builder;

    protected void setUp() {
        builder = new NetKernelBuilder()
    }

    void testEndpointCanExist() {
        def mut = builder.module() {
            map {}
        }
        String xml = mut.buildModuleXml()
        assert xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>/
    }

    void testSpecifySimpleGrammar() {
        def uri = 'res:/responseMessage/{inputMessage}'
        Module mut = builder.module() {
            map {
                simple_uri uri
            }
        }
        String xml = mut.buildModuleXml()
        assertFalse("Endpoints list empty", mut.endpoints.isEmpty())
        assert                 xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')
    }

    void testExecuteGroovyScript(){
        def uri = 'res:/responseMessage/{inputMessage}'
        def scriptPath = 'res:/resources/scripts/myscript.groovy'

        Module mut = builder.module () {
            map {
                simple_uri uri
                to_groovy scriptPath
            }
        }

        String xml = mut.buildModuleXml()
        assertFalse("Endpoints list empty", mut.endpoints.isEmpty())
        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find()
        )
        assertTrue("Endpoint doesn't contain active:groovy identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:groovy/).find()
        )

    }
}
