package zynx.flunk

class Argument {
    String name
    String passBy

    public void setAttributes(Map attrs) {
        attrs.each { key, value ->
            switch (key) {
                case 'name':
                    name = value
                    break
                case 'passBy':
                    passBy = value
                    break
            }
        }
    }
}
