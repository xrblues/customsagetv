package test.junit.lib;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import sagex.SageAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.ConfigurationMetadataManager;
import sagex.phoenix.configuration.XmlMetadataProvider;
import sagex.stub.StubSageAPI;

public class InitBMT extends TestCase {
    private static StubSageAPI api = null;
    public static void initBMT() throws Exception {
        File f = new File("log4j.properties");
        if (f.exists()) {
            PropertyConfigurator.configure(f.getAbsolutePath());
        }
        
        BasicConfigurator.configure();
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
