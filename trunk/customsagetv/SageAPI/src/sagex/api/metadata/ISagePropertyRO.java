package sagex.api.metadata;

import java.util.Date;

public interface ISagePropertyRO extends ISageMetadata {
    @SageProperty("Width")
    public int getWidth();

    @SageProperty("Height")
    public int getHeight();

    @SageProperty("Track")
    public int getTrack();

    @SageProperty("TotalTracks")
    public int getTotalTracks();
    
    @SageProperty("Comment")
    public int getComment();
    
    @SageProperty("AiringTime")
    public Date getAiringTime();
    
    @SageProperty("ThumbnailOffset")
    public int getThumbnailOffset();
    
    @SageProperty("ThumbnailSize")
    public int getThumbnailSize();

    @SageProperty("ThumbnailDesc")
    public int getThumbnailDesc();

    @SageProperty("Duration")
    public long getDuration();
}
