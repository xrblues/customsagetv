package org.jdna.metadataupdater;

import org.apache.log4j.Logger;

import sagex.phoenix.metadata.IMetadata;
import sagex.phoenix.metadata.IMetadataOptions;
import sagex.phoenix.metadata.IMetadataPersistence;
import sagex.phoenix.metadata.MetadataException;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.sage.SageMediaFile;

public class UpdateMediaFileTimeStamp implements IMetadataPersistence {
    private static final Logger log = Logger.getLogger(UpdateMediaFileTimeStamp.class);
    
	public void storeMetadata(IMediaFile file, IMetadata md, IMetadataOptions options) throws MetadataException {
        if (file!=null) {
            log.debug("Updating datetime stamp for: " + file);
            file.touch(file.lastModified() + SageMediaFile.MIN_TOUCH_ADJUSTMENT);
        }
	}
}
