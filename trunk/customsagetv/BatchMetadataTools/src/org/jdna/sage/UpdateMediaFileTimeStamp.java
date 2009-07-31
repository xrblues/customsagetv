package org.jdna.sage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.PersistenceOptions;

public class UpdateMediaFileTimeStamp implements IMediaMetadataPersistence {
    private static final Logger log = Logger.getLogger(UpdateMediaFileTimeStamp.class);
    
    public String getDescription() {
        return "Updates the media file's timestamp to that sage will reload it";
    }

    public String getId() {
        return "updateTimestamp";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        return null;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        if (mediaFile!=null) {
            log.debug("Updating datetime stamp for: " + mediaFile.getLocation());
            mediaFile.touch();
        }
    }
}
