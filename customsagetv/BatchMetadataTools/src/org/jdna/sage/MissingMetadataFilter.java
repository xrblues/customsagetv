package org.jdna.sage;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.sage.media.SageMediaFile;

import sagex.api.MediaFileAPI;

/**
 * Return true if the media resource does not have the MediaTitle or MediaType
 * 
 * @author seans
 *
 */
public class MissingMetadataFilter implements IMediaResourceFilter {
    public MissingMetadataFilter() {
    }
    
    public boolean accept(IMediaResource resource) {
        if (resource instanceof SageMediaFile) {
            Object mf = SageMediaFile.getSageMediaFileObject(resource);
            return !StringUtils.isEmpty(MediaFileAPI.GetMediaFileMetadata(mf, SageProperty.MEDIA_TITLE.sageKey)) && !StringUtils.isEmpty(MediaFileAPI.GetMediaFileMetadata(mf, SageProperty.MEDIA_TYPE.sageKey));
        }
        return false;
    }

}
