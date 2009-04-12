package org.jdna.sage.media;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.fanart.FanartUtil.MediaType;

public class SageShowPeristence implements IMediaMetadataPersistence {
    private static final Logger log = Logger.getLogger(SageShowPeristence.class);
    
    public String getDescription() {
        return "Loads/Save metadata directly to a Sage Show Object";
    }

    public String getId() {
        return "sage";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        // TODO Auto-generated method stub
        return null;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        if (!(mediaFile instanceof SageMediaFile)) {
            log.error("Currently the Sage Show Persistence can only work on native Sage Media files.");
            return;
        }

        Object sageMF = ((SageMediaFile)mediaFile).getSageMediaFile();
        
        String title = (String)md.get(MetadataKey.DISPLAY_TITLE);
        boolean firstRun = false;
        String episode=null;
        String description=md.getDescription();
        long duration=0;
        String cat1=null;
        String cat2=null;
        String actors[]=null;
        String roles[]=null;
        String mpaaRated=null;
        String mpaaExpandedRatings[]=null;
        String year=null;
        String parentRating=null;
        String[] miscList=null;
        String externalID=null;
        String language=null;
        long origAirDate=0;
        
        if (md.get(MetadataKey.MEDIA_TYPE) == MediaType.TV) {
            episode = (String)md.get(MetadataKey.EPISODE_TITLE);
        }
        
        // todo: convert running time into duration?
        // or lookup duration from existing show object?
        
        
        
        Object show = ShowAPI.AddShow(title, firstRun, episode, description, duration, cat1, cat2, actors, roles, mpaaRated, mpaaExpandedRatings, year, parentRating, miscList, externalID, language, origAirDate);
        
        //Object airing = AiringAPI.AddAiringDetailed(ShowExternalID, StationID, StartTime, Duration, PartNumber, TotalParts, ParentalRating, HDTV, Stereo, ClosedCaptioning, SAP, Subtitled, PremierFinale)
        MediaFileAPI.SetMediaFileShow(sageMF, show);
    }

}
