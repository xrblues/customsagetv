package org.jdna.sage.media;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.PersistenceOptions;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.fanart.MediaArtifactType;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.util.PathUtils;

/**
 * Updates the Sage Show Metadata for a TV Episode.
 * 
 * @author seans
 * 
 */
public class SageShowPeristence implements IMediaMetadataPersistence {
    private Random random = new Random(System.currentTimeMillis());
    
    private static final Logger log = Logger.getLogger(SageShowPeristence.class);

    public SageShowPeristence() {
    }
    
    public String getDescription() {
        return "Loads/Save metadata directly to a Sage Show Object";
    }

    public String getId() {
        return "sage";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        MediaMetadata md = new MediaMetadata();
        
        Object file = phoenix.api.GetSageMediaFile(mediaFile);
        if (file==null) {
            log.error("Can only load Sage Metadata For Sage MediaFiles");
            return md;
        }
        
        Object airing = MediaFileAPI.GetMediaFileAiring(file);
        Object show = AiringAPI.GetShow(airing);
        
        String paths[] = phoenix.api.GetFanartBackgrounds(file);
        if (paths!=null) {
            for (String path : paths) {
                MediaArt ma = new MediaArt();
                ma.setDownloadUrl(new File(path).toURI().toString());
                ma.setType(MediaArtifactType.BACKGROUND);
                md.addMediaArt(ma);
            }
        }

        paths = phoenix.api.GetFanartPosters(file);
        if (paths!=null) {
            for (String path : paths) {
                MediaArt ma = new MediaArt();
                ma.setDownloadUrl(new File(path).toURI().toString());
                ma.setType(MediaArtifactType.POSTER);
                md.addMediaArt(ma);
            }
        }

        paths = phoenix.api.GetFanartBanners(file);
        if (paths!=null) {
            for (String path : paths) {
                MediaArt ma = new MediaArt();
                ma.setDownloadUrl(new File(path).toURI().toString());
                ma.setType(MediaArtifactType.BANNER);
                md.addMediaArt(ma);
            }
        }
        
        //md.set(MetadataKey.CAST_MEMBER_LIST, value);
        String people[] = ShowAPI.GetPeopleListInShowInRole(show, "Writer");
        if (people!=null) {
            for (String s : people) {
                CastMember cm = new CastMember(ICastMember.WRITER);
                cm.setName(s.trim());
                md.addCastMember(cm);
            }
        }
        people = ShowAPI.GetPeopleListInShowInRole(show, "Director");
        if (people!=null) {
            for (String s : people) {
                CastMember cm = new CastMember(ICastMember.DIRECTOR);
                cm.setName(s.trim());
                md.addCastMember(cm);
            }
        }
        people = ShowAPI.GetPeopleListInShowInRole(show, "Actor");
        if (people!=null) {
            for (String s : people) {
                CastMember cm = new CastMember(ICastMember.ACTOR);
                String parts[] = s.split("--");
                if (parts!=null&&parts.length>1) {
                    cm.setName(parts[0].trim());
                    cm.setPart(parts[1].trim());
                } else {
                    cm.setName(s.trim());
                }
                md.addCastMember(cm);
            }
        }
        
        String genre = ShowAPI.GetShowCategory(show);
        if (genre!=null) {
            md.getGenres().add(genre);
        }
        md.set(MetadataKey.COMMENT,"");
        md.set(MetadataKey.COMPANY,"");
        md.set(MetadataKey.DESCRIPTION, ShowAPI.GetShowDescription(show));
        MetadataAPI.setDisplayTitle(md, ShowAPI.GetShowTitle(show));
        md.set(MetadataKey.DURATION, String.valueOf(AiringAPI.GetAiringDuration(airing)));
        md.set(MetadataKey.MPAA_RATING, ShowAPI.GetShowRated(show));
        md.set(MetadataKey.LANGUAGE, ShowAPI.GetShowLanguage(show));
        md.set(MetadataKey.RUNNING_TIME, String.valueOf(AiringAPI.GetAiringDuration(airing)));
        md.set(MetadataKey.YEAR, ShowAPI.GetShowYear(show));
        md.set(MetadataKey.ISWATCHED, String.valueOf(AiringAPI.IsWatched(airing)));

        if (MediaFileAPI.IsTVFile(file)) {
            MetadataAPI.setMediaType(md, "TV");
            MetadataAPI.setEpisodeTitle(md, ShowAPI.GetShowEpisode(show));
            MetadataAPI.setMediaTitle(md, ShowAPI.GetShowTitle(show));
        } else {
            MetadataAPI.setMediaTitle(md, ShowAPI.GetShowTitle(show));
            MetadataAPI.setMediaType(md, "Movie");
        }
        
        // dvds use the media title
        if (MediaFileAPI.IsDVD(file) || MediaFileAPI.IsBluRay(file)) {
            MetadataAPI.setMediaTitle(md, MediaFileAPI.GetMediaTitle(file));
            MetadataAPI.setDisplayTitle(md, MediaFileAPI.GetMediaTitle(file));
        }
        
        Date d = new Date(AiringAPI.GetAiringStartTime(airing));
        if (StringUtils.isEmpty(MetadataAPI.getReleaseDate(md))) {
            MetadataAPI.setReleaseDate(md, d);
        }
        
        if (StringUtils.isEmpty(MetadataAPI.getYear(md))) {
            MetadataAPI.setYear(md, d);
        }
        
        if (StringUtils.isEmpty(MetadataAPI.getMPAARating(md))) {
            String ratings[] = AiringAPI.GetAiringRatings(airing);
            if (ratings!=null) {
                MetadataAPI.setMPAARating(md, new StrBuilder().appendWithSeparators(ratings, "; ").toString());
            }
        }
        
        return md;
    }

    /**
     * Some limitations of the storeMetadata
     * - Will not update sagetv native recordings because it loses channel info, etc
     * - Can import recordings, but it will not have channel info
     */
    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        /**
         * Comments from Jeff on ExternalID
         * 
         * The external IDs are unique IDs used by our EPG data provider (Tribune). 
         * The MV, SP, SH and EP are the ones that come from them. DT is for EPG data that comes from the 
         * digital TV stream itself and the ID appended to that is essentially a random hash code. 
         * You can go ahead and make up your own; but the best way to do that and be safe is to put 
         * another character after one of those 2 letter prefixes so then they would never conflict 
         * with one that SageTV uses (since we always put numbers after the 2 char prefix). 
         * And MV=Movie, SP=Sports, SH=Show, EP=Episode and DT=Digital TV data.  SageTV only checks the first 
         * two characters of the ID to see if its intended to be a TV recording or not. For other imported 
         * files SageTV uses an 'MF' prefix for MediaFile and then appends the unique local database 
         * ID for the MediaFile object after that.
         * 
         */
        
        
        if (!options.isUpdateWizBin()) {
            log.warn("Updating of Wiz.Bin is disabled.");
            return;
        }
        
        Object sageMF = phoenix.api.GetSageMediaFile(mediaFile);
        if (sageMF == null) {
            log.error("Currently the Sage Show Persistence can only work on native Sage Media files.");
            return;
        }
        
        // small check to ensure that we don't update core sagetv TV objects in the wiz.bin.  ie,
        // we assume that native TV file have the correct metadata.
        if (MediaFileAPI.IsTVFile(sageMF)) {
            // don't update metadate for sage native tv files, but we do for imported tv files.
            String extId = ShowAPI.GetShowExternalID(sageMF);
            if (extId!=null) {
                if (extId.length()>4 && extId.charAt(2)=='m' && extId.charAt(3)=='t') {
                    // we can update, since it's our own imported tv
                } else {
                    log.info("SageShowPersistence does not update core sagetv TV objects; skipping: " + extId + "; " + mediaFile);
                    return;
                }
            }
        }
        
        MetadataAPI.normalizeMetadata((IMediaFile)mediaFile, md, options);

        log.info("Storing Sage Metdata directly to the Sage SHOW object for " + mediaFile);
        Object airing = MediaFileAPI.GetMediaFileAiring(sageMF);
        Object origShow = AiringAPI.GetShow(airing);
        
        // test if it's a library file
        boolean isArchived = !MediaFileAPI.IsLibraryFile(sageMF);
        
        // should only import as TV if it's not currently imported as TV
        boolean importAsTV = (options.isImportAsTV() && MetadataAPI.isTV(md)) && !(MediaFileAPI.IsTVFile(sageMF));

        String title = MetadataAPI.getDisplayTitle(md);
        
        if (title==null) {
            log.warn("Title is null for: " + PathUtils.getLocation(mediaFile) + "; aborting.");
            return;
        }

        log.debug("*** Title: " + title);
        boolean firstRun = ShowAPI.IsShowFirstRun(airing);
        String episode = md.getString(MetadataKey.EPISODE_TITLE);
        String description = MetadataAPI.getDescription(md);
        long duration = AiringAPI.GetAiringDuration(airing);
        String cat1 = ShowAPI.GetShowCategory(origShow);
        String cat2 = ShowAPI.GetShowSubCategory(origShow);
        String actors[] = null;
        String roles[] = null;
        String mpaaRated = md.getString(MetadataKey.MPAA_RATING);
        String mpaaExpandedRatings[] = null;
        String year = md.getString(MetadataKey.YEAR);
        String parentRating = ShowAPI.GetShowParentalRating(origShow);
        String[] miscList = null;
        String externalID = null;
        String language = ShowAPI.GetShowLanguage(origShow);
        long origAirDate = ShowAPI.GetOriginalAiringDate(origShow);
        
        if (origShow!=null) {
            // update some items from the original show, if it exists
            firstRun = ShowAPI.IsShowFirstRun(airing);
            cat1 = ShowAPI.GetShowCategory(origShow);
            cat2 = ShowAPI.GetShowSubCategory(origShow);
            parentRating = ShowAPI.GetShowParentalRating(origShow);
            language = ShowAPI.GetShowLanguage(origShow);
            origAirDate = ShowAPI.GetOriginalAiringDate(origShow);
            externalID = ShowAPI.GetShowExternalID(origShow);
        }

        if (origAirDate==0 || options.isOverwriteMetadata()) {
            Date d = MetadataUtil.getReleaseDate(md);
            if (d==null) {
                log.debug("No Original Airing Date, using today");
                origAirDate =  System.currentTimeMillis();
            } else {
                log.debug("Setting Original Airing Date: " + d);
                origAirDate = d.getTime();
            }
        }
        
        if (duration==0) {
            log.debug("No Duration, using duration from media file");
            duration = MediaFileAPI.GetDurationForSegment(sageMF, 0);
        }
        
        List<String> genre = MetadataAPI.getGenres(md);
        if (genre != null && genre.size() > 0) {
            cat1 = genre.get(0);
        }
        if (genre != null && genre.size() > 1) {
            cat2 = genre.get(1);
        }

        if (!StringUtils.isEmpty(md.getString(MetadataKey.MPAA_RATING_DESCRIPTION))) {
            // NOTE: Even though you set them as an array, when you get them,
            // they show up as Nudity, Violence, and Language, we'll need a
            // parser to
            // get back the original values
            mpaaExpandedRatings = new String[] { md.getString(MetadataKey.MPAA_RATING_DESCRIPTION) };
        }

        if (MetadataAPI.getCastMembers(md, ICastMember.ALL) != null) {
            List<String> l = new LinkedList<String>();
            List<String> rl = new LinkedList<String>();
            for (ICastMember cm : MetadataAPI.getCastMembers(md, ICastMember.ACTOR)) {
                log.debug("Adding CastMember: " + cm.getName());
                l.add(cm.getName());
                rl.add("Actor");
            }
            for (ICastMember cm : MetadataAPI.getCastMembers(md, ICastMember.DIRECTOR)) {
                log.debug("Adding Director: " + cm.getName());
                l.add(cm.getName());
                rl.add("Director");
            }
            for (ICastMember cm : MetadataAPI.getCastMembers(md, ICastMember.WRITER)) {
                log.debug("Adding Writer: " + cm.getName());
                l.add(cm.getName());
                rl.add("Writer");
            }

            if (l.size() > 0) {
                log.debug("Adding " + l.size() + " actors");
                actors = l.toArray(new String[l.size()]);
                roles = rl.toArray(new String[rl.size()]);
            } else {
                log.warn("No Actors");
            }
        }

        externalID = createShowId(importAsTV, md, options, externalID);
        log.debug("New ExternalID Created: " + externalID);

        // Sage wants to have the movie titles in the episode field as well
        // failure to set this will mean that a movie in sage stv will not
        // show a title
        if (StringUtils.isEmpty(episode)) {
            episode = title;
        }
        
        log.debug("Creating new Show: " + title);
        log.debug("Type: " + MetadataAPI.getMediaType(md));
        log.debug("Title: " + title);
        log.debug("FirstRun: " + firstRun);
        log.debug("Episode: " + episode);
        log.debug("Description: " + description);
        log.debug("Duration: " + duration);
        log.debug("Cat1: " + cat1);
        log.debug("Cat2: " + cat2);
        log.debug("mpaaRated: " + mpaaRated);
        log.debug("mappRatedDesc: " + ArrayUtils.toString(mpaaExpandedRatings));
        log.debug("Year: " + year);
        log.debug("ParentalRating: " + parentRating);
        log.debug("External Id: " + externalID);
        log.debug("Language: " + language);
        log.debug("Original Air Date: " + origAirDate);
        
        // Addst the show
        Object show = ShowAPI.AddShow(title, firstRun, episode, description, duration, cat1, cat2, actors, roles, mpaaRated, mpaaExpandedRatings, year, parentRating, miscList, externalID, language, origAirDate);

        if (show == null) {
            log.error("Failed to create a new Show using the provided metadata!");
            return;
        }

        // associates the show metadata with the mediafile, and it will create a new airing
        log.debug("Adding new show to mediafile");
        MediaFileAPI.SetMediaFileShow(sageMF, show);

        // update this new airing with information from the old airing
        updateAiring(show, airing, MediaFileAPI.GetMediaFileAiring(sageMF));
        
        // lastly unset the archived flag for the tv, if it's archived
        if (isArchived) {
            MediaFileAPI.MoveTVFileOutOfLibrary(sageMF);
        }
        
        // set the watched flag
        boolean watched = BooleanUtils.toBoolean(md.getString(MetadataKey.ISWATCHED));
        if (watched) {
            AiringAPI.SetWatched(sageMF);
        }
    }
    
    private void updateAiring(Object show, Object airing, Object newAir) {
        if (airing!=null && newAir!=null) {
            if (AiringAPI.IsDontLike(airing)) {
                AiringAPI.SetDontLike(newAir);
            }
            
            AiringAPI.SetRecordingName(newAir, AiringAPI.GetRecordingName(airing));
            AiringAPI.SetRecordingQuality(newAir, AiringAPI.GetRecordingQuality(airing));
            AiringAPI.SetRecordingTimes(newAir, AiringAPI.GetAiringStartTime(airing), AiringAPI.GetAiringEndTime(airing));

            if (AiringAPI.IsWatched(airing)) {
                AiringAPI.SetWatched(newAir);
            }
        }
    }

    private String createShowId(boolean importAsTV, IMediaMetadata md, PersistenceOptions options, String externalID) {
        String prefix = null;
        if (importAsTV) {
            if (MetadataAPI.isTV(md)) {
                prefix = "EPmt";
            } else {
                prefix = "MVmt";
            }
        } else {
            prefix = "MFmt";
        }

        // build a suffix using season and episode, if it exists.
        String suffix = null;
        if (MetadataAPI.isTV(md)) {
            if (!StringUtils.isEmpty(MetadataAPI.getSeason(md)) && !StringUtils.isEmpty(MetadataAPI.getEpisode(md))) {
                suffix = org.jdna.util.StringUtils.zeroPad(MetadataAPI.getSeason(md), 2) + org.jdna.util.StringUtils.zeroPad(MetadataAPI.getEpisode(md), 2);
            } else {
                suffix = Integer.toHexString((int)(random.nextDouble()*0xFFFF));
            }
        } else {
            suffix = Integer.toHexString((int)(random.nextDouble()*0xFFFF));
        }
        
        String id = null;
        do {
            // keep gen'd EPGID less than 12 chars
            // SEANS: Taken from nielm's xmlinfo... I'm guessing there is a 12 char limit, so i won't mess with it
            id = prefix + Integer.toHexString((int)(random.nextDouble()*0xFFFF)) + suffix;
            log.debug("Calculated a showid: " + id);
        } while (ShowAPI.GetShowForExternalID(id) != null);
        return id;
    }
}
