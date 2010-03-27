package sagex.api.metadata;

import java.util.Date;

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
public interface ISagePropertyRW extends ISageMetadata {
    @SageProperty("Track")
    public int getTrack();
    @SageProperty("Track")
    public void setTrack(int track);

    @SageProperty("Title")
    public String getTitle();
    @SageProperty("Title")
    public void setTitle(String title);
    
    @SageProperty("EpisodeName")
    public String getEpisodeName();
    @SageProperty("EpisodeName")
    public void setEpisodeName(String name);
    
    @SageProperty("Album")
    public String getAlbum();
    @SageProperty("Album")
    public void getAlbum(String album);
    
    @SageProperty("Genre")
    public String getGenre();
    @SageProperty("Genre")
    public void setGenre(String genre);
    
    @SageProperty("GenreID")
    public String getGenreID();
    @SageProperty("GenreID")
    public void setGenreID(String genreId);
    
    @SageProperty("Description")
    public String getDescription();
    @SageProperty("Description")
    public void setDescription(String desc);
    
    @SageProperty("Year")
    public int getYear();
    @SageProperty("Year")
    public void setYear(int year);
    
    @SageProperty("Language")
    public String getLanguage();
    @SageProperty("Language")
    public void setLanguage(String lang);
    
    @SageProperty("Rated")
    public String getRated();
    @SageProperty("Rated")
    public void setRated(String rated);
    
    @SageProperty("RunningTime")
    public long getRunningTime();
    @SageProperty("RunningTime")
    public void setRunningTime(long time);
    
    @SageProperty("OriginalAirDate")
    public Date getOriginalAirDate();
    @SageProperty("OriginalAirDate")
    public void setOriginalAirDate(Date date);
    
    @SageProperty("ExtendedRatings")
    public String getExtendedRatings();
    @SageProperty("ExtendedRatings")
    public void setExtendedRatings(String ratings);
    
    @SageProperty("Misc")
    public String getMisc();
    @SageProperty("Misc")
    public void setMisc(String misc);
    
    @SageProperty("PartNumber")
    public int getPartNumber();
    @SageProperty("PartNumber")
    public void setPartNumber(int part);
    
    @SageProperty("TotalParts")
    public int getTotalParts();
    @SageProperty("TotalParts")
    public void getTotalParts(int parts);
    
    @SageProperty("HDTV")
    public boolean isHDTV();
    @SageProperty("HDTV")
    public void setHDTV(boolean hdtv);
    
    @SageProperty("CC")
    public boolean isCC();
    @SageProperty("CC")
    public void setCC(boolean cc);
    
    @SageProperty("Stereo")
    public boolean getStereo();
    @SageProperty("Stereo")
    public void setStereo(boolean stereo);
    
    @SageProperty("Subtitled")
    public boolean isSubtitled();
    @SageProperty("Subtitled")
    public void setSubtitled(boolean subs);
    
    @SageProperty("Premiere")
    public boolean getPremiere();
    @SageProperty("Premiere")
    public void setPremiere(boolean prem);
    
    @SageProperty("SeasonPremiere")
    public boolean isSeasonPremiere();
    @SageProperty("SeasonPremiere")
    public void setSeasonPremiere(boolean prem);
    
    @SageProperty("SeriesPremiere")
    public boolean isSeriesPremiere();
    @SageProperty("SeriesPremiere")
    public void setSeriesPremiere(boolean prem);
    
    @SageProperty("ChannelPremiere")
    public boolean isChannelPremiere();
    @SageProperty("ChannelPremiere")
    public void setChannelPremiere(boolean prem);
    
    @SageProperty("SeasonFinal")
    public boolean isSeasonFinal();
    @SageProperty("SeasonFinal")
    public void setSeasonFinal(boolean fin);
    
    @SageProperty("SeriesFinale")
    public boolean isSeriesFinale();
    @SageProperty("SeriesFinale")
    public void setSeriesFinale(boolean fin);

    @SageProperty("SAP")
    public boolean isSAP();
    @SageProperty("SAP")
    public void setSAP(boolean sap);

    @SageProperty("ParentalRating")
    public String getParentalRating();
    @SageProperty("ParentalRating")
    public void setParentalRating(String rate);
    
    @SageProperty("ExternalID")
    public String getExternalID();
    @SageProperty("ExternalID")
    public void setExternalID(String id);
}
