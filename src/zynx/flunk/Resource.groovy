package zynx.flunk

class Resource {
    String identifier
    Boolean varArgs
    String uri

    String scriptPath

    List<Argument> arguments = []
    Sequence sequence

    void initializeResource(String identifier) {
        this.identifier = identifier
        if (identifier.startsWith('res:/')) {
            uri = identifier
        }
    }

    List<Argument> getArguments() {
        if (uri) {
            uri.findAll(~/\{(.*?)\}/, {outerGroup, innerGroup -> innerGroup }).each {argumentName ->
                addArgument(new Argument(name: argumentName))
            }
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
