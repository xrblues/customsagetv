package org.jdna.media.metadata;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.util.Similarity;

public class MetadataUtil {
    private static final Logger log = Logger.getLogger(MetadataUtil.class);
    
    public static final String                                   MOVIE_MEDIA_TYPE = "Movie";
    public static final String                                   TV_MEDIA_TYPE    = "TV";

    private static String compressedRegex = "[^a-zA-Z]+";

    /**
     * Given a metadata id, id:###, return 2 parts, the id, and the ####
     * 
     * if the id is not a valid id, then only a 1 element array will be returned.
     * 
     * @param id
     * @return
     */
    public static String[] getMetadataIdParts(String id) {
        if (id==null) return null;
        String parts[] = id.split(":");
        if (parts==null || parts.length!=2) {
            return new String[] {id};
        }
        return parts;
    }

    /**
     * Udpates the MediaType in the metadata, in the even that it has not been set.
     * 
     * @param md
     */
    public static void updateMetadataMediaType(IMediaMetadata md) {
        if (md==null) return;
        // set the media type, if it's not set
        if (StringUtils.isEmpty((String) md.getString(MetadataKey.MEDIA_TYPE))) {
            if (StringUtils.isEmpty((String) md.getString(MetadataKey.SEASON))) {
                md.setString(MetadataKey.MEDIA_TYPE, MOVIE_MEDIA_TYPE);
            } else {
                md.setString(MetadataKey.MEDIA_TYPE, TV_MEDIA_TYPE);
            }
        }
    }

    /**
     * Return the best score for a title when compared to the search string.  It uses 2 passes to find the best match.
     * the first pass uses the matchTitle as is, and the second pass uses the matchTitle will non search characters removed.
     * 
     * @param searchTitle
     * @param matchTitle
     * @return the best out of the 2 scored attempts
     */
    public static float calculateScore(String searchTitle, String matchTitle) {
        float score1 = Similarity.getInstance().compareStrings(searchTitle, matchTitle);
        float score2 = Similarity.getInstance().compareStrings(searchTitle, MediaMetadataUtils.removeNonSearchCharacters(matchTitle));
        return Math.max(score1, score2);
    }

    /**
     * Return the best score for a title when compared to the search string.  It uses 3 passes to find the best match.
     * the first pass uses the matchTitle as is, and the second pass uses the matchTitle will non search characters removed, and
     * the 3rd pass uses a compressed title search.
     * 
     * Compressed Scoring is useful when you are comparing a Sage recording (csimiami to "CSI: Miami")
     * 
     * @param searchTitle
     * @param matchTitle
     * @return the best out of the 3 scored attempts
     */
    public static float calculateCompressedScore(String searchTitle, String matchTitle) {
        float score1 = calculateScore(searchTitle, matchTitle);
        if (searchTitle==null || matchTitle==null) return score1;
        
        float score2 = Similarity.getInstance().compareStrings(searchTitle.replaceAll(compressedRegex, ""), matchTitle.replaceAll(compressedRegex, ""));
        return Math.max(score1, score2);
    }

    /**
     * Sets the RELEASE_DATE in a consistent YYYY-MM-dd format using the passed in {@link SimpleDateFormat} mask that is passed in.
     * 
     * @param md metadata object
     * @param strDate date in
     * @param dateInFormat date in format using {@link SimpleDateFormat} notation
     */
    public static void setReleaseDateFromFormattedDate(IMediaMetadata md, String strDate, String dateInFormat) {
        if (strDate==null || dateInFormat==null) {
            return;
        }
        
        try {
            DateFormat dateOutFormat  = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateInParser = new SimpleDateFormat(dateInFormat);
            
            Date in = dateInParser.parse(strDate);
            String out = dateOutFormat.format(in);

            md.setString(MetadataKey.RELEASE_DATE, out);
        } catch (Exception e) {
            log.error("Failed to parse/format release dates; dateIn: " + strDate + "; dateInFormat: " + dateInFormat, e);
            md.setString(MetadataKey.RELEASE_DATE, null);
        }
    }
    
    public static Date getReleaseDate(IMediaMetadata md) {
        if (md==null) return null;
        String date = md.getString(MetadataKey.RELEASE_DATE);
        if (date==null) return null;
        
        try {
            DateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd");
            Date d = dateFormat.parse(date);
            return d;
        } catch (Exception e) {
            log.error("Failed to get a date from the metadata date: " + date, e);
        }
        return null;
    }
    
    public static String convertTimeToMillissecondsForSage(String time) {
        return String.valueOf(NumberUtils.toLong(time) * 60 * 1000);
    }
    
    public static String parseRunningTime(String in, String regex) {
        if (in==null || regex==null) return null;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(in);
        if (m.find()) {
            return convertTimeToMillissecondsForSage(m.group(1));
        } else {
            log.warn("Could not find Running Time in " + in + "; using Regex: " + regex);
            return null;
        }
    }
    
    public static String getBareTitle(String name) {
       if (name != null) return name.replaceAll("[^A-Za-z0-9']", " ");
       return name;
    }
}
