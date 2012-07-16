package zynx.flunk

class Argument {
    String name
    String value
    String passBy
    String min = 1
    String max = 1

    public void setAttributes(Map attrs) {
        attrs.each { key, val ->
            switch (key) {
                case 'name':
                    name = val
                    break
                case 'pass_by':
                    passBy = val
                    break
                case 'min':
                    min = val
                    break
                case 'max':
                    max = val
                    break
                case 'value':
                    value = val
                    break
            }
        }
    }
}
