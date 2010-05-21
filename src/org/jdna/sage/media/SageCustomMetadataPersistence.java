package org.jdna.sage.media;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
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
    private Logger log = Logger.getLogger(SageCustomMetadataPersistence.class);

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
        log.info("Updating Custom Metadata Fields for: " + mediaFile);
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
                    String curVal = SageFanartUtil.GetMediaFileMetadata(sageMF, p.sageKey);
                    // only overwrite values if they have not been set or you are overwriting fanart or metadata
                    // it is important to update the custom metadata for fanart, since fanart resolving depends
                    // on the custom metadata fields
                    if (StringUtils.isEmpty(curVal) || options.isOverwriteMetadata() || options.isOverwriteFanart()) {
                        SageFanartUtil.SetMediaFileMetadata(sageMF, p.sageKey, sval);
                    }
                }
            }
        }
    }
}
