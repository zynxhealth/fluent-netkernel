package zynx.flunk

class Generate {

    void Main(String[] args) {
        def filePath = args[0]
        File inFile = new File(filePath)

        String module = 'import zynx.flunk.*\nNetKernelBuilder builder = new NetKernelBuilder()\nModule module = builder.'

        inFile.readLines().each {
            module = module + it + '\n'
        }
        module = module + 'return module'

        GroovyShell shell = new GroovyShell()
        def results = shell.evaluate(module)

        String response = results.buildModuleXml()

        System.out.print response
    }
}
