package test.junit;

import java.io.File;

import sagex.SageAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.ConfigurationMetadataManager;
import sagex.phoenix.configuration.XmlMetadataProvider;
import sagex.stub.StubSageAPI;
import junit.framework.TestCase;

public class InitBMT extends TestCase {
    private static StubSageAPI api = null;
    public static void initBMT() throws Exception {
        if (api==null) {
            api = new StubSageAPI();
            api.setDebugCalls(false);
            SageAPI.setProvider(api);
            System.setProperty("phoenix/homeDir", "target/junit/Phoenix/");
    
            ConfigurationMetadataManager cmm = Phoenix.getInstance().getConfigurationMetadataManager();
            XmlMetadataProvider md = new XmlMetadataProvider(new File("resources/config/bmt.xml"));
            cmm.addMetadata(md);
            
            System.out.println("**** Initialized the BMT Environment for Testing ****");
        }
    }
}
