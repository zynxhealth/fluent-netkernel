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
        assertTrue("Endpoint doesn't exist",
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>/).find())
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
        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())
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
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:groovy identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:groovy/).find())

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


    void testExecuteGroovyScriptWithParameters() {
        def uri = 'res:/{arg1}/{arg2}/something/{arg3}/moretext/{arg4}'
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
                uri.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:groovy identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:groovy/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*<script>/ +
                scriptPath).find())

        assertTrue("Endpoint request doesn't contain argument arg1",
               (xml.replace('\n', ' ') =~
               /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg1'>\[\[arg:arg1\]\]/).find())

        assertTrue("Endpoint request doesn't contain argument arg2",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg2'>\[\[arg:arg2\]\]/).find())

        assertTrue("Endpoint request doesn't contain argument arg3",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg3'>\[\[arg:arg3\]\]/).find())

        assertTrue("Endpoint request doesn't contain argument arg4",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg4'>\[\[arg:arg4\]\]/).find())

        assertTrue("Endpoint internal space doesn't contain groovy language import",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:groovy/).find())

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