package org.jdna.media.metadata;

import java.io.IOException;

import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.sage.media.SageCustomMetadataPersistence;
import org.jdna.sage.media.SageShowPeristence;

import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.vfs.IMediaResource;

/**
 * Default persistence implementation.  This is a basically a 'smart' peristence, in that it will
 * call other peristence implementations based on conditions, such as importing as tv, etc.
 * 
 * @author seans
 *
 */
public class MediaMetadataPersistence implements IMediaMetadataPersistence {
    private MetadataConfiguration config = null;
    public MediaMetadataPersistence() {
        config = GroupProxy.get(MetadataConfiguration.class);
    }

    public String getDescription() {
        return "Smart Peristence";
    }

    public String getId() {
        return "smart";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        return null;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        /**
         * if options.createProperties==true || isRemote
         * -- write properties persistence
         * 
         * if (options.updateWiz || options.importAsTV)
         * -- sage import persistence
         * 
         * -- sage custome metadata persistence
         * 
         * if central fanart
         * -- sage central fanart persistent
         */
        SageTVPropertiesPersistence props = new SageTVPropertiesPersistence();
        SageCustomMetadataPersistence sageExtra = new SageCustomMetadataPersistence();
        CentralFanartPersistence fanart = new CentralFanartPersistence();
        SageShowPeristence sageShow = new SageShowPeristence();
        
        if (options.isCreateProperties()) {
            props.storeMetaData(md, mediaFile, options);
        }
        
        if (options.isUpdateWizBin() || options.isImportAsTV()) {
            sageShow.storeMetaData(md, mediaFile, options);
        }
        
        sageExtra.storeMetaData(md, mediaFile, options);
        
        fanart.storeMetaData(md, mediaFile, options);
        
        // if we need to touch files, the do so...
        if (options.isTouchingFiles()) {
            // TODO: Touch Files to the date/time of the recording (or current time +1ms)
            mediaFile.touch(mediaFile.lastModified()+1);
        }
    }
}
