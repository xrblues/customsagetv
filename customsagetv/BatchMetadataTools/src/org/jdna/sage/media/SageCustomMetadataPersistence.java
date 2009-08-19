package org.jdna.sage.media;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.metadata.impl.sage.SagePropertyType;

import sagex.phoenix.fanart.SageFanartUtil;

/**
 * Persistence that stores and loads ONLY the SageTV custom metadata fields.
 * This persistence is used to update and existing SageTV MediaFile with new
 * Custom Metadata Fields.
 * 
 * @author seans
 */
public class SageCustomMetadataPersistence implements IMediaMetadataPersistence {

    public SageCustomMetadataPersistence() {
    }

    public String getDescription() {
        return "Store/Load from the SageTV custom_metadata_properties Properties";
    }

    public String getId() {
        return "sageCustomMetadata";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        MediaMetadata md = new MediaMetadata();
        if (mediaFile instanceof SageMediaFile) {
            SageMediaFile smf = (SageMediaFile) mediaFile;
            for (SageProperty p : SageProperty.values()) {
                if (p.propertyType == SagePropertyType.EXTENDED) {
                    String val = SageFanartUtil.GetMediaFileMetadata(smf.getSageMediaFileObject(smf), p.sageKey);
                    if (val != null) {
                        if ("0".equals(val) && (p == SageProperty.SEASON_NUMBER || p == SageProperty.EPISODE_NUMBER || p==SageProperty.DISC)) {
                            // don't store 0 in those fields
                            md.set(p.metadataKey, "");
                        } else {
                            md.set(p.metadataKey, val);
                        }
                    }
                }
            }
        }
        
        return md;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        if (mediaFile instanceof SageMediaFile) {
            SageMediaFile smf = (SageMediaFile) mediaFile;
            MetadataAPI.normalizeMetadata(smf, md);
            for (SageProperty p : SageProperty.values()) {
                if (p.propertyType == SagePropertyType.EXTENDED) {
                    String val = md.getString(p.metadataKey);
                    if (val instanceof String && !StringUtils.isEmpty((String) val)) {
                        if ("0".equals(val) && (p == SageProperty.SEASON_NUMBER || p == SageProperty.EPISODE_NUMBER || p==SageProperty.DISC)) {
                            // don't store 0 in those fields
                            SageFanartUtil.SetMediaFileMetadata(smf.getSageMediaFileObject(smf), p.sageKey, "");
                        } else {
                            SageFanartUtil.SetMediaFileMetadata(smf.getSageMediaFileObject(smf), p.sageKey, (String) val);
                        }
                    }
                }
            }
        }
    }
}
