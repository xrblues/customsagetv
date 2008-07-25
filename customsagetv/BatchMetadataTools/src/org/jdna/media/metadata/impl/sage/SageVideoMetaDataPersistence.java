package org.jdna.media.metadata.impl.sage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataPersistence;


public class SageVideoMetaDataPersistence implements IVideoMetaDataPersistence {
	private static final Logger log = Logger.getLogger(SageVideoMetaDataPersistence.class);
	
	public SageVideoMetaDataPersistence() {
	}
	
	public String getDescription() {
		return "Exports to Sage video properties format";
	}

	public String getId() {
		return "sage";
	}

	public void storeMetaData(IVideoMetaData md, IMediaFile mediaFile) throws IOException {
		if (md.getTitle()==null) throw new IOException("MetaData doesn't contain title.  Will not Save/Update.");
		
		// copy existing metadata
		SageVideoMetaData newMD = new SageVideoMetaData(mediaFile, md);
		newMD.save();
	}

	public IVideoMetaData loadMetaData(IMediaFile mediaFile) {
		SageVideoMetaData smd =  new SageVideoMetaData(mediaFile);
		if (smd.hasMetaData()) {
			return smd;
		} else {
			return null;
		}
	}
}
