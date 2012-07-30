module (uri: 'urn:flunk:sample:module', name: 'Flunk sample module', version: '1.0.0') {

    // hook rootspace into front-end fulcrum
    expose_to 'http'

    // create separate rootspaces for implementation and execution
    isolate_dependencies true

    // add simple fileset
    expose {
        file_path 'res:/get-doc-file/(.*)'
        via_rewrite 'res:/resources/doc/$1'
    }

    // add endpoint with simple grammar, implemented via a script
    expose {
        resource ('res:/test/hello/{name}')
        use_script 'res:/resources/scripts/sayHello.ftl' {
            with_argument (name: 'name', value: '[[arg:name]]', pass_by: 'value')
        }
    }

    // add endpoint with active grammar and two arguments, implemented via a script which also takes two arguments
    expose {
        resource ('active:addTwoNumbers') {
            with_argument (name: 'firstNumber', min: 1, max: 1)
            with_argument (name: 'secondNumber')
        }
        use_script 'res:/resources/scripts/addTwoNumbers.groovy' {
            with_argument (name: 'first', value: '[[arg:firstNumber]]')
            with_argument (name: 'second', value: '[[arg:secondNumber]]')
        }
    }

    // add endpoint with active grammar and no arguments, which makes request to another resource
    expose {
        resource ('active:getGoogleFrontPage') {
            make_request_to ('active:httpGet') {
                with_argument (name: 'url', value: 'http://google.com')
                defined_in 'urn:org:netkernel:client:http'
            }
        }
    }

    // add endpoint with active grammar and one argument, which makes request to another resource
    expose {
        resource ('active:numberMultipliedByTen') {
            with_argument (name: 'operand')

            make_request_to ('active:calculate') {
                with_argument (name: 'operator', value: '[x*10]')
                with_argument (name: 'x', value: '[[arg:operand]]')
                defined_in 'urn:org:netkernel:lang:math'
            }
        }
    }
}