package org.jdna.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.MetadataID;
import org.jdna.util.PropertiesUtils;
import org.jdna.util.SortedProperties;

import sagex.phoenix.Phoenix;

/**
 * ConfigurationManager provides an Abstract way to access grouped
 * configurations. Before the ConfigurationManager can be used, it must have a
 * IConfigurationProvider registered.
 * 
 * This class is a singleton, but it needs to be initialized with a provider
 * before it can be used.
 * 
 * @author seans
 * 
 */
public class ConfigurationManager {
    private static final Logger         log = Logger.getLogger(ConfigurationManager.class);

    private static ConfigurationManager instance;

    public static ConfigurationManager getInstance() {
        if (instance == null) instance = new ConfigurationManager();
        return instance;
    }

    private File titleMapPropsFile;
    private Properties titleMapProps;

    public ConfigurationManager() {
        load();
    }

    private void load() {
        // now load the tvfilename props, if they exist
        titleMapProps = new SortedProperties();
        titleMapPropsFile = new File(Phoenix.getInstance().getSageTVRootDir(), "metadata-titles.properties");
        log.debug("Loading: " + titleMapPropsFile.getAbsolutePath());
        if (titleMapPropsFile.exists()) {
            try {
                PropertiesUtils.load(titleMapProps, titleMapPropsFile);
            } catch (Exception e) {
                log.error("Failed to load title mappings from: " + titleMapPropsFile.getAbsolutePath(),e);
            }
        } else {
            log.info("No titles map configured: " + titleMapPropsFile.getAbsolutePath());
        }
    }

    public synchronized void saveTitleMappings() throws IOException {
        log.debug("Writing Title mappings: " + titleMapPropsFile.getAbsolutePath());
        PropertiesUtils.store(titleMapProps, titleMapPropsFile, "Title to MediaProviderDataID mappings");
    }
    
    public MetadataID getMetadataIdForTitle(String title) {
        log.debug("Looking for MetadataID for title: " + title);
        if (title==null) return null;
        String mid = titleMapProps.getProperty(title);
        if (!StringUtils.isEmpty(mid)) {
            log.debug("Found MetadataID + " + mid + " for title: " + title);
            return new MetadataID(mid);
        }
        log.debug("No MetadataID configured for title: " + title);
        return null;
    }
    
    public void setMetadataIdForTitle(String title, MetadataID id) {
        if (id==null||title==null) {
            log.warn("setMetadataForTitle(): title or id is null; Title: " + title + "; id: " + id);
            return;
        }
        titleMapProps.setProperty(title, id.toIDString());
    }
}
