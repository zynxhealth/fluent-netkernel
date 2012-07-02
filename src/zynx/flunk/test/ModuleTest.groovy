package zynx.flunk.test

import zynx.flunk.NetKernelBuilder

class ModuleTest extends GroovyTestCase {
    private NetKernelBuilder builder;
    final private String expectedName = 'Test Module'
    final private String expectedUri = 'urn.com.zynx.test'
    final private String expectedVersion = '1.0.0'

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
}
