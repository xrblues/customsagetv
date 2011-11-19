package sagex.plugintools;

import sage.SageTVPlugin;
import sage.SageTVPluginRegistry;
import sagex.plugin.AbstractPlugin;

public class DeveloperPlugin extends AbstractPlugin {
    public static final String PROP_HTTP_SERVER_REPLACE_ENABLE="sagex/plugintools/enableHTTPServerReplacements";
    public static final String PROP_HTTP_SERVER_REPLACE="sagex/plugintools/httpserver";
    public static final String PROP_REMOVE_AFTER_ADD = "sagex/plugintools/removeFilesAfterAdding";
    
    public DeveloperPlugin(SageTVPluginRegistry registry) {
        super(registry);
        addProperty(SageTVPlugin.CONFIG_BOOL, PROP_HTTP_SERVER_REPLACE_ENABLE, "true", "Update Location to Local HTTP Server", "Replaces the Location of the file to a local HTTP Server");
        addProperty(SageTVPlugin.CONFIG_TEXT, PROP_HTTP_SERVER_REPLACE, "http://localhost:8000/", "Local HTTP Server", "Local HTTP Server (hint: python -m SimpleHTTPServer)").setVisibleOnSetting(PROP_HTTP_SERVER_REPLACE_ENABLE);
        addProperty(SageTVPlugin.CONFIG_BOOL, PROP_REMOVE_AFTER_ADD, "true", "Remove Developer Files", "Remove the Developer Files after they have been Merged with the core SageTVPlugins");
    }
}
