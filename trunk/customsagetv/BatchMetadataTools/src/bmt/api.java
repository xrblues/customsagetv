package bmt;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.metadata.impl.sage.SagePropertyType;
import org.jdna.metadataupdater.Version;
import org.jdna.sage.OnDemandConfiguration;

import sagex.api.Configuration;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.Group;
import sagex.phoenix.configuration.IConfigurationElement;
import sagex.phoenix.configuration.IConfigurationMetadata;
import sagex.phoenix.configuration.proxy.GroupProxy;

public class api {
    private static final Logger log = Logger.getLogger(api.class);
    
    public static String GetVersion() {
        return Version.VERSION;
    }
    
    public static void InstallBMTPlugin() {
        log.debug("BMT Plugin is being installed.");
        String bmtClass=org.jdna.sage.MetadataUpdaterPlugin.class.getName();
        String plugins = Configuration.GetServerProperty("mediafile_metadata_parser_plugins",null);
        
        if (!IsBMTPluginInstalled()) {
            if (StringUtils.isEmpty(plugins)) {
                plugins = "";
            } else {
                plugins += ";";
            }
            Configuration.SetServerProperty("mediafile_metadata_parser_plugins", plugins + bmtClass);
        }
        
        log.debug("Installing the custom metadata fields....");

        List<String> props = new LinkedList<String>();
        String customPropsStr = Configuration.GetServerProperty("custom_metadata_properties", null);
        if (!StringUtils.isEmpty(customPropsStr)) {
            for (String s : customPropsStr.split(";")) {
                props.add(s.trim());
            }
        }
        
        // merge our extended props into the existing set
        for (SageProperty sp : SageProperty.values()) {
            if (sp.propertyType == SagePropertyType.EXTENDED) {
                if (!props.contains(sp.sageKey)) {
                    props.add(sp.sageKey);
                }
            }
        }
        
        if (props.size()>0) {
            Configuration.SetServerProperty("custom_metadata_properties", new StrBuilder().appendWithSeparators(props, ";").toString());
            log.debug("Set Custom Metadata Properties: " + Configuration.GetServerProperty("custom_metadata_properties", ""));
        }
    }

    public static void RemoveBMTPlugin() {
        String bmtClass=org.jdna.sage.MetadataUpdaterPlugin.class.getName();
        String plugins = Configuration.GetServerProperty("mediafile_metadata_parser_plugins",null);
        if (IsBMTPluginInstalled()) {
            plugins = plugins.replaceAll(";"+bmtClass, "");
            plugins = plugins.replaceAll(bmtClass+";", "");
            plugins = plugins.replaceAll(bmtClass, "");
            Configuration.SetServerProperty("mediafile_metadata_parser_plugins", plugins);
        }
    }
    
    public static void SetBMTPluginInstalled(boolean on) {
        if (on) {
            InstallBMTPlugin();
        } else {
            RemoveBMTPlugin();
        }
    }
    
    public static boolean IsBMTPluginInstalled() {
        String bmtClass=org.jdna.sage.MetadataUpdaterPlugin.class.getName();
        String plugins = Configuration.GetServerProperty("mediafile_metadata_parser_plugins",null);
        if (plugins==null) {
            return false;
        }
        return plugins.contains(bmtClass);
    }
    
    /**
     * Return the configuration metadata for configuring BMT on demand options.
     * Using this node you can navigate the configuration elements.
     *
     * Refer to the Phoenix 
     * @return
     */
    public static Group GetBMTConfiguration() {
        return (Group) Phoenix.getInstance().getConfigurationMetadataManager().findElement("bmt/ondemandplugin");
    }
}
