package org.jdna.process;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MetadataAPI;

import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.filters.IResourceFilter;

public class MissingMetadataFilter implements IResourceFilter {
    private static Logger log = Logger.getLogger(MissingMetadataFilter.class);
    private IMediaMetadataPersistence persistence;

    public MissingMetadataFilter(IMediaMetadataPersistence persistence) {
        this.persistence = persistence;
    }

    public boolean accept(IMediaResource res) {
        log.debug("Testing MissingMetadataFilter for: " + res);
        if (res==null) return false;
        if (res.isType(MediaResourceType.FOLDER.value())) return true;
        if (res.isType(MediaResourceType.ANY_VIDEO.value())) {
            return isMissingMetadata(persistence, res);
        }
        return false;
    }
    
    public static boolean isMissingMetadata(IMediaMetadataPersistence persistence, IMediaResource resource) {
        if (resource instanceof IMediaFile) {
            try {
                IMediaMetadata md = persistence.loadMetaData(resource);

                if (md == null) {
                    log.debug("No existing metadata for: " + resource);
                    return true;
                }
                
                if (StringUtils.isEmpty(MetadataAPI.getMediaTitle(md)) || StringUtils.isEmpty(MetadataAPI.getMediaType(md))) {
                    log.debug("Missing MediaTile or MediaType for: " + resource);
                    return true;
                }
                
                // if it's a tv episode then check season/episode values
                if (resource.isType(MediaResourceType.TV.value()) 
                        && (StringUtils.isEmpty(MetadataAPI.getSeason(md)) || StringUtils.isEmpty(MetadataAPI.getEpisode(md)))) {
                    log.debug("TV Resource was missing Season or Episode for: " + resource);
                    return true;
                }
            } catch (Exception e) {
                log.warn("Problem trying to determine if there is existing metadata for: " + resource, e);
                return true;
            }
        }
        return false;
    }
}
