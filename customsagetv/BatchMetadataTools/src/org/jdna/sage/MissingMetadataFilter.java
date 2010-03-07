package org.jdna.sage;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.metadata.impl.sage.SageProperty;

import sagex.api.MediaFileAPI;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.filters.IResourceFilter;

public class MissingMetadataFilter implements IResourceFilter {
    public MissingMetadataFilter() {
    }

    public boolean accept(IMediaResource res) {
        Object sage = phoenix.api.GetSageMediaFile(res);
        String mt = MediaFileAPI.GetMediaFileMetadata(sage, SageProperty.MEDIA_TYPE.sageKey);
        String title = MediaFileAPI.GetMediaFileMetadata(sage, SageProperty.MEDIA_TITLE.sageKey);
        return StringUtils.isEmpty(mt) || StringUtils.isEmpty(title);
    }
}
