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
        // TODO Auto-generated method stub
        return null;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        for (IMediaMetadataPersistence p : persistence) {
            p.storeMetaData(md, mediaFile, options);
        }
    }
}
