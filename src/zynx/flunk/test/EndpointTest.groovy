package zynx.flunk.test

import zynx.flunk.Endpoint
import zynx.flunk.Module
import zynx.flunk.NetKernelBuilder
import zynx.flunk.Argument

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
                to_script scriptPath
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
        List<Argument> args = eut.getArguments()

        assertEquals("Didn't create 4 arguments", 4, args.size())
        assertTrue("Expected argument 1 not found", args.find {it.name == 'arg1' } != null)
        assertTrue("Expected argument 2 not found", args.find {it.name == 'arg2' } != null)
        assertTrue("Expected argument 3 not found", args.find {it.name == 'arg3' } != null)
        assertTrue("Expected argument 4 not found", args.find {it.name == 'arg4' } != null)
        assertTrue("Unexpected argument 5 found", args.find {it.name == 'arg5' } == null)
    }

    void testExecuteGroovyScriptWithParameters() {
        def uri = 'res:/{arg1}/{arg2}/something/{arg3}/moretext/{arg4}'
        def scriptPath = 'res:/resources/scripts/myscript.gy'

        Module mut = builder.module () {
            map {
                simple_uri uri
                to_script scriptPath
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

        assertTrue("Endpoint internal space doesn't contain reference to script path",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<fileset.*>.*<regex>/ + scriptPath).find())

    }



    void testExecuteJavaScriptScript(){
        def uri = 'res:/responseMessage/{inputMessage}'
        def scriptPath = 'res:/resources/scripts/myscript.js'

        Module mut = builder.module () {
            map {
                simple_uri uri
                to_script scriptPath
            }
        }

        String xml = mut.buildModuleXml()

        assertFalse("Endpoints list empty", mut.endpoints.isEmpty())

        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:javascript identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:javascript/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*<script>/ +
                scriptPath).find())

        assertTrue("Endpoint internal space doesn't contain javascript language import",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:javascript/).find())
    }

    void testExecuteFreemarkerScript() {
        def uri = 'res:/responseMessage/{inputMessage}'
        def scriptPath = 'res:/resources/scripts/myscript.ftl'

        Module mut = builder.module () {
            map {
                simple_uri uri
                to_script scriptPath
            }
        }

        String xml = mut.buildModuleXml()

        assertFalse("Endpoints list empty", mut.endpoints.isEmpty())

        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:freemarker identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:freemarker/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*<script>/ +
                scriptPath).find())

        assertTrue("Endpoint internal space doesn't contain freemarker language import",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:freemarker/).find())

    }

    void testHandleUnknownLanguageException() {
        def uri = 'res:/responseMessage/{inputMessage}'
        def scriptPath = 'res:/resources/scripts/myscript.dontknow'

        Module mut = builder.module () {
            map {
                simple_uri uri
                to_script scriptPath
            }
        }

        shouldFail {
            String xml = mut.buildModuleXml()
        }
    }

    void testAddMultipleEndPoints() {
        def expectedUri1 = 'res:/test1/{arg1}'
        def expectedScriptPath1 = 'res:/resources/scripts/mygroovy.gy'
        def expectedUri2 = 'res:/test2/{arg2}'
        def expectedScriptPath2 = 'res:/resources/scripts/myjs.js'
        def expectedUri3 = 'res:/test2/{arg3}'
        def expectedScriptPath3 = 'res:/resources/scripts/mygroovy2.groovy'

        Module mut = builder.module() {
            map {
                simple_uri expectedUri1
                to_script expectedScriptPath1
            }
            map {
                simple_uri expectedUri2
                to_script expectedScriptPath2
            }
            map {
                simple_uri expectedUri3
                to_script expectedScriptPath3
            }
        }

        String xml = mut.buildModuleXml()

        assertEquals("Did not find 3 endpoints", 3, (xml.replace('\n', ' ') =~ /<endpoint>/).size())
        assertEquals("Did not find 1 inner space", 1, (xml.replace('\n', ' ') =~ /<space>/).size())
        assertTrue("No endpoints contain simple grammar 1",
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                expectedUri1.replace('{', '\\{').replace('}', '\\}')).find())
    }



// test for dpml



// test for generated documentation


// test for XUnit tests


// tests for active grammars


// tests for semantic errors


// (tests for "standard" grammars?)


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
//    expose {
//          filePath
//          via_rewrite
//    }
//    map {
//        simple_uri uri
//        to_groovy scriptPath
//    }
//}