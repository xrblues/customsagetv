package org.jdna.media.metadata;

import java.io.IOException;

import org.jdna.media.IMediaResource;

public class CompositeMediaMetadataPersistence implements IMediaMetadataPersistence {
    public IMediaMetadataPersistence[] persistence = null;
    public CompositeMediaMetadataPersistence(IMediaMetadataPersistence... persistence) {
        this.persistence = persistence;
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        for (IMediaMetadataPersistence p : persistence) {
            sb.append(p.getId()).append(";");
        }
        return sb.toString();
    }

    public String getId() {
        StringBuilder sb = new StringBuilder();
        for (IMediaMetadataPersistence p : persistence) {
            sb.append(p.getId()).append(";");
        }
        return sb.toString();
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        MediaMetadata md = new MediaMetadata();
        for (IMediaMetadataPersistence p : persistence) {
            MetadataAPI.copyNonNull(p.loadMetaData(mediaFile), md);
        }
        return md;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        for (IMediaMetadataPersistence p : persistence) {
            p.storeMetaData(md, mediaFile, options);
        }
    }
}
