package zynx.flunk.test

import zynx.flunk.NetKernelBuilder
import zynx.flunk.Module
import java.util.regex.Pattern

class ModuleTest extends GroovyTestCase {
    private NetKernelBuilder builder;
    final private String expectedName = 'Test Module'
    final private String expectedUri = 'urn.com.zynx.test'
    final private String expectedVersion = '1.0.0'
    final private String expectedDescription = 'Test Module Description'

    protected void setUp() {
        builder = new NetKernelBuilder()
    }

    void testModuleHasUriAndVersion() {
        def mut = builder.module(uri : expectedUri, version: expectedVersion)
        assertEquals expectedUri, mut.uri
        assertEquals expectedVersion, mut.version
    }

    void testModuleBuildsModuleFile() {
        def mut = builder.module(uri: expectedUri)
        String xml = mut.buildModuleXml()
        assert xml.replace('\n', ' ') =~ (/<module.*>.*<meta.*>.*<identity.*>.*<uri.*> */ + expectedUri)
    }

    void testModuleHasName() {
        def mut = builder.module(name: expectedName)
        String xml = mut.buildModuleXml()
        assert xml.replace('\n', ' ') =~ (/<module.*>.*<meta.*>.*<info.*>.*<name.*> */ + expectedName)
    }

    void testModuleHasDescription() {
        def mut = builder.module(description: expectedDescription)
        String xml = mut.buildModuleXml()
        assert xml.replace('\n', ' ') =~ (/<module.*>.*<meta.*>.*<info.*>.*<description.*> */ + expectedDescription)
    }

    void testModuleLoadedDynamicallyByDefault() {
        def mut = builder.module()
        String xml = mut.buildModuleXml();
        assert xml.replace('\n', ' ') =~ /<system.*>.*<dynamic/
    }

    void testModuleHasRootSpace() {
        def mut = builder.module()
        String xml = mut.buildModuleXml();
        assert xml =~ /<rootspace.*>/
    }

    void testModuleExposedToHttp() {
        def mut = builder.module {
            expose_to 'http'
        }
        String xml = mut.buildModuleXml();
        assert xml.replace('\n', ' ') =~ /<fileset.*>.*<regex.*>.*res:\/etc\/system\/SimpleDynamicImportHook.xml/
    }

    void testFilesCanBeExposedAsResources() {
        def expected_path = 'res:/extjs/contenteditor/.*/app/(.*)'

        Module mut = builder.module() {
            expose {
                file_path expected_path
            }
        }

        def xml = mut.buildModuleXml()

        assertTrue("Rootspace doesn't contain expected fileset",
                (xml.replace('\n', ' ') =~
                /<rootspace.*>.*<fileset.*>.*<regex>/ + Pattern.quote(expected_path)).find())
    }

    void testFilesCanBeExposedWithRewrites() {
        def expected_path = 'res:/extjs/contenteditor/.*/app/(.*)'
        def expected_rewrite = 'res:/resources/extjs-app/app/$1'

        Module mut = builder.module() {
            expose {
                file_path expected_path
                via_rewrite expected_rewrite
            }
        }

        def xml = mut.buildModuleXml()

        assertTrue("Rootspace doesn't contain expected fileset",
                (xml.replace('\n', ' ') =~
                /<rootspace.*>.*<fileset.*>.*<regex>/ + Pattern.quote(expected_path)).find())

        assertTrue("Fileset doesn't contain rewrite",
                (xml.replace('\n', ' ') =~
                /<rootspace.*>.*<fileset.*>.*<rewrite.*>/ + Pattern.quote(expected_rewrite)).find())
    }

}

