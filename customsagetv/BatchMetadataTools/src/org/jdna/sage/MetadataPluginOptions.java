package org.jdna.sage;

import org.jdna.media.metadata.CompositeMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.sage.media.SageCustomMetadataPersistence;
import org.jdna.sage.media.SageShowPeristence;

public class MetadataPluginOptions {
    private static IMediaMetadataPersistence onDemandPersistence;
    private static IMediaMetadataPersistence fanartOnlyPersistence;
    
    /**
     * Used for On-Demand lookups of regular media files; ie, Non-Epg items
     * @return
     */
    public static IMediaMetadataPersistence getOnDemandUpdaterPersistence() {
        if (onDemandPersistence==null) {
            onDemandPersistence = new CompositeMediaMetadataPersistence(new SageTVPropertiesPersistence(), new SageCustomMetadataPersistence(), new CentralFanartPersistence(), new SageShowPeristence());
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
            fanartOnlyPersistence = new CentralFanartPersistence();
        }
        return fanartOnlyPersistence;
    }
}
