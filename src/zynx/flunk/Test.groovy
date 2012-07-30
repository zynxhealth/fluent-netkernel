package zynx.flunk

class Test {
    String name
    Request request
    List<Resource> mocks = []
    List<Assert> asserts = []

    public void setAttributes(Map attrs) {
        attrs.each { key, value ->
            switch (key) {
                case 'name':
                    name = value
                    break
            }
        }
    }
}
