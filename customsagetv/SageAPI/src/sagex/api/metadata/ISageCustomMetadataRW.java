package sagex.api.metadata;

import java.util.Date;

/**
 * Provide support for some common custom fields, This list is not extensive, but it's a start.
 * 
 * <pre>
 * IMDBID;
 * DiscNumber;
 * EpisodeNumber;
 * EpisodeTitle;
 * MediaProviderDataID;
 * MediaTitle;
 * MediaType;
 * OriginalAirDate;
 * SeasonNumber;
 * UserRating;
 * </pre>
 * @author seans
 *
 */
public interface ISageCustomMetadataRW extends ISageMetadata {
    @SageProperty("MediaTitle")
    public String getMediaTitle();
    @SageProperty("MediaTitle")
    public void setMediaTitle(String title);
    
    @SageProperty("MediaType")
    public String getMediaType();
    @SageProperty("MediaType")
    public void setMediaType(String type);
    
    @SageProperty("SeasonNumber")
    public int getSeasonNumber();
    @SageProperty("SeasonNumber")
    public void setSeasonNumber(int num);
    
    @SageProperty("EpisodeNumber")
    public int getEpisodeNumber();
    @SageProperty("EpisodeNumber")
    public void setEpisodeNumber(int num);
    
    @SageProperty("IMDBID")
    public String getIMDBID();
    @SageProperty("IMDBID")
    public void setIMDBID(String id);
    
    @SageProperty("DiscNumber")
    public int getDiscNumber();
    @SageProperty("DiscNumber")
    public void setDiscNumber();
    
    @SageProperty("MediaProviderDataID")
    public String getMediaProviderDataID();
    @SageProperty("MediaProviderDataID")
    public void setMediaProviderDataID(String id);
    
    @SageProperty("UserRating")
    public float getUserRating();
    @SageProperty("UserRating")
    public void setUserRating(float f);
    
    @SageProperty("OriginalAirDate")
    public Date getOriginalAirDate();
    @SageProperty("OriginalAirDate")
    public void setOriginalAirDate(Date d);
    
    @SageProperty("EpisodeTitle")
    public String getEpisodeTitle();
    @SageProperty("EpisodeTitle")
    public void setEpisodeTitle(String title);
}
