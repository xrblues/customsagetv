package org.jdna.sage;

import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesWithCentralFanartPersistence;

public class MetadataPluginOptions {
    private static IMediaMetadataPersistence persistence;
    private static IMediaMetadataPersistence fanartOnlyPersistence;
    private static PersistenceOptions options;
    
    public static IMediaMetadataPersistence getPersistence() {
        if (persistence==null) {
            persistence = new SageTVPropertiesWithCentralFanartPersistence();
        }
        return persistence;
    }
    
    public static IMediaMetadataPersistence getFanartPersistence() {
        if (fanartOnlyPersistence==null) {
            // TODO: later, once full metadata support is available
            fanartOnlyPersistence = new CentralFanartPersistence();
        }
        return fanartOnlyPersistence;
    }
    
    public static PersistenceOptions getPersistenceOptions() {
        if (options==null) {
            options = new PersistenceOptions();
            options.setOverwriteFanart(false);
            options.setOverwriteMetadata(true);
        }
        return options;
    }
}
