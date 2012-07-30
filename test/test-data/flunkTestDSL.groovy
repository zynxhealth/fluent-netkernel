tests(mutName: 'Flunk Sample Module', version: '1.0.0', mutUri: 'urn:flunk:sample:module:impl') {

    test (name: 'verify Google call') {
        make_request_to ('active:callGoogle') {
            with_argument (name: 'url', value: 'testUrl')
        }

        assert_response {
            stringEquals 'this is an httpGet mock'
            minTime '10'
            maxTime '20'
        }

        mock_resource ('active:httpGet') {
            with_argument (name:'url')
            respond_with {
                resource ('active:groovy') {

                }
            }
        }
    }
}