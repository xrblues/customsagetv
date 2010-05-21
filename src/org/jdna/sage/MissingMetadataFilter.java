package org.jdna.sage;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.metadata.impl.sage.SageProperty;

import sagex.api.MediaFileAPI;
import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.filters.IResourceFilter;

public class MissingMetadataFilter implements IResourceFilter {
    public MissingMetadataFilter() {
    }

    public boolean accept(IMediaResource res) {
        try {
            Object sage = phoenix.api.GetSageMediaFile(res);
            String mt = MediaFileAPI.GetMediaFileMetadata(sage, SageProperty.MEDIA_TYPE.sageKey);
            String title = MediaFileAPI.GetMediaFileMetadata(sage, SageProperty.MEDIA_TITLE.sageKey);
            if (StringUtils.isEmpty(mt)) return true;
            if (mt.equals(MediaType.TV.sageValue())) {
                return StringUtils.isEmpty(title) || 
                StringUtils.isEmpty(MediaFileAPI.GetMediaFileMetadata(sage, SageProperty.SEASON_NUMBER.sageKey)) ||
                StringUtils.isEmpty(MediaFileAPI.GetMediaFileMetadata(sage, SageProperty.EPISODE_TITLE.sageKey))
                ;
            } else {
                return StringUtils.isEmpty(title);
            }
        } catch (Exception e) {
            return true;
        }
    }
}
