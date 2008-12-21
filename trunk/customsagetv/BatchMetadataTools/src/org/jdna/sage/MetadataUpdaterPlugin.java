package org.jdna.sage;

import java.io.File;

import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.impl.MovieResourceFilter;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.impl.sage.SageVideoMetaDataPersistence;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.media.util.NullResourceVisitor;

import sage.MediaFileMetadataParser;

/**
 * A MetadataUpdaterPlugin that will fetch metadata for new movies.
 * 
 * Sage will run this on any new items that are added to your collection, or on items that are updated in your collection.
 * 
 * @author seans
 *
 */
public class MetadataUpdaterPlugin implements MediaFileMetadataParser {
	private static AutomaticUpdateMetadataVisitor updater;
	private static MovieResourceFilter filter;
	
	public MetadataUpdaterPlugin() {
	}
	
	/**
	 * For a given file, find the metadata and return back a Map of SageTV properties.
	 * 
	 */
	public Object extractMetadata(File file, String arg) {
		// lazy load the static references, and only load them once
		if (filter==null) {
			String providerId = ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId();
			System.out.println("** Batch Metadata Plugin; Using ProviderId: " + providerId);
			System.out.println("** Configuration for Metadata Plugin: " + ConfigurationManager.getInstance().getConfigFileLocation());
	
			updater = new AutomaticUpdateMetadataVisitor(providerId, true, true, new NullResourceVisitor(), new IMediaResourceVisitor() {
				public void visit(IMediaResource resource) {
					System.out.println("Could not automatically update: " + resource.getLocationUri());
				}
			});
			filter = MovieResourceFilter.INSTANCE;
		}
		
		// do the work....
		try {
			System.out.println("BatchMetadataTools; Handling File: " + file.getAbsolutePath() + "; arg: " + arg);
			IMediaResource mr = MediaResourceFactory.getInstance().createResource(file.toURI());
			if (filter.accept(mr)) {
				IMediaMetadata md = mr.getMetadata();
				if (md==null) {
					updater.visit(mr);
					if (mr.getMetadata()!=null) {
						Object props = SageVideoMetaDataPersistence.metadataToSageTVMap(mr.getMetadata());
						System.out.println("Metadata Imported for: " + file.getAbsolutePath());
						return props;
					}
				} else {
					System.out.println("Ignoring Media: " + file.getAbsolutePath() + "; It already has metadata.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
