package org.jdna.media.metadata.impl.sage;

import java.io.IOException;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.PersistenceOptions;

public class CentralFanartPersistence implements IMediaMetadataPersistence {
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
        // ensure the metadata type is set.
        MetadataUtil.updateMetadataMediaType(md);
        FanartStorage.downloadFanart((IMediaFile)mediaFile, md, options);
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        // we can't reload image metadata
        return null;
    }
}
