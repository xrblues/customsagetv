package org.jdna.sage;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.metadataupdater.Version;
import org.jdna.process.ScanMediaFileEvent;
import org.jdna.process.SysEventMessageID;

import sage.MediaFileMetadataParser;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.event.message.SystemMessageEvent;
import sagex.phoenix.event.message.SystemMessageEvent.Severity;
import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.plugin.Plugin.State;
import sagex.phoenix.util.PropertiesUtils;
import bmt.BMTActivator;

/**
 * A MetadataUpdaterPlugin that will fetch metadata for new movies.
 * 
 * Sage will run this on any new items that are added to your collection, or on
 * items that are updated in your collection.
 * 
 * @author seans
 * 
 */
public class MetadataUpdaterPlugin implements MediaFileMetadataParser {
    private static final Logger log = Logger.getLogger(MetadataUpdaterPlugin.class);
    private static boolean init=false;

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
        /**
         * new model
         * - read file
         * - create a query
         * - create persistence options
         * - fire scan event
         * - return null
         * - track multiple returns of the same file to prevent sage from going into an endless loop
         */
        try {
            if (!init) {
                init=true;
                log.debug("========= BEGIN BATCH METADATA TOOLS ENVIRONMENT ==============");
                log.info("    BMT Version:  " + Version.VERSION);
                log.debug("Phoenix Version:  " + phoenix.api.GetVersion());
                log.debug("  Sagex Version:  " + sagex.api.Version.GetVersion());
                log.debug("   Java Version:  " + System.getProperty("java.version"));
                log.debug(" Java Classpath:  " + System.getProperty("java.class.path"));
                log.debug("========= END BATCH METADATA TOOLS ENVIRONMENT ==============");
                
                log.info("Registering the ScanMediaFileEventHandler");
                final SageScanMediaFileEvenHandler handler = new SageScanMediaFileEvenHandler();
                Phoenix.getInstance().getEventBus().addHandler(ScanMediaFileEvent.TYPE, handler);
                Runtime.getRuntime().addShutdownHook(new Thread() {
                   public void run() {
                       handler.shutDown(); 
                   }
                });
                log.info("Registering URL Cache Monitor");
                BMTActivator act = new BMTActivator();
                act.pluginChanged(null, State.STARTING);
            }
        } catch (Throwable e) {
            log.warn("Failed while initializing the BMT Plugin!", e);
            Phoenix.getInstance().getEventBus().fireEvent(new SystemMessageEvent(SysEventMessageID.AUTOMATIC_PLUGIN_INIT_FAILED, Severity.ERROR, "Failed to initialize the Automatic Metadata Plugin"));
            return null;
        }

        if (file==null) {
            log.warn("File is Null!");
            return null;
        }
        
        if (!pluginConfig.getEnabled()) {
            log.warn("BMT Automatic Plugin Disabled.");
            return null;
        }
        
        File propFile = FanartUtil.resolvePropertiesFile(file);
        if (propFile==null || !propFile.exists()) {
            scanFile(file);
        } else {
            // we have properties files..
            if (pluginConfig.getOverwriteFanart()||pluginConfig.getOverwriteMetadata()) {
                scanFile(file);
            } else {
                // just return the properties...
                Properties props = new Properties();
                try {
                    PropertiesUtils.load(props, propFile);
                } catch (IOException e) {
                    log.warn("Failed to load properties for: " + propFile.getAbsolutePath());
                }
                
                if (StringUtils.isEmpty(props.getProperty(SageProperty.DISPLAY_TITLE.sageKey))) {
                    scanFile(file);
                } else {
                    log.info("Returning existing metadata for: " + file.getAbsolutePath());
                    return props;
                }
            }
        }
        
        // metadata will be updated later by the eventbus system, if there's a listener.
        return null;
    }

    private void scanFile(File file) {
        log.info("BMT Automatic Plugin handling file: " + file.getAbsolutePath() + "; BMT Version: " + Version.VERSION + "; Phoenix Version: " + phoenix.api.GetVersion());
        PersistenceOptions options = new PersistenceOptions();
        options.setUpdateWizBin(pluginConfig.getUpdateWizBin());
        options.setUseTitleMasks(true);
        options.setOverwriteFanart(pluginConfig.getOverwriteFanart());
        options.setOverwriteMetadata(pluginConfig.getOverwriteMetadata());
        options.setUpdateWizBin(pluginConfig.getUpdateWizBin());
        options.setCreateDefaultSTVThumbnail(pluginConfig.getCreateDefaultSTVThumbnail());
        if (pluginConfig.getImportTVAsRecordings()) {
            options.setImportAsTV(pluginConfig.getImportTVAsRecordings());
            options.setUpdateWizBin(true);
        }
        
        if (pluginConfig.getUpdateWizBin()) {
            options.setCreateProperties(false);
            options.setTouchingFiles(false);
        } else {
            options.setCreateProperties(true);
            options.setTouchingFiles(true);
        }
        
        ScanMediaFileEvent event = new ScanMediaFileEvent(file, options);
        Phoenix.getInstance().getEventBus().fireEvent(event);
    }
}
