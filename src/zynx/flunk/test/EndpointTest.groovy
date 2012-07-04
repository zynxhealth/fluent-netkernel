package zynx.flunk.test

import zynx.flunk.NetKernelBuilder
import zynx.flunk.Module
import zynx.flunk.Endpoint

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

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                        /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*<script>/ +
                        scriptPath).find())

        assertTrue("Endpoint internal space doesn't contain groovy language import",
                (xml.replace('\n', ' ') =~
                        /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:groovy/).find())
    }

    void testExtractArgumentsFromGrammarUri() {
        Endpoint eut = new Endpoint()
        eut.grammar = 'res:/{arg1}/{arg2}/something/{arg3}/moretext/{arg4}'
        List<String> args = eut.getArguments()
        assertEquals(4, args.size())
        assertEquals('arg1', args[0].toString())
        assertEquals('arg2', args[1].toString())
        assertEquals('arg3', args[2].toString())
        assertEquals('arg4', args[3].toString())
    }

}



//
//  module {
//      resource {
//          located_at ...
//          implemented_via script
//      }
//  }
//

//module {
//    expose {
//        via_simple_uri
//        script scriptPath
//    }
//}

//module {
//    map {
//        simple_uri uri
//        to_groovy scriptPath
//    }
//}