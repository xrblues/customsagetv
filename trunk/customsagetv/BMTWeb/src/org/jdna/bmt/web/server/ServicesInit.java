package org.jdna.bmt.web.server;

import org.apache.log4j.BasicConfigurator;

import sagex.SageAPI;
import sagex.phoenix.Phoenix;

public class ServicesInit {
    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            initialized = true;

            if (SageAPI.isRemote()) {
                BasicConfigurator.configure();
                System.out.println("*** USING TEST PHOENIX DIR ****");
                System.setProperty("phoenix/homeDir", "testing/Phoenix/");
            }
            
            try {
                // force load the configuration metadata
                Phoenix.getInstance().getConfigurationMetadataManager();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}
