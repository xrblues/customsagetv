package org.jdna.util;

import org.jdna.media.metadata.CompositeMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesWithCentralFanartPersistence;
import org.jdna.sage.RenameMediaFile;
import org.jdna.sage.media.SageCustomMetadataPersistence;
import org.jdna.sage.media.SageShowPeristence;

public class PersistenceFactory {
    public static IMediaMetadataPersistence getOnDemandPersistence() {
       return new CompositeMediaMetadataPersistence(
               new SageTVPropertiesPersistence(), 
               new SageCustomMetadataPersistence(), 
               new CentralFanartPersistence(), 
               new SageShowPeristence()
           );
    }
    
    public static IMediaMetadataPersistence getWebSavePersistence() {
        return null;   
    }

    public static IMediaMetadataPersistence getWebLoadPersistence() {
        return null;
    }
    
    public static IMediaMetadataPersistence getAutomaticPluginPersistence() {
        return new SageTVPropertiesWithCentralFanartPersistence();
    }
    
    public static IMediaMetadataPersistence getCommandlinePersistence() {
        return new CompositeMediaMetadataPersistence(
             new RenameMediaFile(), 
             new SageTVPropertiesWithCentralFanartPersistence()
        );
    }
    
    public static IMediaMetadataPersistence getFanartOnlyPersistence() {
        return new CentralFanartPersistence();
    }
    
    public static IMediaMetadataPersistence getPropertiesPersistence() {
        return new SageTVPropertiesPersistence();
    }
}
