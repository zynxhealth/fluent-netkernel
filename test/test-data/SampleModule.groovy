module (uri: 'urn:flunk:testmodule', name: 'my test module', version: '1.0.0') {
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