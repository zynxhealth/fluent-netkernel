package zynx.flunk

class Generate {
    public static void main(String[] args) {
        WhatToMake what = WhatToMake.Module

        if (args.length < 1) {
            System.out.print 'Error: missing module file to be generated\n'
            return
        }

        if (args.length > 1) {
            switch (args[1]) {
                case 'module':
                    what = WhatToMake.Module;
                    break
                case 'testlist':
                    what = WhatToMake.TestList;
                    break
                case 'testreferences':
                    what = WhatToMake.TestReferences;
                    break;
            }
        }

        def filePath = args[0]
        File inFile = new File(filePath)

        String module = 'import zynx.flunk.*\nNetKernelBuilder builder = new NetKernelBuilder()\ndef config = builder.'

        inFile.readLines().each {
            module = module + it + '\n'
        }
        module = module + 'return config'

        GroovyShell shell = new GroovyShell()
        def config = shell.evaluate(module)

        String response
        switch (what) {
            case WhatToMake.Module:
                response = results.buildModuleXml();
                break
            case WhatToMake.TestList:
                response = results.buildTestListXml();
                break
            case WhatToMake.TestReferences:
                response = results.buildTestReferencesXml();
                break
        }

        println response
    }
}
