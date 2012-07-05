package zynx.flunk

/**
 * Created with IntelliJ IDEA.
 * User: pair
 * Date: 7/3/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
class Endpoint {
    String grammar
    String groovyScript = ""
    String scriptPath = ''

    List<String> getArguments() {
        grammar.findAll(~/\{(.*?)\}/, { outer, inner -> inner })
    }

    String getLanguage() {
        switch (scriptPath) {
            case ~/.*\.js/:
                return 'javascript'
            case ~/.*\.(groovy|gy)/:
                return 'groovy'
            default:
                throw new Exception("Script path '$scriptPath' is either empty or it references unknown language")
        }
    }
}
