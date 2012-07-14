package zynx.flunk

class Resource {
    String identifier
    Boolean varArgs = false

    List<Argument> arguments = []

    List<Argument> getArguments() {
//        grammar.findAll(~/\{(.*?)\}/, {outerGroup, innerGroup -> innerGroup }).each {argumentName ->
//            addArgument(new Argument(name: argumentName))
//        }
        return arguments
    }

    void addArgument(Argument arg) {
        if (!arguments.find {it.name == arg.name}) {
            arguments << arg
        }
    }

    public void setAttributes(Map attrs) {
        attrs.each { key, value ->
            switch (key) {
                case 'identifier':
                    identifier = value
                    break

                default:
                    identifier = value
                    break
        }
    }
}
}
