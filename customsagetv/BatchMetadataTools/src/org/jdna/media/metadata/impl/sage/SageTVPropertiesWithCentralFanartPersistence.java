package org.jdna.media.metadata.impl.sage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.PersistenceOptions;

import sagex.phoenix.vfs.IMediaResource;

/**
 * Simple composite persistence that stores the properties and then downloads the fanart
 * 
 * @author seans
 */
public class SageTVPropertiesWithCentralFanartPersistence implements IMediaMetadataPersistence {
    private static final Logger log = Logger.getLogger(SageTVPropertiesWithCentralFanartPersistence.class);
    private SageTVPropertiesPersistence propPersist = new SageTVPropertiesPersistence();
    private CentralFanartPersistence fanartPersist = new CentralFanartPersistence();
    
    public SageTVPropertiesWithCentralFanartPersistence() {
        if (!SageProperty.isPropertySetValid()) {
            throw new RuntimeException("Programmer Error: SageProperty is missing some MetadataKey values!");
        }
    }

    public String getDescription() {
        return "Composite Persistence that will store the properties and download fanart";
    }

    public String getId() {
        return "sagePropertyWithFanart";
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        log.debug("Storing Properties...");
        propPersist.storeMetaData(md, mediaFile, options);
        log.debug("Storing Fanart...");
        fanartPersist.storeMetaData(md, mediaFile, options);
        log.debug("Stored Properties and Fanart.");
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        return propPersist.loadMetaData(mediaFile);
    }
}
