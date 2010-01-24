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
    ALBUM("", MetadataKey.ALBUM, SagePropertyType.CORE),
    ALBUM_ARTIST("", MetadataKey.ALBUM_ARTIST, SagePropertyType.CORE),
    ARTIST("", MetadataKey.ARTIST, SagePropertyType.CORE),
    ASPECT_RATION("x-AspectRatio", MetadataKey.ASPECT_RATIO, SagePropertyType.BMT),
    FANART_BACKGROUND("x-Fanart-BackgroundUrl", MetadataKey.BACKGROUND_ART, SagePropertyType.BMT),
    FANART_BANNER("x-Fanart-BannerUrl", MetadataKey.BANNER_ART, SagePropertyType.BMT),
    SERIALIZED_CAST("x-ser-Cast", MetadataKey.CAST_MEMBER_LIST, SagePropertyType.BMT),
    SERIALIZED_GENRES("x-ser-Genres", MetadataKey.GENRE_LIST, SagePropertyType.BMT),
    SERIALIZED_FANART("x-ser-Fanart", MetadataKey.MEDIA_ART_LIST, SagePropertyType.BMT),
    /*SERIALIZED_DESCRIPTION("x-ser-Description", MetadataKey.DESCRIPTION, SagePropertyType.BMT),*/
    COMMENT("", MetadataKey.COMMENT, SagePropertyType.CORE),
    COMPANY("x-Company", MetadataKey.COMPANY, SagePropertyType.BMT),
    COMPOSER("", MetadataKey.COMPOSER, SagePropertyType.CORE),
    DESCRIPTION("Description", MetadataKey.DESCRIPTION, SagePropertyType.CORE),
    DISPLAY_TITLE("Title", MetadataKey.DISPLAY_TITLE, SagePropertyType.CORE),
    DURATION("", MetadataKey.DURATION, SagePropertyType.CORE),
    GENRES("Genre", MetadataKey.GENRE_LIST, SagePropertyType.CORE),
    LANGUAGE("", MetadataKey.LANGUAGE, SagePropertyType.CORE),
    MEDIA_ART("", MetadataKey.MEDIA_ART_LIST, SagePropertyType.BMT),
    PROVIDER_DATA_ID("MediaProviderDataID", MetadataKey.MEDIA_PROVIDER_DATA_ID, SagePropertyType.EXTENDED),
    IMDB_ID("IMDBID", MetadataKey.IMDB_ID, SagePropertyType.EXTENDED),
    MEDIA_TITLE("MediaTitle", MetadataKey.MEDIA_TITLE, SagePropertyType.EXTENDED),
    MEDIA_TYPE("MediaType", MetadataKey.MEDIA_TYPE, SagePropertyType.EXTENDED),
    METADATA_PROVIDER_ID("x-MetadataProviderId", MetadataKey.METADATA_PROVIDER_ID, SagePropertyType.BMT),
    /* METADATA_PROVIDER_DATA_URL("x-MetadataProviderDataUrl", MetadataKey.METADATA_PROVIDER_DATA_URL, SagePropertyType.BMT), */
    MAPP_RATING("Rated", MetadataKey.MPAA_RATING, SagePropertyType.CORE),
    MPAA_RATING_DESCRIPTION("x-MPAARatingDescription", MetadataKey.MPAA_RATING_DESCRIPTION, SagePropertyType.BMT),
    FANART_POSTER("x-Fanart-PosterUrl", MetadataKey.POSTER_ART, SagePropertyType.BMT),
    ORIGINAL_AIR_DATE("OriginalAirDate", MetadataKey.RELEASE_DATE, SagePropertyType.EXTENDED),
    RUNNING_TIME("RunningTime", MetadataKey.RUNNING_TIME, SagePropertyType.CORE),
    TOTAL_TRACKS("", MetadataKey.TOTAL_TRACKS, SagePropertyType.CORE),
    TRACK("", MetadataKey.TRACK, SagePropertyType.CORE),
    EPISODE_TITLE("EpisodeTitle", MetadataKey.EPISODE_TITLE, SagePropertyType.EXTENDED),
    EPISODE_NUMBER("EpisodeNumber", MetadataKey.EPISODE, SagePropertyType.EXTENDED),
    SEASON_NUMBER("SeasonNumber", MetadataKey.SEASON, SagePropertyType.EXTENDED),
    DISC("DiscNumber", MetadataKey.DVD_DISC, SagePropertyType.EXTENDED),
    USER_RATING("UserRating", MetadataKey.USER_RATING, SagePropertyType.EXTENDED),
    WRITERS("Writer", MetadataKey.CAST_MEMBER_LIST, SagePropertyType.CORE, ICastMember.WRITER),
    DIRECTORS("Director", MetadataKey.CAST_MEMBER_LIST, SagePropertyType.CORE, ICastMember.DIRECTOR),
    ACTORS("Actor", MetadataKey.CAST_MEMBER_LIST, SagePropertyType.CORE, ICastMember.ACTOR),
    YEAR("Year", MetadataKey.YEAR, SagePropertyType.CORE),
    FILENAME("x-FileName", null, SagePropertyType.BMT),
    FILEURI("x-FileUri", null, SagePropertyType.BMT);

    public SagePropertyType propertyType;
    public String      sageKey;
    public MetadataKey metadataKey;
    public Object      userData;

    /**
     * Create a normal 2 way mapping
     * 
     * @param id
     * @param mdKey
     */
    SageProperty(String id, MetadataKey mdKey, SagePropertyType type) {
        this.sageKey = id;
        this.metadataKey = mdKey;
        this.propertyType=type;
    }

    /**
     * Create a normal 2 way mapping but also store an extra piece of
     * userdata.
     * 
     * @param id
     * @param mdKey
     * @param userData
     */
    SageProperty(String id, MetadataKey mdKey, SagePropertyType type, Object userData) {
        this.sageKey = id;
        this.metadataKey = mdKey;
        this.userData = userData;
        this.propertyType=type;
    }

    /**
     * Creates an unnamed property.  These are basicaly placeholders, they will not be stored.
     * 
     * @param mdKey
     */
    SageProperty(MetadataKey mdKey) {
        this.sageKey="";
        this.metadataKey=mdKey;
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

        boolean b = mdkeys.size() == MetadataKey.values().length;
        if (!b) {
            System.out.println("=== Begin Sage Properties and Metadata Keys are out of Sync ===");
            for (MetadataKey k : MetadataKey.values()) {
                if (!mdkeys.contains(k)) {
                    System.out.println("   Add " + k + " to SageProperty.");
                }
            }
            
            System.out.println("=== End Sage Properties and Metadata Keys are out of Sync ===");
        }
        return b;
    }
    
    /**
     * Find Property by sage key
     * @param sageKey
     */
    public static SageProperty valueOfSageKey(String sageKey) {
       for (SageProperty p : values()) {
           if (p.sageKey!=null && p.sageKey.equals(sageKey)) {
               return p;
           }
       }
       return null;
    }
    
    /**
     * Returns the MetadataKey for a given SageProperty Key
     * 
     * @param sageProp
     * @return
     */
    public static MetadataKey metadataKey(String sageProp) {
        for (SageProperty p : values()) {
            if (p.sageKey!=null && p.sageKey.equals(sageProp)) {
                return p.metadataKey;
            }
        }
        return null;
    }
}