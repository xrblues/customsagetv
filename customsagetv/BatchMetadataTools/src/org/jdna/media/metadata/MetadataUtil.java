package org.jdna.media.metadata;

import org.apache.commons.lang.StringUtils;

public class MetadataUtil {
    public static final String                                   MOVIE_MEDIA_TYPE = "Movie";
    public static final String                                   TV_MEDIA_TYPE    = "TV";

    /**
     * Given a metadata id, id:###, return 2 parts, the id, and the ####
     * 
     * if the id is not a valid id, then only a 1 element array will be returned.
     * 
     * @param id
     * @return
     */
    public static String[] getMetadataIdParts(String id) {
        if (id==null) return null;
        String parts[] = id.split(":");
        if (parts==null || parts.length!=2) {
            return new String[] {id};
        }
        return parts;
    }

    /**
     * Udpates the MediaType in the metadata, in the even that it has not been set.
     * 
     * @param md
     */
    public static void updateMetadataMediaType(IMediaMetadata md) {
        if (md==null) return;
        // set the media type, if it's not set
        if (StringUtils.isEmpty((String) md.get(MetadataKey.MEDIA_TYPE))) {
            if (StringUtils.isEmpty((String) md.get(MetadataKey.SEASON))) {
                md.set(MetadataKey.MEDIA_TYPE, MOVIE_MEDIA_TYPE);
            } else {
                md.set(MetadataKey.MEDIA_TYPE, TV_MEDIA_TYPE);
            }
        }
    }
}
