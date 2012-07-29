package zynx.flunk

class Resource {
    String identifier
    boolean varArgs
    String uri

    List<Argument> arguments = []
    Sequence sequence
    Request request

    void setIdentifier(String identifier) {
        this.identifier = identifier

        if (identifier.startsWith('res:/')) {
            this.uri = identifier
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
}
