package bmt;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.util.LoggerConfiguration;

import sagex.phoenix.fanart.SageUtil;

public class api {
    static {
        LoggerConfiguration.configure();
    }
    
    public static void InstallBMTPlugin() {
        String bmtClass=org.jdna.sage.MetadataUpdaterPlugin.class.getName();
        String plugins = (String) SageUtil.GetProperty("mediafile_metadata_parser_plugins",null);
        if (!IsBMTPluginInstalled()) {
            if (plugins==null) {
                plugins = "";
            } else {
                plugins += ";";
            }
            SageUtil.SetProperty("mediafile_metadata_parser_plugins", plugins + bmtClass);
        }
        
        
    }

    public static void RemoveBMTPlugin() {
        String bmtClass=org.jdna.sage.MetadataUpdaterPlugin.class.getName();
        String plugins = (String) SageUtil.GetProperty("mediafile_metadata_parser_plugins",null);
        if (IsBMTPluginInstalled()) {
            plugins = plugins.replaceAll(";"+bmtClass, "");
            plugins = plugins.replaceAll(bmtClass+";", "");
            plugins = plugins.replaceAll(bmtClass, "");
            SageUtil.SetProperty("mediafile_metadata_parser_plugins", plugins);
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
        String plugins = (String) SageUtil.GetProperty("mediafile_metadata_parser_plugins",null);
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
