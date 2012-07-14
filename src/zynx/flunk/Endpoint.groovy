package zynx.flunk

class Endpoint {

    String getSimpleGrammar() {
        if (!simpleGrammar && resource && resource.identifier.startsWith('res:/'))  {
            simpleGrammar = resource.identifier
        }

        return simpleGrammar
    }

    void setSimpleGrammar(String simpleGrammar) {
        this.simpleGrammar = simpleGrammar
    }

    String simpleGrammar
    String groovyScript = ""
    String scriptPath = ''

    Resource resource
    List<Argument> arguments = []

    List<Argument> getArguments() {
        if (simpleGrammar) {
            simpleGrammar.findAll(~/\{(.*?)\}/, {outerGroup, innerGroup -> innerGroup }).each {argumentName ->
                addArgument(new Argument(name: argumentName))
            }
        }
        else if (resource) {
            arguments = resource.getArguments()
        }

        return arguments
    }

    void addArgument(Argument arg) {
        if (!arguments.find {it.name == arg.name}) {
            arguments << arg
        }
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
