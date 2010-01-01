package org.jdna.media.metadata.impl.sage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.PersistenceOptions;

import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;

public class CentralFanartPersistence implements IMediaMetadataPersistence {
    private static final Logger log = Logger.getLogger(CentralFanartPersistence.class);
    public CentralFanartPersistence() {
        if (!SageProperty.isPropertySetValid()) {
            throw new RuntimeException("Programmer Error: SageProperty is missing some MetadataKey values!");
        }
    }

    public String getDescription() {
        return "Writes Fanart Metadata as Images to the Central Fanart Folder system";
    }

    public String getId() {
        return "centralFanart";
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        if (md==null) {
            log.error("Can't store fanart for null metadata!");
            return;
        }
        log.debug("Storing fanart for: " + MetadataAPI.getMediaTitle(md));
        // ensure the metadata type is set.
        MetadataAPI.normalizeMetadata((IMediaFile)mediaFile, md, options);
        FanartStorage.downloadFanart((IMediaFile)mediaFile, md, options);
        log.debug("Storing fanart complete.");
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        log.debug("Central Fanart does not support reloading metadata.");
        // we can't reload image metadata
        return null;
    }
}
