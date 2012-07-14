package zynx.flunk

class Argument {
    String name
    String passBy
    String min = 1
    String max = 1

    public void setAttributes(Map attrs) {
        attrs.each { key, value ->
            switch (key) {
                case 'name':
                    name = value
                    break
                case 'pass_by':
                    passBy = value
                    break
                case 'min':
                    min = value
                    break
                case 'max':
                    max = value
                    break
            }
        }
    }
}
