package zynx.flunk

class Request {
    String identifier
    List<Argument> arguments = []
    List<Import> imports = []
    String resourcePath
    boolean varArgs

    void processScript(String scriptPath) {
        String language = getLanguage(scriptPath)
        resourcePath = scriptPath

        identifier = "active:$language"
        arguments << new Argument(name: "operator", value: scriptPath)
        imports << new Import(uri: "urn:org:netkernel:lang:$language")
    }

    private String getLanguage(scriptPath) {
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
