package test.junit;

import java.io.IOException;

import junit.framework.TestCase;

import org.jdna.configuration.BMTConfigurationMetadataProvider;
import org.jdna.url.UrlConfiguration;

import sagex.SageAPI;
import sagex.api.Configuration;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.ConfigurationManager;
import sagex.phoenix.configuration.ConfigurationMetadataManager;
import sagex.phoenix.configuration.Field;
import sagex.phoenix.configuration.Group;
import sagex.phoenix.configuration.IConfigurationElement;
import sagex.phoenix.configuration.IConfigurationMetadataVisitor;
import sagex.stub.StubSageAPI;

public class BMTMetadataTestCase extends TestCase {

    public BMTMetadataTestCase() {
    }

    public BMTMetadataTestCase(String name) {
        super(name);
    }

    public void testFieldProxy() {
        StubSageAPI api = new StubSageAPI();
        api.setDebugCalls(false);
        SageAPI.setProvider(api);
        Configuration.SetProperty("phoenix/homeDir", "target/junit/Phoenix/");

        performObjectTest();

        ConfigurationManager cm = Phoenix.getInstance().getConfigurationManager();
        assertTrue("There is no metadata, Should be a String", cm.getClientProperty("bmt/urlconfiguration/cacheExpiryInSeconds", "45") instanceof String);
    }
    
    private void performObjectTest() {
        ConfigurationManager cm = Phoenix.getInstance().getConfigurationManager();
        
        UrlConfiguration c = new UrlConfiguration();
        assertNotNull(c);
        assertNotNull(c.getCacheDir());
        assertNotNull(c.getHttpUserAgent());
        assertNotNull(c.getUrlFactoryClass());
        assertNotSame(0, c.getCacheExpiryInSeconds());
        
        c.setCacheExpiryInSeconds(30);
        assertEquals("Set Failed (int)", 30, c.getCacheExpiryInSeconds());
        assertEquals("30", cm.getClientProperty("bmt/urlconfiguration/cacheExpiryInSeconds", "45"));
        
        c.setCacheDir("tmp");
        assertEquals("Set Failed (String)", "tmp", c.getCacheDir() );
        assertEquals("tmp", cm.getClientProperty("bmt/urlconfiguration/cacheDir", "tmpXXX"));
        
        cm.setClientProperty("bmt/urlconfiguration/cacheExpiryInSeconds", "4800");
        assertEquals(4800,c.getCacheExpiryInSeconds());

        cm.setClientProperty("bmt/urlconfiguration/cacheDir", "/tmp/tmp/");
        assertEquals("/tmp/tmp/",c.getCacheDir());
    }

    public void testBMTMetadata() throws Exception {
        StubSageAPI api = new StubSageAPI();
        api.setDebugCalls(false);
        SageAPI.setProvider(api);
        Configuration.SetProperty("phoenix/homeDir", "target/junit/Phoenix/");
        
        ConfigurationManager cm = Phoenix.getInstance().getConfigurationManager();
        ConfigurationMetadataManager cmm = Phoenix.getInstance().getConfigurationMetadataManager();
        
        BMTConfigurationMetadataProvider md = new BMTConfigurationMetadataProvider();
        Group root[];
        try {
            root = md.load();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not load BMT Metadata");
            return;
        }
        assertEquals(1, root.length);
        Group main = root[0];
        main.visit(new IConfigurationMetadataVisitor() {
            public void accept(IConfigurationElement el) {
                System.out.println(el.getId());
                assertNotNull("Missing Label: " + el, el.getLabel());
                assertNotNull("Missing ID: " + el.getLabel(), el.getId());
                assertNotNull("Missing Description: " + el.getLabel(), el.getDescription());
            }
        });
        assertEquals("Child count does not match.  If you've added new child groups to the configuration, the update the test case.", 8, main.getChildren().length);
        
        cmm.addMetadata(md);
        Field el = (Field) cmm.getConfigurationElement("bmt/urlconfiguration/cacheExpiryInSeconds");
        assertNotNull(el);
        assertEquals("int", el.getType());
        
        assertEquals("There is metadata, Should be a Integer", Integer.class, cm.getClientProperty("bmt/urlconfiguration/cacheExpiryInSeconds", "45").getClass());
        
        el = (Field) cmm.getConfigurationElement("phoenix/mediametadata/fanartEnabled");
        assertNotNull(el);
        assertEquals("boolean", el.getType());
    }
}
