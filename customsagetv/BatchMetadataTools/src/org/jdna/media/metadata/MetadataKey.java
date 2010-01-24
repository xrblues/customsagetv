package org.jdna.media.metadata;

public enum MetadataKey {
    MEDIA_TITLE("MediaTitle", ""),
    ALBUM("Album", ""),
    ARTIST("Artist", ""),
    ALBUM_ARTIST("AlbumArtist", ""),
    COMPOSER("Composer", ""),
    TRACK("Track", ""),
    TOTAL_TRACKS("TotalTracks", ""),
    YEAR("Year", ""),
    COMMENT("Comment", ""),
    GENRE_LIST("GenreList", ""),
    LANGUAGE("Language", ""),
    MPAA_RATING("MPAARating", ""),
    MPAA_RATING_DESCRIPTION("MPAARatingDescription", ""),
    USER_RATING("UserRating", ""),
    RUNNING_TIME("RunningTime", ""),
    DURATION("Duration", ""),
    DESCRIPTION("Description", ""),
    CAST_MEMBER_LIST("CastMemberList", ""),
    POSTER_ART("PosterArt", ""),
    BACKGROUND_ART("BackgroundArt", ""),
    BANNER_ART("BannerArt",""),
    MEDIA_ART_LIST("MediaArtList", ""),
    ASPECT_RATIO("AspectRatio", ""),
    COMPANY("Company", ""),
    MEDIA_PROVIDER_DATA_ID("MediaProviderDataID", ""),
    RELEASE_DATE("OriginalAirDate", ""),
    EPISODE("EpisodeNumber", ""),
    EPISODE_TITLE("EpisodeTitle",""),
    SEASON("SeasonNumber", ""), 
    MEDIA_TYPE("MediaType",""),
    DVD_DISC("Disc", ""), 
    METADATA_PROVIDER_ID("MetadataProviderId", ""),
    /* METADATA_PROVIDER_DATA_URL("MetadataProviderDataUrl", ""), */
    IMDB_ID("IMDBId",""),
    DISPLAY_TITLE("Title","");
    
    private String id, desc;

    MetadataKey(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return desc;
    }

    public static MetadataKey valueOfId(String id) throws RuntimeException {
        for (MetadataKey k : MetadataKey.values()) {
            if (k.getId().equals(id)) return k;
        }
        throw new RuntimeException("No Enum in MetadataKey for Id: " + id);
    }
}
