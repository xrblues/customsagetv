package org.jdna.media.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaResource;

public class CompositeMediaMetadataPersistence implements IMediaMetadataPersistence {
    private static final Logger log = Logger.getLogger(CompositeMediaMetadataPersistence.class);
    
    public List<IMediaMetadataPersistence> persistence = new ArrayList<IMediaMetadataPersistence>();
    
    public CompositeMediaMetadataPersistence(IMediaMetadataPersistence... defPersist) {
        if (defPersist!=null) {
            for (IMediaMetadataPersistence p : defPersist) {
                add(p);
            }
        }
    }

    public void add(IMediaMetadataPersistence p) {
        persistence.add(p);
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
            log.debug("Loading Metadata From: " + p.getId());
            MetadataAPI.copyNonNull(p.loadMetaData(mediaFile), md);
        }
        return md;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        for (IMediaMetadataPersistence p : persistence) {
            log.debug("Storing Metadata To: " + p.getId());
            p.storeMetaData(md, mediaFile, options);
        }
    }
}
