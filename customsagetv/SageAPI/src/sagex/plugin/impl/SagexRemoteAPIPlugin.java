package sagex.plugin.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        addProperty(SageTVPlugin.CONFIG_INTEGER, SagexConfiguration.PROP_RMI_PORT, "1098", "RMI Port", "Only change this if you absolutely require the RMI server to use another port.  If you do change this, then remote clients will need to be updated as well to use the correct port.")
                .setVisibleOnSetting(this,SagexConfiguration.PROP_ENABLE_RMI);
        addProperty(SageTVPlugin.CONFIG_BOOL, SagexConfiguration.PROP_ENABLE_DISCOVERY, "true", "Enable RMI Discovery", "Enables remote clients to automatically discover SageTV Servers)").setVisibleOnSetting(this, SagexConfiguration.PROP_ENABLE_RMI);
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

        addProperty(SageTVPlugin.CONFIG_INTEGER, SagexConfiguration.PROP_HTTP_PORT, defPort, "HTTP Port", "This value should be the same as youre Jetty HTTP Port (usually autodetected)").setVisibleOnSetting(this, SagexConfiguration.PROP_ENABLE_HTTP);
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
    
    @ConfigValueChangeHandler(SagexConfiguration.PROP_ENABLE_HTTP)
    public void onHTTPEnabledChanged(String setting) {
        log.info("HTTP Enabled Flag Changed: " + getConfigBoolValue(setting));
        if (getConfigBoolValue(setting)) {
            stopHTTP();
        } else {
            startHTTP();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see sagex.plugin.AbstractPlugin#start()
     */
    @Override
    public void start() {
        super.start();
        log.info("Starting sagex-api-services Plugin");
        if (getConfigBoolValue(SagexConfiguration.PROP_ENABLE_RMI)) {
            SageRMIServer.getInstance().startServer();
        }
        startHTTP();
    }
    
    private void startHTTP() {
        if (getConfigBoolValue(SagexConfiguration.PROP_ENABLE_HTTP)) {
            String ctx = "jetty/contexts/sagex.xml";
            File f = new File(ctx);
            if (f.exists()) {
                log.info("sagex.xml context exists... no action.");
            } else {
                InputStream is = null;
                is = this.getClass().getClassLoader().getResourceAsStream(ctx);
                if (is != null) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(f);
                        byte buf[] = new byte[1024];
                        int i =0;
                        while ((i=is.read(buf))!=0) {
                            fos.write(buf, 0, i);
                        }
                    } catch (FileNotFoundException e) {
                        log.error("failed to install the sagex context", e);
                    } catch (IOException e) {
                        log.error("failed while reading/write the context file", e);
                    } finally {
                        if (fos != null) {
                            try {
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                            }
                        }
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }
                } else {
                    log.warn("Failed to read the sagex jetty context: " + ctx + " from the sagex-api jar");
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see sagex.plugin.AbstractPlugin#stop()
     */
    @Override
    public void stop() {
        super.stop();
        log.info("Stopping sagex-api-services Plugin");
        if (getConfigBoolValue(SagexConfiguration.PROP_ENABLE_RMI)) {
            SageRMIServer.getInstance().stopServer();
        }
    }
    
    private void stopHTTP() {
        if (getConfigBoolValue(SagexConfiguration.PROP_ENABLE_HTTP)) {
            File f = new File("jetty/contexts/sagex.xml");
            if (f.exists()) {
                if (!f.delete()) {
                    log.warn("was unable to remove the sagex context: " + f);
                }
            }
        }
    }
}
