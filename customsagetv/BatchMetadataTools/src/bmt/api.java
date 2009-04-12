package bmt;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.metadata.impl.sage.SagePropertyType;
import org.jdna.metadataupdater.Version;
import org.jdna.util.LoggerConfiguration;

import sagex.api.Configuration;

public class api {
    private static final Logger log = Logger.getLogger(api.class);
    static {
        LoggerConfiguration.configurePlugin();
        log.info("Metadata Tools API: " + GetVersion() + "; Using Phoenix: " + phoenix.api.GetVersion());
    }
    
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
    
    public static String GetCurrentMetadataProviderIds() {
        try {
            return ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId();
        } catch (Exception e) {
            return "";
        }
    }

    public static void SetCurrentProvidersIds(String list) {
        System.out.println("Setting New Current Metadata Providers to: " + list);
        ConfigurationManager.getInstance().getMetadataConfiguration().setDefaultProviderId(list);
        SaveMetadataProviderConfiguration();
    }
    
    public static void SaveMetadataProviderConfiguration() {
        try {
            // update central fanart locations from phoenix
            ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setFanartEnabled(phoenix.api.IsFanartEnabled());
            ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setCentralFanartFolder(phoenix.api.GetFanartCentralFolder());

            // update and save
            ConfigurationManager.getInstance().updated(ConfigurationManager.getInstance().getMetadataConfiguration());
            ConfigurationManager.getInstance().save();
            System.out.println("Metadata Configuration is peristed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String GetMetadataProviderId(Object prov) {
        if (prov==null) return null;
        if (prov instanceof IMediaMetadataProvider) {
            return ((IMediaMetadataProvider)prov).getInfo().getId();
        } else if (prov instanceof IProviderInfo) {
            return ((IProviderInfo)prov).getId();
        } else {
            return String.valueOf(prov);
        }
    }

    public static String GetMetadataProviderName(IProviderInfo info) {
        return info.getName();
    }

    public static String GetMetadataProviderDescription(IProviderInfo info) {
        return info.getDescription();
    }
    
    public static boolean IsMetadataProviderInstalled(Object prov) {
        if (prov==null) return false;
        return ArrayUtils.contains(GetCurrentMetadataProviderIds().split(","), GetMetadataProviderId(prov));
    }

    public static IProviderInfo[] getMetadataProviders(boolean all) {
        List<IMediaMetadataProvider> provs = MediaMetadataFactory.getInstance().getMetaDataProviders();
        List <IProviderInfo> info = new LinkedList<IProviderInfo>();
        
        if (all) {
            for (IMediaMetadataProvider mp : provs) {
                info.add(mp.getInfo());
            }
        } else {
            for (String p : GetCurrentMetadataProviderIds().split(",")) {
                SafeAddProvider(p, info);
            }
        }
        
        return info.toArray(new IProviderInfo[info.size()]);
     }
    

    private static void SafeAddProvider(String id, List<IProviderInfo> infos) {
        try {
            IMediaMetadataProvider mp = MediaMetadataFactory.getInstance().getProvider(id);
            infos.add(mp.getInfo());
        } catch (Exception e) {
            System.out.println("Invalid Provider: removing id: " + id);
            RemoveDefaultMetadataProvider(id);
        }
    }

    
    public static IProviderInfo[] GetUninstalledMetadataProviders() {
        List<IMediaMetadataProvider> provs = MediaMetadataFactory.getInstance().getMetaDataProviders();
        List <IProviderInfo> info = new LinkedList<IProviderInfo>();
        
        for (IMediaMetadataProvider mp : provs) {
            if (!IsMetadataProviderInstalled(mp.getInfo())) {
                info.add(mp.getInfo());
            }
        }
        
        return info.toArray(new IProviderInfo[info.size()]);
     }

    
    public static void AddDefaultMetadataProvider(Object prov) {
        String id = GetMetadataProviderId(prov);
        String ids[] = GetCurrentMetadataProviderIds().split(",");
        
        if (ArrayUtils.contains(ids, id)) {
            return;
        }
        
        ids = (String[])ArrayUtils.add(ids, id);
        SetCurrentProvidersIds(new StrBuilder().appendWithSeparators(ids, ",").toString());
    }

    public static void RemoveDefaultMetadataProvider(Object prov) {
        String id = GetMetadataProviderId(prov);
        String ids[] = GetCurrentMetadataProviderIds().split(",");
        
        if (!ArrayUtils.contains(ids, id)) {
            return;
        }
        
        ids = (String[])ArrayUtils.removeElement(ids, id);
        SetCurrentProvidersIds(new StrBuilder().appendWithSeparators(ids, ",").toString());
    }
    
    public static void IncreaseMetadataProviderPriority(Object prov) {
        String id = GetMetadataProviderId(prov);
        System.out.println("Increasing Metadata Provider Priority for: " + id);
        String ids[] = GetCurrentMetadataProviderIds().split(",");
        int i = ArrayUtils.indexOf(ids, id);
        if (i<=0) {
            return; // do nothing;
        }
        
        // swap
        ids[i] = ids[i-1];
        ids[i-1] = id;
        
        System.out.println("Metadata Provider Priority for: " + id + " Changed from : " + i + " to " + (i-1));
        SetCurrentProvidersIds(new StrBuilder().appendWithSeparators(ids, ",").toString());
    }

    public static void DecreaseMetadataProviderPriority(Object prov) {
        String id = GetMetadataProviderId(prov);
        System.out.println("Decreasing Metadata Provider Priority for: " + id);
        String ids[] = GetCurrentMetadataProviderIds().split(",");
        int i = ArrayUtils.indexOf(ids, id);
        if (i<0) {
            return; // do nothing;
        }
        
        if (i>=(ids.length-1)) {
            // do nothing;
            return;
        }
        
        // swap
        ids[i] = ids[i+1];
        
        ids[i+1] = id;

        System.out.println("Metadata Provider Priority for: " + id + " Changed from : " + i + " to " + (i+1));
        SetCurrentProvidersIds(new StrBuilder().appendWithSeparators(ids, ",").toString());
    }
}
