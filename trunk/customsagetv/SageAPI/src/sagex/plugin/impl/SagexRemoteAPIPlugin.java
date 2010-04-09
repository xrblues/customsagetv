package sagex.plugin.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import sage.SageTVPlugin;
import sage.SageTVPluginRegistry;
import sagex.plugin.AbstractPlugin;
import sagex.plugin.ConfigValueChangeHandler;
import sagex.remote.rmi.SageRMIServer;

public class SagexRemoteAPIPlugin extends AbstractPlugin {
    public static SagexRemoteAPIPlugin pluginInstance = null;
    
    public static SagexRemoteAPIPlugin getPluginInstance() {
        return pluginInstance;
    }
    
    public SagexRemoteAPIPlugin(SageTVPluginRegistry registry) {
        super(registry);

        addProperty(SageTVPlugin.CONFIG_BOOL, SagexConfiguration.PROP_ENABLE_RMI, "true", "Enable RMI Remote API", "Allows you to access the SageTV server remotely using the sagex remote apis over Java/RMI");
        addProperty(SageTVPlugin.CONFIG_INTEGER, SagexConfiguration.PROP_RMI_PORT, "1098", "RMI Port", "Only change this if you absolutely require the RMI server to use another port.  If you do change this, then remote clients will need to be updated as well to use the correct port.").setVisibleOnSetting(
                SagexConfiguration.PROP_ENABLE_RMI);
        addProperty(SageTVPlugin.CONFIG_BOOL, SagexConfiguration.PROP_ENABLE_DISCOVERY, "true", "Enable RMI Discovery", "Enables remote clients to automatically discover SageTV Servers)").setVisibleOnSetting(
                SagexConfiguration.PROP_ENABLE_RMI);
        addProperty(SageTVPlugin.CONFIG_BOOL, SagexConfiguration.PROP_ENABLE_HTTP, "true", "Enable HTTP Restful API", "Enables the HTTP Rest API for SageTV (Note this requires the Jetty Plugin)");
        
        String defPort = "8080";
        File jfile = new File("JettyStarter.properties");
        if (jfile.exists()) {
            Properties jprops = new Properties();
            try {
                jprops.load(new FileInputStream(jfile));
                if (jprops.containsKey("jetty.port")) {
                    defPort = jprops.getProperty("jetty.port");
                }
            } catch (Throwable e) {
                log.warn("Wasn't able to load the jetty properties to discover port");
            }
        }
        
        addProperty(SageTVPlugin.CONFIG_INTEGER, SagexConfiguration.PROP_HTTP_PORT, defPort, "HTTP Port", "This value should be the same as youre Jetty HTTP Port").setVisibleOnSetting(
                SagexConfiguration.PROP_ENABLE_HTTP);
        
    }

    @ConfigValueChangeHandler(SagexConfiguration.PROP_ENABLE_RMI)
    public void onRMIEnabledChanged(String setting) {
        log.info("RMI Enabled Flag Changed: " + getConfigBoolValue(setting));
        if (getConfigBoolValue(setting)) {
            if (!SageRMIServer.getInstance().isRunning()) {
                SageRMIServer.getInstance().startServer();
            }
        } else {
            if (SageRMIServer.getInstance().isRunning()) {
                SageRMIServer.getInstance().stopServer();
            }
        }
    }

    @ConfigValueChangeHandler(SagexConfiguration.PROP_RMI_PORT)
    public void onRMIPortEnabledChanged(String setting) {
        int rmiPort = getConfigIntValue(setting);
        log.info("RMI Port Changed: " + rmiPort);
        if (SageRMIServer.getInstance().isRunning()) {
            SageRMIServer.getInstance().stopServer();
            SageRMIServer.getInstance().startServer();
        }
    }
}
