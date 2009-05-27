package org.jdna.metadataupdater;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.util.SortedProperties;

import sagex.ISageAPIProvider;
import sagex.SageAPI;

public class BMTSageAPIProvider implements ISageAPIProvider {
    private static final Logger log      = Logger.getLogger(BMTSageAPIProvider.class);
    private Properties          props    = new SortedProperties();
    private File                propFile = new File("bmt-commandline.properties");
    private ISageAPIProvider remoteAPI = null;
    private boolean remoteAPIEnabled = false;

    public BMTSageAPIProvider() {
    }

    public Object callService(String name, Object[] args) throws Exception {
        return callService(null, name, args);
    }

    public Object callService(String ctx, String cmd, Object[] args) throws Exception {
        if ("GetProperty".equals(cmd) || "GetServerProperty".equals(cmd)) {
            String prop = props.getProperty((String) args[0], (String) (args.length > 1 ? args[1] : null));
            if (prop != null) {
                props.setProperty((String) args[0], prop);
            }
            log.debug("GetProperty; Key: " + args[0] + "; Value: " + prop);
            return prop;
        }

        if ("SetProperty".equals(cmd) || "SetServerProperty".equals(cmd)) {
            props.setProperty((String) args[0], (String) args[1]);
            log.debug("SetProperty; Key: " + args[0] + "; Value: " + args[1]);
            return null;
        }

        if ("SaveProperties".equals(cmd)) {
            log.debug("Saving Properties");
            FileWriter fw = null;
            try {
                props.store(fw, "BMT-CommandLine Properties");
            } finally {
                if (fw != null) {
                    fw.flush();
                    fw.close();
                }
            }
            return null;
        }

        if (remoteAPIEnabled) {
            if (remoteAPI==null) {
                remoteAPI = SageAPI.getRemoteProvider();
            }
            return remoteAPI.callService(cmd, args);
        }
        
        log.debug("UnHandled Sage API Call: " + cmd + "; Args: [" + StringUtils.join(args, ", ") + "]");
        return null;
    }

    public void enableRemoteAPI(boolean b) {
        this.remoteAPIEnabled  = b;
    }
    
    public String toString() {
        return this.getClass().getName() + "[BMT Command Line Impl]";
    }
}
