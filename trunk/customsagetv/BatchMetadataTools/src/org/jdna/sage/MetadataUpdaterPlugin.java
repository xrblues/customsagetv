package org.jdna.sage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.metadataupdater.Version;
import org.jdna.process.MetadataItem;
import org.jdna.process.MetadataProcessor;
import org.jdna.process.SysEventMessageID;

import sage.MediaFileMetadataParser;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.event.message.SystemMessageEvent;
import sagex.phoenix.event.message.SystemMessageEvent.Severity;
import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.plugin.Plugin.State;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.util.FileUtils;
import sagex.phoenix.util.PropertiesUtils;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.filters.IResourceFilter;
import sagex.phoenix.vfs.filters.MediaTypeFilter;
import sagex.phoenix.vfs.impl.FileResourceFactory;
import bmt.BMTActivator;

/**
 * Plugin Rules - run only for mediafiles that do not have properties - ignore
 * metadata on tv types, but try to download fanart
 * 
 * @author seans
 * 
 */
public class MetadataUpdaterPlugin implements MediaFileMetadataParser {
    private static final Logger log          = Logger.getLogger(MetadataUpdaterPlugin.class);
    private static boolean      init         = false;

    private PluginConfiguration pluginConfig = GroupProxy.get(PluginConfiguration.class);

    public MetadataUpdaterPlugin() {
        // Automatic Plugin
    }

    /**
     * For a given file, find the metadata and return back a Map of SageTV
     * properties.
     * 
     */
    public Object extractMetadata(File file, @SuppressWarnings("unused") String arg) {
        try {
            if (!init) {
                init = true;
                log.debug("========= BEGIN BATCH METADATA TOOLS ENVIRONMENT ==============");
                log.info("    BMT Version:  " + Version.VERSION);
                log.debug("Phoenix Version:  " + phoenix.api.GetVersion());
                log.debug("  Sagex Version:  " + sagex.api.Version.GetVersion());
                log.debug("   Java Version:  " + System.getProperty("java.version"));
                log.debug(" Java Classpath:  " + System.getProperty("java.class.path"));
                log.debug("========= END BATCH METADATA TOOLS ENVIRONMENT ==============");

                log.info("Registering URL Cache Monitor");
                BMTActivator act = new BMTActivator();
                act.pluginChanged(null, State.STARTING);
            }
        } catch (Throwable e) {
            log.warn("Failed while initializing the BMT Plugin!", e);
            Phoenix.getInstance().getEventBus().fireEvent(new SystemMessageEvent(SysEventMessageID.AUTOMATIC_PLUGIN_INIT_FAILED, Severity.ERROR, "Failed to initialize the Automatic Metadata Plugin"));
            return null;
        }

        if (file == null) {
            log.warn("File is Null!");
            return null;
        }

        if (!pluginConfig.getEnabled()) {
            log.warn("BMT Automatic Plugin Disabled.");
            return null;
        }

        File propFile = FanartUtil.resolvePropertiesFile(file);
        if (propFile == null || !propFile.exists()) {
            return scanFile(file);
        } else {
            // check if the properties are good, and if so, just use them
            Properties props = new Properties();
            try {
                PropertiesUtils.load(props, propFile);
            } catch (IOException e) {
                log.warn("Failed to load properties for: " + propFile.getAbsolutePath());
            }

            // missing Title, so let's scan
            if (StringUtils.isEmpty(props.getProperty(SageProperty.DISPLAY_TITLE.sageKey))) {
                return scanFile(file);
            } else {
                log.info("Returning existing metadata for: " + file.getAbsolutePath());
                return props;
            }
        }
    }

    private Properties scanFile(File file) {
        log.info("BMT Automatic Plugin handling file: " + file.getAbsolutePath() + "; BMT Version: " + Version.VERSION + "; Phoenix Version: " + phoenix.api.GetVersion());
        PersistenceOptions options = new PersistenceOptions();
        options.setUpdateWizBin(false);
        options.setUseTitleMasks(true);
        options.setOverwriteFanart(true);
        options.setOverwriteMetadata(true);
        options.setUpdateWizBin(false);
        options.setCreateDefaultSTVThumbnail(pluginConfig.getCreateDefaultSTVThumbnail());
        options.setImportAsTV(false);
        options.setCreateProperties(pluginConfig.getCreateProperties());
        // no need to touch files when using the automatic plugin
        options.setTouchingFiles(false);

        // tell the persistence engines that we are using the automatic plugin...
        options.setUsingAutomaticPlugin(true);
        
        MetadataProcessor processor = new MetadataProcessor(options);
        
        IMediaResource fmf = FileResourceFactory.createResource(file);
        
        boolean scanFile = false;
        // setup the filters
        for (String s : pluginConfig.getSupportedMediaTypes().split("\\s*,\\s*")) {
            MediaResourceType type = MediaResourceType.toMediaResourceType(s);
            if (type!=null) {
                IResourceFilter r = new MediaTypeFilter(type);
                if (r.accept(fmf)) {
                    scanFile = true;
                    break;
                }
            } else {
                log.warn("Invalid MediaType: " + s + " specified in Filter: " + pluginConfig.getSupportedMediaTypes());
            }
        }
        
        if (!scanFile) {
            log.warn("Filed Not Accepted by the Automatic plugin: " + file + " because it did not match a mediatype in " + pluginConfig.getSupportedMediaTypes());
            return null;
        }
        
        // process the file
        ProgressTracker<MetadataItem> tracker = new ProgressTracker<MetadataItem>() {
            @Override
            public void addFailed(MetadataItem item, String msg, Throwable t) {
                super.addFailed(item, msg, t);
                // use system messages to notify of failed items.
                if (pluginConfig.getUseSystemMessagesForFailed()) {
                    if (item != null && item.getQuery()!=null) {
                        Map<String, String> vars = new HashMap<String, String>();
                        for (SearchQuery.Field f : SearchQuery.Field.values()) {
                            vars.put(f.name(), item.getQuery().get(f));
                        }
                        vars.put("MediaType", item.getQuery().getMediaType().name());
                        String prov = null;
                        if (item.getProvider()!=null) {
                            prov = item.getProvider().getInfo().getId();
                        }
                        SystemMessageEvent evt = new SystemMessageEvent(SysEventMessageID.METADATA_FAILED, Severity.ERROR, "Failed to find metadata for: " + item.getQuery().get(Field.FILE) + " using provider " + prov, vars);
                        Phoenix.getInstance().getEventBus().fireEvent(evt);
                    } else if (item != null) {
                        String prov = null;
                        if (item.getProvider()!=null) {
                            prov = item.getProvider().getInfo().getId();
                        }
                        SystemMessageEvent evt = new SystemMessageEvent(SysEventMessageID.METADATA_FAILED, Severity.ERROR, "Failed to find metadata for: " + item.getFile() + " using provider " + prov);
                        Phoenix.getInstance().getEventBus().fireEvent(evt);
                    }
                }
            }
        };
        processor.process(fmf, tracker);
        
        // now load the properties, if they exist...
        File propFile = FanartUtil.resolvePropertiesFile(file);
        if (propFile!=null && propFile.exists()) {
            Properties props = new Properties();
            try {
                PropertiesUtils.load(props, propFile);
                log.debug("Scan appeared to work for " + file + "; returning properties.");
                if (!pluginConfig.getCreateProperties()) {
                    log.debug("Removing Property File: " + propFile + " because create properties is disabled.");
                    FileUtils.deleteQuietly(propFile);
                }
                return props;
            } catch (IOException e) {
                log.warn("Failed to load properties for: " + propFile.getAbsolutePath());
            }            
        }
        
        log.warn("Failed to create metadata for " + file);
        return null;
    }
}
