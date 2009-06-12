package org.jdna.sage;

import org.jdna.media.metadata.CompositeMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.sage.media.SageCustomMetadataPersistence;

public class MetadataPluginOptions {
    private static IMediaMetadataPersistence onDemandPersistence;
    private static IMediaMetadataPersistence fanartOnlyPersistence;
    private static IMediaMetadataPersistence automaticUpdaterPersistence;
    
    private static PersistenceOptions options;
    
    /**
     * Used for On-Demand lookups of regular media files; ie, Non-Epg items
     * @return
     */
    public static IMediaMetadataPersistence getOnDemandUpdaterPersistence() {
        if (onDemandPersistence==null) {
            //onDemandPersistence = new CompositeMediaMetadataPersistence(new SageTVPropertiesPersistence(), new SageShowPeristence(), new SageCustomMetadataPersistence(), new CentralFanartPersistence());
            // eventually have the SageShowPersistence() included when tv import is there.
            onDemandPersistence = new CompositeMediaMetadataPersistence(new SageTVPropertiesPersistence(), new SageCustomMetadataPersistence(), new CentralFanartPersistence(), new UpdateMediaFileTimeStamp());
        }
        return onDemandPersistence;
    }

    /**
     * Used for On-Demand lookups for Airings (ie, epg data)
     * 
     * @return
     */
    public static IMediaMetadataPersistence getFanartOnlyPersistence() {
        if (fanartOnlyPersistence==null) {
            fanartOnlyPersistence = new CompositeMediaMetadataPersistence(new SageCustomMetadataPersistence(), new CentralFanartPersistence());
        }
        return fanartOnlyPersistence;
    }
    
    public static PersistenceOptions getPersistenceOptions() {
        if (options==null) {
            options = new PersistenceOptions();
            options.setOverwriteFanart(false);
            options.setOverwriteMetadata(false);
        }
        return options;
    }
}
