package org.jdna.media.metadata.impl.sage;

import java.util.Set;
import java.util.TreeSet;

import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.MetadataKey;

/**
 * Provides a crossmapping of Sage Properties to Metadata Properties and
 * vice versa
 * 
 * @author seans
 * 
 */
public enum SageProperty {
    ALBUM("", MetadataKey.ALBUM),
    ALBUM_ARTIST("", MetadataKey.ALBUM_ARTIST),
    ARTIST("", MetadataKey.ARTIST),
    ASPECT_RATION("x-AspectRatio", MetadataKey.ASPECT_RATIO),
    FANART_BACKGROUND("x-Fanart-BackgroundUrl", MetadataKey.BACKGROUND_ART),
    FANART_BANNER("x-Fanart-BannerUrl", MetadataKey.BANNER_ART),
    SERIALIZED_CAST("x-ser-Cast", MetadataKey.CAST_MEMBER_LIST),
    SERIALIZED_GENRES("x-ser-Genres", MetadataKey.GENRE_LIST),
    SERIALIZED_DESCRIPTION("x-ser-Description", MetadataKey.DESCRIPTION),
    COMMENT("", MetadataKey.COMMENT),
    COMPANY("x-Company", MetadataKey.COMPANY),
    COMPOSER("", MetadataKey.COMPOSER),
    DESCRIPTION("Description", MetadataKey.DESCRIPTION),
    DISPLAY_TITLE("Title", MetadataKey.DISPLAY_TITLE),
    DURATION("", MetadataKey.DURATION),
    DISC("x-Disc", MetadataKey.DVD_DISC),
    GENRES("Genre", MetadataKey.GENRE_LIST),
    LANGUAGE("", MetadataKey.LANGUAGE),
    MEDIA_ART("", MetadataKey.MEDIA_ART_LIST),
    PROVIDER_DATA_ID("MediaProviderDataID", MetadataKey.MEDIA_PROVIDER_DATA_ID),
    MEDIA_TITLE("MediaTitle", MetadataKey.MEDIA_TITLE),
    MEDIA_TYPE("MediaType", MetadataKey.MEDIA_TYPE),
    METADATA_PROVIDER_ID("x-MetadataProviderId", MetadataKey.METADATA_PROVIDER_ID),
    METADATA_PROVIDER_DATA_URL("x-MetadataProviderDataUrl", MetadataKey.METADATA_PROVIDER_DATA_URL),
    MAPP_RATING("Rated", MetadataKey.MPAA_RATING),
    MPAA_RATING_DESCRIPTION("x-MPAARatingDescription", MetadataKey.MPAA_RATING_DESCRIPTION),
    FANART_POSTER("x-Fanart-PosterUrl", MetadataKey.POSTER_ART),
    ORIGINAL_AIR_DATE("OriginalAirDate", MetadataKey.RELEASE_DATE),
    RUNNING_TIME("RunningTime", MetadataKey.RUNNING_TIME),
    TOTAL_TRACKS("", MetadataKey.TOTAL_TRACKS),
    TRACK("", MetadataKey.TRACK),
    EPISODE_TITLE("EpisodeTitle", MetadataKey.EPISODE_TITLE),
    EPISODE_NUMBER("EpisodeNumber", MetadataKey.EPISODE),
    SEASON_NUMBER("SeasonNumber", MetadataKey.SEASON),
    USER_RATING("UserRating", MetadataKey.USER_RATING),
    WRITERS("Writer", MetadataKey.CAST_MEMBER_LIST, ICastMember.WRITER),
    DIRECTORS("Director", MetadataKey.CAST_MEMBER_LIST, ICastMember.DIRECTOR),
    ACTORS("Actor", MetadataKey.CAST_MEMBER_LIST, ICastMember.ACTOR),
    YEAR("Year", MetadataKey.YEAR),
    FILENAME("x-FileName", null),
    FILEURI("x-FileUri", null);

    public String      sageKey;
    public MetadataKey metadataKey;
    public Object      userData;

    /**
     * Create a normal 2 way mapping
     * 
     * @param id
     * @param mdKey
     */
    SageProperty(String id, MetadataKey mdKey) {
        this.sageKey = id;
        this.metadataKey = mdKey;
    }

    /**
     * Create a normal 2 way mapping but also store an extra piece of
     * userdata.
     * 
     * @param id
     * @param mdKey
     * @param userData
     */
    SageProperty(String id, MetadataKey mdKey, Object userData) {
        this.sageKey = id;
        this.metadataKey = mdKey;
        this.userData = userData;
    }

    /**
     * Can be used to validate that all metadatakey properties are
     * referenced in the sage property set
     */
    public static boolean isPropertySetValid() {
        Set<MetadataKey> mdkeys = new TreeSet<MetadataKey>();

        for (SageProperty p : values()) {
            if (p.metadataKey != null) {
                mdkeys.add(p.metadataKey);
            }
        }

        return mdkeys.size() == MetadataKey.values().length;
    }
}