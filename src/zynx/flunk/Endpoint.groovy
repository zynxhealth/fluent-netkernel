package zynx.flunk

class Endpoint {
    String grammar
    String groovyScript = ""
    String scriptPath = ''

    List<Argument> args = []

    List<Argument> getArguments() {
        String result
        Argument arg

        for (it in grammar.findAll(~/\{(.*?)\}/, {outer, inner -> inner })) {
            args << new Argument(name: it)
        }
        return args
    }

    String getLanguage() {
        switch (scriptPath) {
            case ~/.*\.js/:
                return 'javascript'
            case ~/.*\.(groovy|gy)/:
                return 'groovy'
            case ~/.*\.(ftl)/:
                return 'freemarker'
            default:
                throw new Exception("Script path '$scriptPath' is either empty or it references unknown language")
        }
    }
}
