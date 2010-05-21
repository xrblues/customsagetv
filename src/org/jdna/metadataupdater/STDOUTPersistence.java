package org.jdna.metadataupdater;

import java.io.IOException;
import java.util.Map;

import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;

import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.util.PathUtils;

public class STDOUTPersistence implements IMediaMetadataPersistence {
    public String getDescription() {
        return "Simple STDOUT Pesistence";
    }

    public String getId() {
        return "stdout";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        return new MediaMetadata();
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        System.out.println("\nBegin Properties for Media File: " + PathUtils.getLocation(mediaFile));
        Map<String,String> props = SageTVPropertiesPersistence.getSageTVMetadataMap((IMediaFile) mediaFile, md, options);
        if (props!=null) {
            for (Map.Entry<String, String> me : props.entrySet()) {
                System.out.printf("   %s=%s\n", me.getKey(), me.getValue());
            }
        } else {
            System.out.println("No Metadata");
        }
        System.out.println("End Properties for Media File: " + PathUtils.getLocation(mediaFile));
        
        System.out.println("\nBegin Raw Metadata Dump for File: " + PathUtils.getLocation(mediaFile));
        for (MetadataKey k : MetadataKey.values()) {
            System.out.printf("  %s=%s\n", k.name(), md.getString(k));
        }
        System.out.println("Begin Raw Metadata Dump for File: " + PathUtils.getLocation(mediaFile));
        System.out.println("\nBegin Raw Metadata Dump of Fanart for File: " + PathUtils.getLocation(mediaFile));
        for (IMediaArt ma : md.getFanart()) {
            System.out.printf("  Provider: %s\n", ma.getProviderId());
            System.out.printf("      Type: %s\n", ma.getType());
            System.out.printf("    Season: %s\n", ma.getSeason());
            System.out.printf("       Url: %s\n\n", ma.getDownloadUrl());
        }
        System.out.println("Begin Raw Metadata Dump of Fanart for File: " + PathUtils.getLocation(mediaFile));
    }
}
