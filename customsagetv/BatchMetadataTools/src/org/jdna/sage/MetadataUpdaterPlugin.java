package org.jdna.sage;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.impl.MovieResourceFilter;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.impl.sage.SageTVWithCentralFanartFolderPersistence;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.media.util.NullResourceVisitor;
import org.jdna.metadataupdater.Version;
import org.jdna.util.LoggerConfiguration;

import sage.MediaFileMetadataParser;

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
    private static AutomaticUpdateMetadataVisitor updater;
    private static MovieResourceFilter            filter;

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
                LoggerConfiguration.configurePlugin();
                
                System.out.println("========= BEGIN BATCH METADATA TOOLS ENVIRONMENT ==============");
                System.out.println("    BMT Version:  " + Version.VERSION);
                System.out.println("Phoenix Version:  " + phoenix.api.GetVersion());
                System.out.println("  Sagex Version:  " + sagex.api.Version.GetVersion());
                System.out.println("   Java Version:  " + System.getProperty("java.version"));
                System.out.println(" Java Classpath:  " + System.getProperty("java.class.path"));
                
                String classpath = System.getProperty("java.class.path");
                Pattern p = Pattern.compile("metadata-updater-([0-9\\.]+).jar");
                Matcher m = p.matcher(classpath);
                if (m.find()) {
                    System.out.println("You should not be running a VERSIONED metadata-updater.jar.  Please remove all versioned jarfiles starting with metadata-updater-*");
                }
                System.out.println("========= END BATCH METADATA TOOLS ENVIRONMENT ==============");
                
                String providerId = ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId();
                System.out.println("** Batch Metadata Plugin; Using ProviderId: " + providerId);
                System.out.println("** Configuration for Metadata Plugin: " + ConfigurationManager.getInstance().getConfigFileLocation());
    
                updater = new AutomaticUpdateMetadataVisitor(providerId, ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isOverwrite(), null, new NullResourceVisitor(), new IMediaResourceVisitor() {
                    public void visit(IMediaResource resource) {
                        System.out.println("Could not automatically update: " + resource.getLocationUri());
                    }
                });
                filter = MovieResourceFilter.INSTANCE;
            }
        } catch (Throwable e) {
            System.out.println("BMT: Error!!!");
            e.printStackTrace();
        }

        // do the work....
        try {
            // sync our settings with the sage stv settings for central fanart...
            ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setFanartEnabled(phoenix.api.IsFanartEnabled());
            if (phoenix.api.IsFanartEnabled()) {
                ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setCentralFanartFolder(phoenix.api.GetFanartCentralFolder());
            }

            // now do the lookups
            IMediaResource mr = MediaResourceFactory.getInstance().createResource(file.toURI());
            if (filter.accept(mr)) {
                System.out.println("BatchMetadataTools "+ Version.VERSION +"; Handling File: " + file.getAbsolutePath() + "; arg: " + arg);
                IMediaMetadata md = mr.getMetadata();
                if (md == null) {
                    updater.visit(mr);
                    if (mr.getMetadata() != null) {
                        Object props = SageTVWithCentralFanartFolderPersistence.getSageTVMetadataMap((IMediaFile)mr, mr.getMetadata());
                        System.out.println("Metadata Imported for: " + file.getAbsolutePath());
                        return props;
                    } else {
                        System.out.println("Unable to Fetch Metadata for Medai: " + file.getAbsolutePath());
                    }
                } else {
                    System.out.println("Media: " + file.getAbsolutePath() + "; It already has metadata, returning existing metadata.");
                    Object props = SageTVWithCentralFanartFolderPersistence.getSageTVMetadataMap((IMediaFile)mr, mr.getMetadata());
                    return props;
                }
            } else {
                System.out.println("BatchMetadataTools: Skipping File: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
