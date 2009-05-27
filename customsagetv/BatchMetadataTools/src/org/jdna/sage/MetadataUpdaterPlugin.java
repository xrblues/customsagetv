package org.jdna.sage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.FileMediaFolder;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.MovieResourceFilter;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.dvdproflocal.DVDProfilerLocalConfiguration;
import org.jdna.media.metadata.impl.dvdproflocal.LocalDVDProfMetaDataProvider;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.media.util.NullResourceVisitor;
import org.jdna.metadataupdater.MetadataUpdaterConfiguration;
import org.jdna.metadataupdater.Version;
import org.jdna.sage.media.SageMediaFile;

import sage.MediaFileMetadataParser;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;

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
    private static AutomaticUpdateMetadataVisitor automaticUpdater;
    private static MovieResourceFilter            filter;
    private static IMediaMetadataPersistence      persistence;
    private static PersistenceOptions             options;
    
    private MetadataUpdaterConfiguration metadataUpdaterConfig = new MetadataUpdaterConfiguration();
    private MetadataConfiguration metadataConfig = new MetadataConfiguration();
    private DVDProfilerLocalConfiguration dvdProfConfig =new DVDProfilerLocalConfiguration();

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
            if (filter == null) {
                debug("========= BEGIN BATCH METADATA TOOLS ENVIRONMENT ==============");
                debug("    BMT Version:  " + Version.VERSION);
                debug("Phoenix Version:  " + phoenix.api.GetVersion());
                debug("  Sagex Version:  " + sagex.api.Version.GetVersion());
                debug("   Java Version:  " + System.getProperty("java.version"));
                debug(" Java Classpath:  " + System.getProperty("java.class.path"));

                String classpath = System.getProperty("java.class.path");
                Pattern p = Pattern.compile("metadata-updater-([0-9\\.]+).jar");
                Matcher m = p.matcher(classpath);
                if (m.find()) {
                    debug("You should not be running a VERSIONED metadata-updater.jar.  Please remove all versioned jarfiles starting with metadata-updater-*");
                }
                debug("========= END BATCH METADATA TOOLS ENVIRONMENT ==============");

                String providerId = metadataConfig.getDefaultProviderId();
                debug("** Batch Metadata Plugin; Using ProviderId: " + providerId);

                persistence = MetadataPluginOptions.getAutomaticUpdaterPersistence();
                options = MetadataPluginOptions.getPersistenceOptions();
                automaticUpdater = new AutomaticUpdateMetadataVisitor(providerId, persistence, options, null, new NullResourceVisitor(), new IMediaResourceVisitor() {
                    public void visit(IMediaResource resource) {
                        debug("Could not automatically update: " + resource.getLocationUri());
                    }
                });
                filter = MovieResourceFilter.INSTANCE;
            }
        } catch (Throwable e) {
            error("Failed while initializing the BMT Plugin!", e);
        }

        // do the work....
        try {
            // sync our settings with the sage stv settings for central
            // fanart...
            if (phoenix.api.IsFanartEnabled()) {
                metadataUpdaterConfig.setFanartEnabled(phoenix.api.IsFanartEnabled());
                metadataUpdaterConfig.setCentralFanartFolder(phoenix.api.GetFanartCentralFolder());
            }

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
                    error("DVD Profiler Images dir is not valid: " + f.getAbsolutePath(), new FileNotFoundException(f.getAbsolutePath()));
                }
                f = new File(dvdprof.getXmlFile());
                if (!f.exists()) {
                    error("DVD Profiler Xml file is not valid: " + f.getAbsolutePath(), new FileNotFoundException(f.getAbsolutePath()));
                }
            }

            IMediaResource mr = null;
            Object o = MediaFileAPI.GetMediaFileForFilePath(file);
            if (o != null) {
                mr = new SageMediaFile(o);
            } else {
                mr = FileMediaFolder.createResource(file);
            }
            if (filter.accept(mr)) {
                debug("BatchMetadataTools " + Version.VERSION + "; Handling File: " + file.getAbsolutePath() + "; arg: " + arg);

                IMediaMetadata md = persistence.loadMetaData(mr);
                if (md != null) {
                    Object props = SageTVPropertiesPersistence.getSageTVMetadataMap((IMediaFile) mr, md);
                    debug("Reusing existing metadata for MediaFile: " + file.getAbsolutePath());
                    return props;
                }

                // update the metadata and download fanart...
                automaticUpdater.visit(mr);

                // now load the props and pass back to sage
                md = persistence.loadMetaData(mr);
                if (md != null) {
                    // return the props
                    Object props = SageTVPropertiesPersistence.getSageTVMetadataMap((IMediaFile) mr, md);
                    debug("New Metadata Imported for: " + file.getAbsolutePath());
                    return props;
                } else {
                    debug("Failed to Fetch Metadata for Media: " + file.getAbsolutePath());
                }
            } else {
                debug("Type not recognized.  Can't perform metadata/fanart lookup on file file: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            error("Failed while performing Automatic Metadata/Fanart lookup", e);
        }

        return null;
    }

    private void debug(String msg) {
        System.out.println("BMT: " + msg);
    }

    private void error(String msg, Throwable t) {
        debug("BMT: ERROR: " + msg);
        debug("=============== BEGIN BMT EXCEPTION ================");
        t.printStackTrace();
        debug("=============== END BMT EXCEPTION ================");
    }
}
