package org.jdna.sage;

import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.sage.media.SageMediaFile;

import sagex.phoenix.fanart.MediaArtifactType;

public class MissingFanartFilter implements IMediaResourceFilter {
    private MediaArtifactType fanart = null;
    
    public MissingFanartFilter(MediaArtifactType fanart) {
        this.fanart=fanart;
    }
    
    public boolean accept(IMediaResource resource) {
        Object file = SageMediaFile.getSageMediaFileObject(resource);
        if (file != null) {
            if (fanart == MediaArtifactType.POSTER) {
                return !phoenix.api.HasFanartPoster(file);
            }
            if (fanart == MediaArtifactType.BACKGROUND) {
                return !phoenix.api.HasFanartBackground(file);
            }
            if (fanart == MediaArtifactType.BANNER) {
                return !phoenix.api.HasFanartBanner(file);
            }
        }
        return false;
    }

}
