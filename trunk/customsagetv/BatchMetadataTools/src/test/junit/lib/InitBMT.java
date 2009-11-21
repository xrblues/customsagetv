package test.junit.lib;

import java.io.File;

import javax.swing.plaf.basic.BasicLookAndFeel;

import org.apache.log4j.BasicConfigurator;

import junit.framework.TestCase;
import sagex.SageAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.ConfigurationMetadataManager;
import sagex.phoenix.configuration.XmlMetadataProvider;
import sagex.stub.StubSageAPI;

public class InitBMT extends TestCase {
    private static StubSageAPI api = null;
    public static void initBMT() throws Exception {
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
