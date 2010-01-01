package org.jdna.sage.media;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.metadata.impl.sage.SagePropertyType;

import sagex.phoenix.fanart.SageFanartUtil;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;

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
        Object sageMF = phoenix.api.GetSageMediaFile(mediaFile);
        
        if (sageMF != null) {
            for (SageProperty p : SageProperty.values()) {
                if (p.propertyType == SagePropertyType.EXTENDED) {
                    String val = SageFanartUtil.GetMediaFileMetadata(sageMF, p.sageKey);
                    if (!StringUtils.isEmpty(val)) {
                        String sval = val;
                        if ((p == SageProperty.SEASON_NUMBER || p == SageProperty.EPISODE_NUMBER || p == SageProperty.DISC)) {
                            int nval = NumberUtils.toInt(val, 0);
                            if (nval != 0) {
                                sval = String.valueOf(nval);
                            } else {
                                sval = "";
                            }
                        }
                        md.set(p.metadataKey, sval);
                    }
                }
            }
        }

        return md;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        Object sageMF = phoenix.api.GetSageMediaFile(mediaFile);
        if (sageMF != null) {
            MetadataAPI.normalizeMetadata((IMediaFile) mediaFile, md, options);
            for (SageProperty p : SageProperty.values()) {
                if (p.propertyType == SagePropertyType.EXTENDED) {
                    String val = md.getString(p.metadataKey);
                    String sval = val;
                    if (StringUtils.isEmpty(val)) {
                        sval = "";
                    }
                    if ((p == SageProperty.SEASON_NUMBER || p == SageProperty.EPISODE_NUMBER || p == SageProperty.DISC)) {
                        int nval = NumberUtils.toInt(val, 0);
                        if (nval == 0) {
                            sval = "";
                        } else {
                            sval = String.valueOf(nval);
                        }
                    }
                    SageFanartUtil.SetMediaFileMetadata(sageMF, p.sageKey, sval);
                }
            }
        }
    }
}
