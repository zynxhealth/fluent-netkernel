package zynx.flunk

enum WhatToMake {MODULE, TESTLIST}

class Generate {

    public static void main(String[] args) {
        WhatToMake what = WhatToMake.MODULE

        if(args.length < 1)
        {
            System.out.print 'Error: missing module file to be generated\n'
            return
        }

        if (args.length > 1) {
            switch (args[1]) {
                case 'module': what = WhatToMake.MODULE; break
                case 'testlist': what = WhatToMake.TESTLIST; break
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
        def results = shell.evaluate(module)

        String response
        switch (what) {
            case WhatToMake.MODULE: response = results.buildModuleXml(); break
            case WhatToMake.TESTLIST: response = results.buildTestListXml(); break
        }

        System.out.print response
    }
}
