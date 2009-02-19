package org.jdna.sage;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.impl.MovieResourceFilter;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.impl.sage.SageVideoMetaDataPersistence;
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
                LoggerConfiguration.configure();
                
                System.out.println("========= BEGIN BATCH METADATA TOOLS ENVIRONMENT ==============");
                System.out.println("   BMT Version:  " + Version.VERSION);
                System.out.println("  Java Version:  " + System.getProperty("java.version"));
                System.out.println("Java Classpath:  " + System.getProperty("java.class.path"));
                
                String classpath = System.getProperty("java.class.path");
                Pattern p = Pattern.compile("metadata-updater-([0-9\\.]+).jar");
                Matcher m = p.matcher(classpath);
                if (m.find()) {
                    if (m.find()) {
                        System.out.println("You have more than 1 metadata updater log in the classpath.  Clean it up, and restart.");
                    } else {
                        System.out.println("Only found 1 metadata-updater jar in the classpath, which is good.");
                    }
                }
                System.out.println("========= END BATCH METADATA TOOLS ENVIRONMENT ==============");
                
                String providerId = ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId();
                System.out.println("** Batch Metadata Plugin; Using ProviderId: " + providerId);
                System.out.println("** Configuration for Metadata Plugin: " + ConfigurationManager.getInstance().getConfigFileLocation());
    
                updater = new AutomaticUpdateMetadataVisitor(providerId, true, IMediaMetadataPersistence.OPTION_OVERWRITE_POSTER | IMediaMetadataPersistence.OPTION_OVERWRITE_BACKGROUND, new NullResourceVisitor(), new IMediaResourceVisitor() {
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
            IMediaResource mr = MediaResourceFactory.getInstance().createResource(file.toURI());
            if (filter.accept(mr)) {
                System.out.println("BatchMetadataTools; Handling File: " + file.getAbsolutePath() + "; arg: " + arg);
                IMediaMetadata md = mr.getMetadata();
                if (md == null) {
                    updater.visit(mr);
                    if (mr.getMetadata() != null) {
                        Object props = SageVideoMetaDataPersistence.metadataToSageTVMap(mr.getMetadata());
                        System.out.println("Metadata Imported for: " + file.getAbsolutePath());
                        return props;
                    } else {
                        System.out.println("Unable to Fetch Metadata for Medai: " + file.getAbsolutePath());
                    }
                } else {
                    System.out.println("Media: " + file.getAbsolutePath() + "; It already has metadata, returning existing metadata.");
                    Object props = SageVideoMetaDataPersistence.metadataToSageTVMap(mr.getMetadata());
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
