module(uri: 'urn:flunk:sample:module', name: 'Flunk sample module', version: '1.0.0') {

    // hook rootspace into front-end fulcrum
    expose_to 'http'

    // create separate rootspaces for implementation and execution
    isolate_dependencies true

    // add simple fileset
    expose {
        file_path 'res:/get-doc-file/(.*)'
        via_rewrite 'res:/resources/doc/$1'
    }
    /* code above results in
    <fileset>
        <regex>res:/get-doc-file/(.*)</regex>
        <rewrite>res:/resources/doc/$1</rewrite>
    </fileset>
     */

    // add endpoint with simple grammar, implemented via a script
    expose {
        resource('res:/test/hello/{name}')
        use_script('res:/resources/scripts/sayHello.ftl') {
            with_argument(name: 'name', value: '[[arg:name]]', pass_by: 'value')
        }
    }
    /* code above results in
    <mapper>
        <config>
            <endpoint>
                <grammar>
                    <simple>res:/test/hello/{name}</simple>
                </grammar>
                <request>
                    <identifier>active:freemarker</identifier>
                    <argument name='operator'>
                        <script>res:/resources/scripts/sayHello.ftl</script>
                    </argument>
                    <argument name='name' method='as-string'>[[arg:name]]</argument>
                </request>
            </endpoint>
        </config>
       <space>
            <import>
                <uri>urn:org:netkernel:lang:freemarker</uri>
            </import>
       </space>
    </mapper>

     */

    // add endpoint with active grammar and two arguments, implemented via a script which also takes two arguments
    expose {
        resource('active:addTwoNumbers') {
            with_argument(name: 'firstNumber', min: 1, max: 1)
            with_argument(name: 'secondNumber')
        }
        use_script('res:/resources/scripts/addTwoNumbers.groovy') {
            with_argument(name: 'first', value: '[[arg:firstNumber]]')
            with_argument(name: 'second', value: '[[arg:secondNumber]]')
        }
    }
    /* code above results in
   <mapper>
       <config>
           <endpoint>
               <grammar>
                   <active>active:addTwoNumbers</active>
                   <argument name='firstNumber' min=1 max=1 />
                   <argument name='secondNumber' />
               </grammar>
               <request>
                   <identifier>active:groovy</identifier>
                   <argument name='operator'>
                       <script>res:/resources/scripts/addTwoNumbers.groovy</script>
                   </argument>
                   <argument name='first'>[[arg:firstNumber]]</argument>
                   <argument name='second'>[[arg:secondNumber]]</argument>
               </request>
           </endpoint>
       </config>
       <space>
            <import>
                <uri>urn:org:netkernel:lang:groovy</uri>
            </import>
       </space>
   </mapper>
    */

    // add endpoint with active grammar and no arguments, which makes request to another resource
    expose {
        resource('active:getGoogleFrontPage') {
            make_request_to('active:httpGet') {
                with_argument(name: 'url', value: 'http://google.com')
                defined_in 'urn:org:netkernel:client:http'
            }
        }
    }
    /* code above results in
   <mapper>
       <config>
           <endpoint>
               <grammar>
                   <active>active:getGoogleFrontPage</active>
               </grammar>
               <request>
                   <identifier>active:httpGet</identifier>
                   <argument name='url'>http://google.com</argument>
               </request>
           </endpoint>
       </config>
       <space>
            <import>
                <uri>urn:org:netkernel:client:http</uri>
            </import>
       </space>
   </mapper>
    */

    // add endpoint with active grammar and one argument, which makes request to another resource
    expose {
        resource('active:numberMultipliedByTen') {
            with_argument(name: 'operand')

            make_request_to('active:calculate') {
                with_argument(name: 'operator', value: '[x*10]')
                with_argument(name: 'x', value: '[[arg:operand]]')
                defined_in 'urn:org:netkernel:lang:math'
            }
        }
    }
    /* code above results in
   <mapper>
       <config>
           <endpoint>
               <grammar>
                   <active>active:numberMultipliedByTen</active>
                   <argument name='operand' />
               </grammar>
               <request>
                   <identifier>active:calculate</identifier>
                   <argument name='operator'>[x*10]</argument>
                   <argument name='x'>[[arg:operand]]</argument>
               </request>
           </endpoint>
       </config>
       <space>
            <import>
                <uri>urn:org:netkernel:lang:math</uri>
            </import>
       </space>
   </mapper>
    */
}