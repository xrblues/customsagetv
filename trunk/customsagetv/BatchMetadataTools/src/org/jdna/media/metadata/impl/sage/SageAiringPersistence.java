package org.jdna.media.metadata.impl.sage;

import java.io.IOException;

import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

public class SageAiringPersistence implements IMediaMetadataPersistence {
    public String getDescription() {
        return "Writes directly to a Sage Airing";
    }

    public String getId() {
        return "SageAiringPersistence";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        // TODO Auto-generated method stub
        return null;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, long options) throws IOException {
    }
}
