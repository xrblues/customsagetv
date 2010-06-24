package bmt;

import org.jdna.metadataupdater.Version;

import sagex.phoenix.configuration.Group;

public class api {
    static {
        // TODO: Later, we need to initialize bmt in one location
        BMT.init();
    }
    
    
    public static String GetVersion() {
        return Version.VERSION;
    }
    
    @Deprecated
    public static void InstallBMTPlugin() {
    }

    @Deprecated
    public static void RemoveBMTPlugin() {
    }
    
    @Deprecated
    public static void SetBMTPluginInstalled(boolean on) {
    }
    
    @Deprecated
    public static boolean IsBMTPluginInstalled() {
    	return false;
    }
    
    /**
     * Return the configuration metadata for configuring BMT on demand options.
     * Using this node you can navigate the configuration elements.
     *
     * Refer to the Phoenix 
     * @return
     */
    public static Group GetBMTConfiguration() {
    	return null;
    }
}
