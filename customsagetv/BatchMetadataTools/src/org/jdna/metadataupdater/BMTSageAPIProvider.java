package org.jdna.metadataupdater;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.util.PropertiesUtils;
import org.jdna.util.SortedProperties;

import sagex.ISageAPIProvider;
import sagex.SageAPI;

public class BMTSageAPIProvider implements ISageAPIProvider {
    private static final Logger log      = Logger.getLogger(BMTSageAPIProvider.class);
    private Properties          props    = new SortedProperties();
    private ISageAPIProvider remoteAPI = null;
    private boolean remoteAPIEnabled = false;

    public BMTSageAPIProvider() {
        File f = new File("Sage.properties");
        if (!f.exists()) {
            f = new File("SageClient.properties");
        }
        if (f.exists()) {
            log.info("Loading Properties from: " + f.getAbsolutePath());
            try {
                PropertiesUtils.load(props, f);
            } catch (IOException e) {
                log.error("Failed to load Properties from: " + f.getAbsolutePath(), e);
            }
        } else {
            log.warn("No Sage Properties found, using defaults.");
        }
    }

    public Object callService(String name, Object[] args) throws Exception {
        return callService(null, name, args);
    }

    public Object callService(String ctx, String cmd, Object[] args) throws Exception {
        if (remoteAPIEnabled) {
            if (remoteAPI==null) {
                remoteAPI = SageAPI.getRemoteProvider();
            }
            return remoteAPI.callService(cmd, args);
        } else {
            if ("GetProperty".equals(cmd) || "GetServerProperty".equals(cmd)) {
                String prop = props.getProperty((String) args[0], (String) (args.length > 1 ? args[1] : null));
                if (prop != null) {
                    props.setProperty((String) args[0], prop);
                }
                log.debug("GetProperty; Key: " + args[0] + "; Value: " + prop);
                return prop;
            } else if ("SetProperty".equals(cmd) || "SetServerProperty".equals(cmd)) {
                props.setProperty((String) args[0], (String) args[1]);
                log.debug("SetProperty; Key: " + args[0] + "; Value: " + args[1]);
                return null;
            }
        }
        
        log.debug("UnHandled Sage API Call: " + cmd + "; Args: [" + StringUtils.join(args, ", ") + "]");
        return null;
    }

    public void enableRemoteAPI(boolean b) {
        this.remoteAPIEnabled  = b;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[BMT Command Line Impl]";
    }
}
