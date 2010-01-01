package org.jdna.sage;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.metadata.impl.sage.SageProperty;

import sagex.api.MediaFileAPI;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.filters.IResourceFilter;

/**
 * Return true if the media resource does not have the MediaTitle or MediaType
 * 
 * @author seans
 *
 */
public class MissingMetadataFilter implements IResourceFilter {
    public MissingMetadataFilter() {
    }
    
    public boolean accept(IMediaResource resource) {
        Object mf = phoenix.api.GetSageMediaFile(resource);
        if (mf!=null) {
            return StringUtils.isEmpty(MediaFileAPI.GetMediaFileMetadata(mf, SageProperty.MEDIA_TITLE.sageKey)) || StringUtils.isEmpty(MediaFileAPI.GetMediaFileMetadata(mf, SageProperty.MEDIA_TYPE.sageKey));
        }
        return false;
    }
}
