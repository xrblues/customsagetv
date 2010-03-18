package sagex.api.metadata;

/**
 * Core SAGETV Metadata Properties...
 * 
 * Some notes as per Jeff @ SageTV
 * 
 * <pre>
 * 14. Updated functionality of SetMediaFileMetadata so that if any of the following names are used for the metadata property it will instead update the Airing or Show object in the database instead that links to the MediaFile (changes to the Airing will cause construction of a new Airing object and relinkage to the MediaFile; while Show objects will be updated in place). GetMediaFileMetadata was not changed; so in these special cases, Get and Set are not mirrors of each other. The metadata names this applies to are: 
 * Track, 
 * Title, 
 * EpisodeName, 
 * Album, 
 * Genre, 
 * GenreID, 
 * Description, 
 * Year, 
 * Language, 
 * Rated, 
 * RunningTime, 
 * OriginalAirDate, 
 * ExtendedRatings, 
 * Misc, 
 * PartNumber, 
 * TotalParts, 
 * HDTV, 
 * CC, 
 * Stereo, 
 * Subtitled, 
 * Premiere, 
 * SeasonPremiere, 
 * SeriesPremiere, 
 * ChannelPremiere, 
 * SeasonFinal, 
 * SeriesFinale, 
 * SAP, 
 * ParentalRating,
 * and all of the 'Roles' for a Show object. A .properties file will be created/appended for the corresponding MediaFile to ensure this data is retained if a rescan of the MediaFiles is performed.
 * 15. Added support for setting the ExternalID for a MediaFile's Show object through the MediaFile API call SetMediaFileMetadata. This will result in a new Show object being built which will then be linked to the MediaFile; this supports converting the MediaFile to a 'TV' type file.
 * </pre>
 * 
 * <pre>
 * Duration is for the actual media duration; which sometimes can't be detected
 * accurately by the format detector (like w/ VBR MP3 files).
 * 
 * AiringDuration is for the duration of the Airing object that wraps a file
 * (since it might be different than the actual file duration)
 * 
 * RunningTime is for things like movies; its a duration stored in the Show
 * object (if you look at the Detailed Info for a movie from the EPG in SageTV
 * you'll see this)
 * 
 * AiringTime is the time the Airing for the MediaFile occurred at.
 * 
 * You can't modify Duration, AiringDuraiton or AiringTime with the
 * SetMediaFileMetadata API call since they'd end up breaking the timeline if
 * we allowed it and there's really no reason to modify them anyways aside from
 * maintaing information from old TV recordings (which we already handle with
 * MPEG metadata embedding)
 * 
 * And you can use the total parts & part number fields; but SageTV won't show
 * them as a single item unless you implement a UI to do it as such. This is
 * not the same as a multi-segment recording.
 * 
 * </pre>
 * 
 * <pre>
 * 7. Added support for the following properties which are automatically determined by the core if not defined when using the GetMediaFileMetadata API call: Format.Video.Height, Format.Video.Width, Format.Video.FPS, Format.Video.Interlaced, Format.Video.Progressive, Format.Audio.NumStreams, Format.Audio.[StreamNum].XXX (where StreamNum < NumStreams and XXX is one of the Format.Audio.XXX properties), Format.Subtitle.NumStreams, Format.Subtitle.[StreamNum].XXX (where StreamNum < NumStreams and XXX is one of the Format.Subtitle.XXX properties), Format.Subtitle.Codec and Format.Container.
 * 17. Added support for the following metadata values (case-insensitive) when using GetMediaFileMetadata if the values are not defined by an import plugin: Format.Video.Codec, Format.Video.Resolution, Format.Video.Aspect, Format.Audio.Codec, Format.Audio.Channels, Format.Audio.Language, Format.Audio.SampleRate, Format.Audio.BitsPerSample, Format.Audio.Bitrate (in kbps), Format.Subtitle.Language, Format.Video.Bitrate (in Mbps)
 * </pre>
 * 
 * @author seans
 * 
 */
public enum SagePropertyRW implements ISageProperty {
    Track(Type.Int),
    Title(),
    EpisodeName(),
    Album(),
    Genre(),
    GenreID(),
    Description(),
    Year(Type.Int),
    Language(),
    Rated(),
    RunningTime(Type.Long),
    OriginalAirDate(Type.Date),
    ExtendedRatings(),
    Misc(),
    PartNumber(Type.Int),
    TotalParts(Type.Int),
    HDTV(Type.Boolean),
    CC(Type.Boolean),
    Stereo(Type.Boolean),
    Subtitled(Type.Boolean),
    Premiere(Type.Boolean),
    SeasonPremiere(Type.Boolean),
    SeriesPremiere(Type.Boolean),
    ChannelPremiere(Type.Boolean),
    SeasonFinal(Type.Boolean),
    SeriesFinale(Type.Boolean),
    SAP(Type.Boolean),
    ParentalRating(),
    ExternalID()
    ;

    private String value;
    private ISageProperty.Type type;
    
    SagePropertyRW() {
        this.value = name();
        this.type=Type.String;
    }

    SagePropertyRW(Type type) {
        this.value = name();
        this.type=type;
    }

    SagePropertyRW(String name) {
        this.value = name;
        this.type=Type.String;
    }

    SagePropertyRW(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public String key() {
        return value;
    }

    public Type type() {
        return type;
    }
}
