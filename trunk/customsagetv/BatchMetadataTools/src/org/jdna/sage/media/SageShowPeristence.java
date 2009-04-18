package org.jdna.sage.media;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

public class SageShowPeristence implements IMediaMetadataPersistence {
    private static final Logger log = Logger.getLogger(SageShowPeristence.class);
    
    public String getDescription() {
        return "Loads/Save metadata directly to a Sage Show Object";
    }

    public String getId() {
        return "sage";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        log.debug("loadMetadata() not yet supported."); 
        return null;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        if (!(mediaFile instanceof SageMediaFile)) {
            log.error("Currently the Sage Show Persistence can only work on native Sage Media files.");
            return;
        }

        log.debug("Storing Sage Metdata directly to the Sage SHOW object");
        Object sageMF = ((SageMediaFile)mediaFile).getSageMediaFile();
        Object airing = MediaFileAPI.GetMediaFileAiring(sageMF);
        Object origShow  = AiringAPI.GetShow(airing);

        log.debug("Setting default values");
        
        String title = (String)md.get(MetadataKey.DISPLAY_TITLE);
        boolean firstRun = ShowAPI.IsShowFirstRun(airing);
        String episode=(String)md.get(MetadataKey.EPISODE_TITLE);
        String description=md.getDescription();
        long duration=ShowAPI.GetShowDuration(origShow);
        String cat1=ShowAPI.GetShowCategory(origShow);
        String cat2=ShowAPI.GetShowSubCategory(origShow);;
        String actors[]=null;
        String roles[]=null;
        String mpaaRated=(String)md.get(MetadataKey.MPAA_RATING);
        String mpaaExpandedRatings[]=null;
        String year=(String)md.get(MetadataKey.YEAR);
        String parentRating=ShowAPI.GetShowParentalRating(origShow);
        String[] miscList=null;
        String externalID=(String)md.get(MetadataKey.MEDIA_PROVIDER_DATA_ID);
        String language=ShowAPI.GetShowLanguage(origShow);
        long origAirDate=ShowAPI.GetOriginalAiringDate(origShow);

        String genre[] = md.getGenres();
        if (genre!=null && genre.length>0) {
            cat1 = genre[0];
        }
        if (genre!=null && genre.length>1) {
            cat2 = genre[1];
        }
        
        if (!StringUtils.isEmpty((String)md.get(MetadataKey.MPAA_RATING_DESCRIPTION))) {
            // NOTE: Even though you set them as an array, when you get them,
            // they show up as Nudity, Violence, and Language, we'll need a parser to
            // get back the original values
            mpaaExpandedRatings = new String[] {(String)md.get(MetadataKey.MPAA_RATING_DESCRIPTION)};
        }
        
        if (md.getCastMembers(ICastMember.ALL)!=null) {
            List<String> l = new LinkedList<String>();
            List<String> rl = new LinkedList<String>();
            for (ICastMember cm : md.getCastMembers(ICastMember.ACTOR)) {
                log.debug("Adding CastMember: " + cm.getName());
                l.add(cm.getName());
                rl.add("Actor");
            }
            for (ICastMember cm : md.getCastMembers(ICastMember.DIRECTOR)) {
                log.debug("Adding Director: " + cm.getName());
                l.add(cm.getName());
                rl.add("Director");
            }
            for (ICastMember cm : md.getCastMembers(ICastMember.WRITER)) {
                log.debug("Adding Writer: " + cm.getName());
                l.add(cm.getName());
                rl.add("Writer");
            }
            
            if (l.size()>0) {
                log.debug("Adding " + l.size() + " actors");
                actors =  l.toArray(new String[l.size()]);
                roles = rl.toArray(new String[rl.size()]);
            } else {
                log.warn("No Actors");
            }
        }

        /*
        List<String> miscStuff = new LinkedList<String>();
        miscStuff.add("MDID");
        miscStuff.add((String)md.get(MetadataKey.MEDIA_PROVIDER_DATA_ID));
        
        miscList = miscStuff.toArray(new String[miscStuff.size()]);
        */
        
        log.debug("Creating new Show...");
        Object show = ShowAPI.AddShow(title, firstRun, episode, description, duration, cat1, cat2, actors, roles, mpaaRated, mpaaExpandedRatings, year, parentRating, miscList, externalID, language, origAirDate);
        
        if (show==null) {
            log.error("Failed to create a new Show using the provided metadata!");
            return;
        }
        
        log.debug("Adding new show to mediafile");
        MediaFileAPI.SetMediaFileShow(sageMF, show);
    }
}
