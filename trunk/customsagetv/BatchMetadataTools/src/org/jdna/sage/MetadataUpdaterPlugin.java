package org.jdna.sage;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.FileMediaFolder;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.MovieResourceFilter;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.dvdproflocal.DVDProfilerLocalConfiguration;
import org.jdna.media.metadata.impl.dvdproflocal.LocalDVDProfMetaDataProvider;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesWithCentralFanartPersistence;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.metadataupdater.Version;
import org.jdna.sage.media.SageMediaFile;

import sage.MediaFileMetadataParser;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;
import sagex.phoenix.configuration.proxy.GroupProxy;

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
    public static boolean init = false;
    
    private static ScanningStatus status = ScanningStatus.getInstance();
    
    private MetadataConfiguration metadataConfig = GroupProxy.get(MetadataConfiguration.class);
    private DVDProfilerLocalConfiguration dvdProfConfig =GroupProxy.get(DVDProfilerLocalConfiguration.class);
    private PluginConfiguration pluginConfig = GroupProxy.get(PluginConfiguration.class);
    
    public MetadataUpdaterPlugin() {
    }

    /**
     * For a given file, find the metadata and return back a Map of SageTV
     * properties.
     * 
     */
    public Object extractMetadata(File file, String arg) {
        // lazy load the static references, and only load them once
        try {
            if (!init) {
                init=true;
                log.debug("========= BEGIN BATCH METADATA TOOLS ENVIRONMENT ==============");
                log.debug("    BMT Version:  " + Version.VERSION);
                log.debug("Phoenix Version:  " + phoenix.api.GetVersion());
                log.debug("  Sagex Version:  " + sagex.api.Version.GetVersion());
                log.debug("   Java Version:  " + System.getProperty("java.version"));
                log.debug(" Java Classpath:  " + System.getProperty("java.class.path"));
                log.debug("========= END BATCH METADATA TOOLS ENVIRONMENT ==============");
            }
        } catch (Throwable e) {
            log.error("Failed while initializing the BMT Plugin!", e);
            status.addFailed(null, "Failed to initialize the Automatic Metadata Plugin", e);
        }

        if (file==null) {
            log.error("File is Null!");
            return null;
        }

        log.debug("Automatic Plugin is Processing File: " + file.getAbsolutePath());
        
        // do the work....
        IMediaResource mr = null;
        try {
            if (!pluginConfig.getEnabled()) {
                log.info("Plugin Disabled; File: " + file.getAbsolutePath());
                return null;
            }
            
            AutomaticUpdateMetadataVisitor automaticUpdater;
            MovieResourceFilter            filter;
            IMediaMetadataPersistence      persistence;
            PersistenceOptions             options;
           
            persistence = new SageTVPropertiesWithCentralFanartPersistence();;
            options = new PersistenceOptions();
            options.setOverwriteFanart(pluginConfig.getOverwriteFanart());
            options.setOverwriteMetadata(pluginConfig.getOverwriteMetadata());
            status.beginTask("Automatic Plugin; Scanning: " + file.getAbsolutePath(), 1);
            automaticUpdater = new AutomaticUpdateMetadataVisitor(metadataConfig.getDefaultProviderId(), persistence, options, null, status);
            filter = MovieResourceFilter.INSTANCE;

            // check if the dvd profiler info is not set, and if not set, then
            // use the sagemc defaults
            if (metadataConfig.getDefaultProviderId().contains(LocalDVDProfMetaDataProvider.PROVIDER_ID)) {
                DVDProfilerLocalConfiguration dvdprof = dvdProfConfig;
                if (StringUtils.isEmpty(dvdprof.getImageDir())) {
                    if (!StringUtils.isEmpty(Configuration.GetProperty("sagemc/DVDProfiler_Root", null))) {
                        dvdprof.setImageDir(Configuration.GetProperty("sagemc/DVDProfiler_Root", null) + File.separator + "Images");
                    }
                }
                if (StringUtils.isEmpty(dvdprof.getXmlFile())) {
                    if (!StringUtils.isEmpty(Configuration.GetProperty("sagemc/meta_data_collection_xml", null))) {
                        dvdprof.setImageDir(Configuration.GetProperty("sagemc/meta_data_collection_xml", null));
                    }
                }
                File f = new File(dvdprof.getImageDir());
                if (!f.exists()) {
                    log.error("DVD Profiler Images dir is not valid: " + f.getAbsolutePath(), new FileNotFoundException(f.getAbsolutePath()));
                }
                f = new File(dvdprof.getXmlFile());
                if (!f.exists()) {
                    log.error("DVD Profiler Xml file is not valid: " + f.getAbsolutePath(), new FileNotFoundException(f.getAbsolutePath()));
                }
            }

            Object o = MediaFileAPI.GetMediaFileForFilePath(file);
            if (o != null) {
                mr = new SageMediaFile(o);
            } else {
                log.warn("Not an existing Sage Media File: " + file.getAbsolutePath() + "; Treating it as a regular File Media File.");
                mr = FileMediaFolder.createResource(file);
            }
            
            if (filter.accept(mr)) {
                log.debug("Scanning MediaFile: " + file.getAbsolutePath() + "; arg: " + arg + "; Providers: " + metadataConfig.getDefaultProviderId());

                IMediaMetadata md = null;
                
                // don't overwrite metadata, unless we are asked to
                if (!pluginConfig.getOverwriteMetadata()) {
                    md = persistence.loadMetaData(mr);
                    if (md != null) {
                        // normalize the properties
                        md = MetadataAPI.normalizeMetadata((IMediaFile)mr, md);
                        Object props = SageTVPropertiesPersistence.getSageTVMetadataMap((IMediaFile) mr, md);
                        log.info("Reusing existing metadata for MediaFile: " + file.getAbsolutePath());
                        status.addSuccess((IMediaFile) mr);
                        status.worked(1);
                        if (pluginConfig.getReturnNullMetadata()) {
                            return null;
                        } else {
                            return props;
                        }
                    }
                }

                // update the metadata and download fanart...
                automaticUpdater.visit(mr);

                // now load the props and pass back to sage
                md = persistence.loadMetaData(mr);
                if (md != null) {
                    // normalize the properties
                    md = MetadataAPI.normalizeMetadata((IMediaFile)mr, md);
                    
                    // return the props
                    Object props = SageTVPropertiesPersistence.getSageTVMetadataMap((IMediaFile) mr, md);
                    log.info("New Metadata Imported for: " + file.getAbsolutePath());
                    
                    // as a work around for when sagetv sometimes will not hang on import and not scan all movies
                    if (pluginConfig.getReturnNullMetadata()) {
                        return null;
                    } else {
                        return props;
                    }
                } else {
                    log.error("Failed to Fetch Metadata for Media: " + file.getAbsolutePath());
                }
            } else {
                log.info("Type not recognized.  Can't perform metadata/fanart lookup on file file: " + file.getAbsolutePath());
            }
        } catch (Throwable e) {
            log.error("Failed while performing Automatic Metadata/Fanart lookup", e);
            status.addFailed((IMediaFile) mr, "Failed with an error, while doing Automatic Metadata/Fanart lookup", e);
        } finally {
            status.done();
            System.out.println("BMT: Processed File: " + file.getAbsolutePath());
        }

        log.debug("Returning NULL Properties for File: " + file.getAbsolutePath());
        return null;
    }
}
