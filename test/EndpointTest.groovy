import zynx.flunk.Module
import zynx.flunk.NetKernelBuilder
import zynx.flunk.Argument
import zynx.flunk.Resource
import groovy.xml.MarkupBuilder

class EndpointTest extends GroovyTestCase {
    private NetKernelBuilder builder

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

        println xml
        assertTrue("Endpoint doesn't contain simple grammar",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>/ +
                uri.replace('{', '\\{').replace('}', '\\}')).find())
    }

    void testSpecifyActiveGrammar(){
        def myResource = 'active:myResource'
        def arg1 = 'arg1'
        def arg2 = 'arg2'

        Module mut = builder.module(name:'name') {
            expose {
                resource (myResource) {
                    with_argument (name: arg1, min: 2, max: 3)
                    with_argument (name: arg2)
                    with_varargs true
                }
            }
        }

        String xml = mut.buildModuleXml()

        println xml

        assertTrue("Endpoint doesn't contain identifer for active grammar",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<identifier>/ + myResource).find())
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
        def myResource = 'active:myResource'
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

        assertTrue("Endpoint doesn't contain identifer for active grammar",
                (xml.replace('\n', ' ') =~
                        /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<identifier>/ + myResource).find())
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

    void testCreateRequestFromResource() {
        def myResource = 'active:myResource'
        def anotherResource = 'active:anotherResource'
        def resourceSpace = 'urn:another:resource:space'

        Module mut = builder.module () {
            expose {
                resource (myResource) {
                    make_request_to (anotherResource) {
                        defined_in resourceSpace
                    }
                }
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Endpoint doesn't contain identifer for active grammar",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<active*>.*<identifier>/ + myResource).find())

        assertTrue("Endpoint doesn't contain request to resource $anotherResource",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>/ + anotherResource).find())

        assertTrue("Mapper doesn't contain import for space $resourceSpace",
                (xml.replace('\n', ' ') =~
                /<mapper.*>.*<space.*>.*<import.*>.*<uri>/ + resourceSpace).find())
    }

    void testCreateRequestWithArguments() {
        def myResource = 'active:myResource'
        def anotherResource = 'active:anotherResource'
        def resourceSpace = 'urn:another:resource:space'
        def argName = 'myArgument'
        def argValue = 'argValue'

        Module mut = builder.module () {
            expose {
                resource (myResource) {
                    make_request_to (anotherResource) {
                        with_argument (name: argName, value: argValue)
                        defined_in resourceSpace
                    }
                }
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Endpoint doesn't contain request to resource $anotherResource",
                (xml.replace('\n', ' ') =~
                        /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>/ + anotherResource).find())

        assertTrue("Request doesn't contain argument $argName",
                (xml.replace('\n', ' ') =~
                        /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='/ + argName + /'>/).find())

        assertTrue("Argument $argName doesn't contain value $argValue",
                (xml.replace('\n', ' ') =~
                        /<request>.*<argument.*>/ + argValue).find())


    }

//    void testCreateRequestToGroovyScript() {
//        def myResource = 'active:myResource'
//        def anotherResource = 'active:anotherResource'
//        def myScript = 'res:/resources/scripts/myGroovy.groovy'
//
//        Module mut = builder.module () {
//            expose {
//                resource (myResource) {
//                    make_request_to {
//                        use_script myScript
//                    }
//                }
//            }
//        }
//
//        String xml = mut.buildModuleXml()
//
//        assertTrue("Endpoint doesn't contain request to resource $anotherResource",
//                (xml.replace('\n', ' ') =~
//                        /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>/ + anotherResource).find())
//
//        assertTrue("Request doesn't contain argument $argName",
//                (xml.replace('\n', ' ') =~
//                        /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='/ + argName + /'>/).find())
//
//        assertTrue("Argument $argName doesn't contain value $argValue",
//                (xml.replace('\n', ' ') =~
//                        /<request>.*<argument.*>/ + argValue).find())
//
//
//    }


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

//        assertTrue("Endpoint request doesn't contain argument arg1",
//                (xml.replace('\n', ' ') =~
//                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg1'>\[\[arg:arg1\]\]/).find())

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

    void testExecuteGroovyScriptWithAutoGeneratedArguments() {
        def uri = 'res:/{arg1}/{arg2}/something/{arg3}/moretext/{arg4}'
        def scriptPath = 'res:/resources/scripts/myscript.gy'

        Module mut = builder.module () {
            expose {
                resource (uri)
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

//        assertTrue("Endpoint request doesn't contain argument arg1",
//               (xml.replace('\n', ' ') =~
//               /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg1'>\[\[arg:arg1\]\]/).find())
//
//        assertTrue("Endpoint request doesn't contain argument arg2",
//                (xml.replace('\n', ' ') =~
//                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg2'>\[\[arg:arg2\]\]/).find())
//
//        assertTrue("Endpoint request doesn't contain argument arg3",
//                (xml.replace('\n', ' ') =~
//                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg3'>\[\[arg:arg3\]\]/).find())
//
//        assertTrue("Endpoint request doesn't contain argument arg4",
//                (xml.replace('\n', ' ') =~
//                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request.*>.*<argument name='arg4'>\[\[arg:arg4\]\]/).find())

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
                use_script (scriptPath) {
                    with_argument (name: arg1, pass_by: 'argument-as-string', value: "[[arg:$arg1]]")
                    with_argument (name: arg2, value: "[[arg:$arg2]]")
                    with_varargs true
                }
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

        shouldFail {
            Module mut = builder.module () {
                expose {
                    resource (uri)
                    use_script scriptPath
                }
            }
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

    void testSetupImplSpace() {
        def moduleName = 'Math Functions'
        def moduleUri = 'urn:my:math:functions'
        def myResource = 'active:myResource'
        def anotherResource = 'active:anotherResource'
        def resourceSpace = 'urn:another:resource:space'

        Module mut = builder.module (name: moduleName, uri: moduleUri) {
            isolate_dependencies true

            expose {
                resource (myResource) {
                    make_request_to (anotherResource) {
                        defined_in resourceSpace
                    }
                }
            }
        }

        String xml = mut.buildModuleXml()

        assertTrue("Module doesn't contain implementation space '$moduleName - Impl'",
                (xml.replace('\n', ' ') =~
                /<rootspace name='/ + moduleName + / - Impl'/).find())

        assertTrue("Implementation space doesn't have a uri $moduleUri:impl",
                (xml.replace('\n', ' ') =~
                /<rootspace name='/ + moduleName + / - Impl'.*uri='/ + moduleUri + /:impl'/).find())

        assertTrue("Module doesn't contain execution space '$moduleName'",
                (xml.replace('\n', ' ') =~
                /<rootspace name='/ + moduleName + /'/).find())

        assertTrue("Execution space doesn't have a uri $moduleUri",
                (xml.replace('\n', ' ') =~
                /<rootspace name='/ + moduleName + /'.*uri='/ + moduleUri + /'/).find())

        assertTrue("Endpoint not found in implementation space",
                (xml.replace('\n', ' ') =~
                /<rootspace name='/ + moduleName + /'.*<mapper.*>.*<config.*>.*<endpoint.*>/).find())

        assertTrue("Import for $resourceSpace not found in execution space",
                (xml.replace('\n', ' ') =~
                /<rootspace name='/ + moduleName + /'.*<import.*>.*<uri>/ + resourceSpace).find())
    }

    void testXmlCanBePassedAsLiteral() {
        def arg = 'baz'
        def uri = "res:/foobar"
        def scriptPath = 'res:/resources/foobar.groovy'
        def xmlBuilding =
            { it.foo
                    { bar() }
            }

        Module mut = builder.module () {
            expose {
                resource (uri)
                use_script (scriptPath) {
                    with_argument (name: arg, pass_by: 'literal-xml', value: xmlBuilding)
                }
            }
        }

        String xml = mut.buildModuleXml().replace('\n', ' ')

        assertTrue("Argument {$arg} doesn't contain 'literal' element",
        (xml =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<argument name='/+ arg + /'.*>.*<literal/).find())

        assertTrue("Argument {$arg} literal value doesn't specify 'xml' type",
        (xml =~ /<argument name='/+ arg + /'.*>.*<literal type='xml'>/).find())

        assertTrue("Argument {$arg} literal value doesn't contain the expected XML contents",
        (xml =~ /<foo> *<bar *\/> *<\/foo>/).find())
    }


//    void testCreateSimpleDPMLSequence() {
//        def resourceName = 'res:/run-dpml/'
//        def scriptPath = 'res:/resources/scripts/toUpper.ftl'
//        def argName = 'upperCaseMe'
//        def argValue = 'banana'
//
//        Module mut = builder.module() {
//            expose_to 'http'
//            expose {
//                resource (resourceName)
//
//                use_sequence {
//                    step ('response') {
//                        use_script scriptPath
//                        with_argument (name: argName, value: argValue)
//                    }
//                }
//            }
//        }
//
//        def xml = mut.buildModuleXml()
//
//        assertTrue("Endpoint doesn't contain simple grammar",
//                (xml.replace('\n', ' ') =~ /<mapper.*>.*<config.*>.*<endpoint.*>.*<grammar>.*<simple>.*/ + resourceName).find())
//
//        assertTrue("Endpoint doesn't contain active:dpml identifier",
//                (xml.replace('\n', ' ') =~
//                /<mapper.*>.*<config.*>.*<endpoint.*>.*<request>.*<identifier>active:dpml/).find())
//
//        assertTrue("Endpoint request doesn't contain operator argument",
//                (xml.replace('\n', ' ') =~
//                /<request>.*<argument name='operator'>.*<sequence>/).find())
//
//        assertTrue("Sequence doesn't contain request 'response'",
//                (xml.replace('\n', ' ') =~
//                /<request>.*<identifier>active:dpml.*<sequence>.*<request assignment='response'/).find())
//
//        assertTrue("Sequence request doesn't contain operator argument with script $scriptPath",
//                (xml.replace('\n', ' ') =~
//                /<sequence>.*<request assignment='response'>.*<argument name='operator'>.*/ + scriptPath).find())
//
//        assertTrue("Sequence request contain argument $argName",
//                (xml.replace('\n', ' ') =~
//                /<sequence>.*<request assignment='response'>.*<argument name='/ + argName).find())
//
//        assertTrue("Sequence request contain argument $argName with value $argValue",
//                (xml.replace('\n', ' ') =~
//                /<sequence>.*<request assignment='response'>.*<argument name='/ + argName + /.*<literal type='string'>.*/ + argValue).find())
//
//        assertTrue("Endpoint internal space doesn't contain dpml import",
//                (xml.replace('\n', ' ') =~
//                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:dpml/).find())
//
//        assertTrue("Endpoint internal space doesn't contain freemarker language import",
//                (xml.replace('\n', ' ') =~
//                /<mapper.*>.*<space.*>.*<import.*>.*<uri>urn:org:netkernel:lang:freemarker/).find())
//
//        assertTrue("Endpoint internal space doesn't contain fileset $scriptPath",
//                (xml.replace('\n', ' ') =~
//                /<mapper.*>.*<space.*>.*<fileset.*>.*<regex>/ + scriptPath).find())
//    }

}
