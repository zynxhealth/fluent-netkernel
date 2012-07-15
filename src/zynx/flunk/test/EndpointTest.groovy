package zynx.flunk.test

import zynx.flunk.Module
import zynx.flunk.NetKernelBuilder
import zynx.flunk.Argument
import zynx.flunk.Resource

class EndpointTest extends GroovyTestCase {
    private NetKernelBuilder builder;

    protected void setUp() {
        builder = new NetKernelBuilder()
    }

    void testSpecifySimpleGrammar() {
        def uri = 'res:/responseMessage/{inputMessage}'
        Module mut = builder.module() {
            expose {
                resource(uri)
            }
        }
        String xml = mut.buildModuleXml()
        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())
    }

    void testSpecifyActiveGrammar(){
        def myResource = 'myResource'
        def arg1 = 'arg1'
        def arg2 = 'arg2'

        Module mut = builder.module(name:'name') {
            expose {
                resource(myResource) {
                    with_argument (name: arg1, min: 2, max: 3)
                    with_argument (name: arg2)
                    with_variable_arguments true
                }
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Endpoint doesn't contain identifer for active grammar",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<identifier>active:/ + myResource).find())
        assertTrue("Grammar doesn't contain the argument {$arg1}" ,
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<argument name='/+ arg1 + /'/).find())
        assertTrue("Argument {$arg1} has invalid min attribute" ,
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<argument name='/+ arg1 + /'.*min='2'/).find())
        assertTrue("Argument {$arg1} has invalid max attribute" ,
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<argument name='/+ arg1 + /'.*max='3'/).find())

        assertTrue("Grammar doesn't contain the argument {$arg2}" ,
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<argument name='/+ arg2 + /'/).find())
        assertTrue("Argument {$arg2} has invalid default for max attribute" ,
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<argument name='/+ arg2 + /'.*max='1'/).find())
        assertTrue("Argument {$arg2} has invalid default for min attribute" ,
                (xml.replace('\n', ' ') =~
                        /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<argument name='/+ arg2 + /'.*max='1'/).find())

        assertTrue("Grammar doesn't contain variable arguments" ,
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<varargs/).find())

    }

    void testAssociateActiveGrammarToGroovyScript() {
        def myResource = 'myResource'
        def arg1 = 'arg1'
        def scriptPath = 'res:/resources/scripts/myscript.groovy'

        Module mut = builder.module (name:'name') {
            expose {
                resource (myResource) {
                    with_argument (name: arg1)
                }
                use_script scriptPath
            }
        }

        String xml = mut.buildModuleXml()
        println xml

        assertTrue("Endpoint doesn't contain identifer for active grammar",
                (xml.replace('\n', ' ') =~
                        /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<identifier>active:/ + myResource).find())
        assertTrue("Grammar doesn't contain the argument {$arg1}" ,
                (xml.replace('\n', ' ') =~
                        /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<argument name='/+ arg1 + /'/).find())

        assertTrue("Endpoint doesn't contain active:groovy identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:groovy/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*/ +
                scriptPath).find())

        assertTrue("Endpoint internal space doesn't contain groovy language import",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:groovy/).find())

    }

    void testUseResourceKeywordToSpecifySimpleGrammar()  {
        def arg1 = 'arg1'
        def myResource = "res:/myResource/{$arg1}"
        def scriptPath = 'res:/resources/scripts/myscript.groovy'

        Module mut = builder.module(name:'name') {
            expose {
                resource (myResource)
                use_script scriptPath
            }
        }
        String xml = mut.buildModuleXml()

        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                myResource.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:groovy identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:groovy/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*/ +
                scriptPath).find())

        assertTrue("Endpoint internal space doesn't contain groovy language import",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:groovy/).find())

        assertTrue("Endpoint request doesn't contain argument arg1",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg1'>\[\[arg:arg1\]\]/).find())

    }

    void testExecuteGroovyScript(){
        def uri = 'res:/responseMessage/{inputMessage}'
        def scriptPath = 'res:/resources/scripts/myscript.groovy'

        Module mut = builder.module () {
            expose {
                resource (uri)
                use_script scriptPath
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:groovy identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:groovy/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*/ +
                scriptPath).find())

        assertTrue("Endpoint internal space doesn't contain groovy language import",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:groovy/).find())
    }

    void testExtractArgumentsFromGrammarUri() {
        Resource rut = new Resource()
        rut.uri = 'res:/{arg1}/{arg2}/something/{arg3}/moretext/{arg4}'
        List<Argument> args = rut.getArguments()

        assertEquals("Didn't create 4 arguments", 4, args.size())
        assertTrue("Expected argument 1 not found", args.find {it.name == 'arg1' } != null)
        assertTrue("Expected argument 2 not found", args.find {it.name == 'arg2' } != null)
        assertTrue("Expected argument 3 not found", args.find {it.name == 'arg3' } != null)
        assertTrue("Expected argument 4 not found", args.find {it.name == 'arg4' } != null)
        assertTrue("Unexpected argument 5 found", args.find {it.name == 'arg5' } == null)
    }

    void testExecuteGroovyScriptWithArguments() {
        def uri = 'res:/{arg1}/{arg2}/something/{arg3}/moretext/{arg4}'
        def scriptPath = 'res:/resources/scripts/myscript.gy'

        Module mut = builder.module () {
            expose {
                resource(uri)
                use_script scriptPath
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:groovy identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:groovy/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*/ +
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

    void testSpecifyArgumentsByValue() {
        def arg1 = 'argument1'
        def arg2 = 'argument2'
        def uri = "res:/responseMessage/{$arg1}"
        def scriptPath = 'res:/resources/scripts/myscript.ftl'

        Module mut = builder.module () {
            expose {
                resource (uri)
                use_script scriptPath
                with_argument (name: arg1, pass_by: 'value')
                with_argument (name: arg2)
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*/ +
                scriptPath).find())

        assertTrue("Argument {$arg1} doesn't contain method=as-string modifier" ,
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='/+ arg1 + /' method='as-string'>/).find())

        assertTrue("Argument {$arg2} has a method=as-string modifier" ,
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='/+ arg2 + /'>/).find())

    }

    void testExecuteJavaScriptScript(){
        def uri = 'res:/responseMessage/{inputMessage}'
        def scriptPath = 'res:/resources/scripts/myscript.js'

        Module mut = builder.module () {
            expose {
                resource (uri)
                use_script scriptPath
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:javascript identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:javascript/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*/ +
                scriptPath).find())

        assertTrue("Endpoint internal space doesn't contain javascript language import",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:javascript/).find())
    }

    void testExecuteFreemarkerScript() {
        def uri = 'res:/responseMessage/{inputMessage}'
        def scriptPath = 'res:/resources/scripts/myscript.ftl'

        Module mut = builder.module () {
            expose {
                resource (uri)
                use_script scriptPath
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:freemarker identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:freemarker/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*/ +
                scriptPath).find())

        assertTrue("Endpoint internal space doesn't contain freemarker language import",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:freemarker/).find())

    }

    void testHandleUnknownLanguageException() {
        def uri = 'res:/responseMessage/{inputMessage}'
        def scriptPath = 'res:/resources/scripts/myscript.dontknow'

        Module mut = builder.module () {
            expose {
                resource (uri)
                use_script scriptPath
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
            expose {
                resource (expectedUri1)
                use_script expectedScriptPath1
            }
            expose {
                resource (expectedUri2)
                use_script expectedScriptPath2
            }
            expose {
                resource (expectedUri3)
                use_script expectedScriptPath3
            }
        }

        String xml = mut.buildModuleXml()

        assertEquals("Did not find 3 endpoints", 3, (xml.replace('\n', ' ') =~ /<endpoint>/).size())
        assertEquals("Did not find 1 inner space", 1, (xml.replace('\n', ' ') =~ /<space>/).size())
        assertTrue("No endpoints contain simple grammar 1",
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                expectedUri1.replace('{', '\\{').replace('}', '\\}')).find())
    }

    void testUseExposeKeywordToMapResource() {
        def uri = 'res:/responseMessage/{inputMessage}'
        def scriptPath = 'res:/resources/scripts/myscript.groovy'

        Module mut = builder.module () {
            expose {
                resource (uri)
                use_script scriptPath
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())

        assertTrue("Endpoint doesn't contain active:groovy identifier",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:groovy/).find())

        assertTrue("Endpoint request doesn't contain operator argument",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='operator'>.*/ +
                scriptPath).find())

        assertTrue("Endpoint internal space doesn't contain groovy language import",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:groovy/).find())
    }

    void testCreateSampleModule() {
        Module module =
            builder.module (uri: 'urn:alex:test:module', name: 'my test module', version: '1.0.0', ) {
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

        println module.buildModuleXml()
    }

// test for dpml



// test for generated documentation


// tests for semantic errors


}
