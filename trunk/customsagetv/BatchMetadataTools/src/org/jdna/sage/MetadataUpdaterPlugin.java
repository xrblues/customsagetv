package org.jdna.sage;

import java.io.File;
import java.util.List;

import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaResource;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.impl.sage.SageVideoMetaDataPersistence;
import org.jdna.metadataupdater.MetadataUpdater;

import sage.MediaFileMetadataParser;

public class MetadataUpdaterPlugin implements MediaFileMetadataParser {

	public Object extractMetadata(File file, String arg) {
		try {
			System.out.println("Handling File: " + file.getAbsolutePath() + "; arg: " + arg);
			if (file.isDirectory()) {
				System.out.println("Skipping Directory: " + file.getAbsolutePath());
				return null;
			}
		
			IMediaResource mr = MediaResourceFactory.getInstance().createResource(file.toURI());
			IMediaMetadata md = mr.getMetadata();
			if (md==null) {
				String providerId = ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId();
				// we can fetch  it
				String name = MediaMetadataUtils.cleanSearchCriteria(mr.getTitle(), true);
				List<IMediaSearchResult> results = MediaMetadataFactory.getInstance().search(providerId, IMediaMetadataProvider.SEARCH_TITLE, name);
				if (!MetadataUpdater.isGoodSearch(results)) {
					System.out.println("Not very sucessful with the search for: " + name);
					return null;
				}

				mr.updateMetadata(MediaMetadataFactory.getInstance().getProvider(providerId).getMetaData(results.get(0)), true);
				Object props = SageVideoMetaDataPersistence.metadataToSageTVMap(mr.getMetadata());
				System.out.println("Metadata Imported for: " + file.getAbsolutePath());
				return props;
			} else {
				System.out.println("Ignoring Media: " + file.getAbsolutePath() + "; It already has metadata.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}

}
