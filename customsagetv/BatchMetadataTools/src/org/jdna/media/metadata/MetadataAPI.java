package org.jdna.media.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.IMediaFile;
import org.jdna.media.MediaConfiguration;
import org.jdna.media.metadata.impl.imdb.IMDBUtils;
import org.jdna.media.metadata.impl.sage.SageMetadataConfiguration;
import org.jdna.media.metadata.impl.sage.SageProperty;

import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.MediaArtifactType;
import sagex.phoenix.fanart.MediaType;

/**
 * Static class for working with metadata fields in a typesafe manner
 * 
 * @author seans
 *
 */
public class MetadataAPI {
    public static IMediaMetadata copy(IMediaMetadata src, IMediaMetadata dest) {
        for (MetadataKey k : MetadataKey.values()) {
            dest.setString(k, safeTrim(src.getString(k)));
        }
        dest.getCastMembers().addAll(src.getCastMembers());
        dest.getFanart().addAll(src.getFanart());
        dest.getGenres().addAll(src.getGenres());
        return dest;
    }

    private static String safeTrim(String string) {
        if (string==null) return null;
        return string.trim();
    }

    public static IMediaMetadata copyNonNull(IMediaMetadata src, IMediaMetadata dest) {
        for (MetadataKey k : MetadataKey.values()) {
            if (src.getString(k)!=null) {
                dest.setString(k, safeTrim(src.getString(k)));
            }
        }
        dest.getCastMembers().addAll(src.getCastMembers());
        dest.getFanart().addAll(src.getFanart());
        dest.getGenres().addAll(src.getGenres());
        return dest;
    }

    public static String getAspectRatio(IMediaMetadata md) {
        return md.getString(MetadataKey.ASPECT_RATIO);
    }

    public static void setAspectRatio(IMediaMetadata md, String aspectRatio) {
        md.setString(MetadataKey.ASPECT_RATIO, aspectRatio);
    }

    public static String getCompany(IMediaMetadata md) {
        return md.getString(MetadataKey.COMPANY);
    }

    public static void setCompany(IMediaMetadata md,String company) {
        md.setString(MetadataKey.COMPANY, company);
    }

    public static List<String> getGenres(IMediaMetadata md) {
        return md.getGenres();
    }

    public static String getMPAARating(IMediaMetadata md) {
        return md.getString(MetadataKey.MPAA_RATING);
    }

    public static void setMPAARating(IMediaMetadata md, String rating) {
        md.setString(MetadataKey.MPAA_RATING, rating);
    }

    public static String getProviderDataId(IMediaMetadata md) {
        return md.getString(MetadataKey.MEDIA_PROVIDER_DATA_ID);
    }

    public static void setProviderDataId(IMediaMetadata md, String providerDataId) {
        md.setString(MetadataKey.MEDIA_PROVIDER_DATA_ID, providerDataId);
    }

    public static String getReleaseDate(IMediaMetadata md) {
        return md.getString(MetadataKey.RELEASE_DATE);
    }

    public static void setReleaseDate(IMediaMetadata md, String releaseDate) {
        md.setString(MetadataKey.RELEASE_DATE, releaseDate);
    }

    public static String getRuntime(IMediaMetadata md) {
        return md.getString(MetadataKey.RUNNING_TIME);
    }

    public static void setRuntime(IMediaMetadata md, String runtime) {
        md.setString(MetadataKey.RUNNING_TIME, runtime);
    }

    public static String getMediaTitle(IMediaMetadata md) {
        return md.getString(MetadataKey.MEDIA_TITLE);
    }

    public static void setMediaTitle(IMediaMetadata md, String title) {
        md.setString(MetadataKey.MEDIA_TITLE, title);
    }

    public static String getUserRating(IMediaMetadata md) {
        return md.getString(MetadataKey.USER_RATING);
    }

    public static void setUserRating(IMediaMetadata md, String userRating) {
        md.setString(MetadataKey.USER_RATING, userRating);
    }

    public static String getYear(IMediaMetadata md) {
        return md.getString(MetadataKey.YEAR);
    }

    public static void setYear(IMediaMetadata md, String year) {
        md.setString(MetadataKey.YEAR, year);
    }

    public static List<ICastMember> getCastMembers(IMediaMetadata md, int type) {
        List<ICastMember> castMembers = md.getCastMembers();
        if (castMembers == null || type == ICastMember.ALL) return castMembers;

        // TODO: cache this information
        List<ICastMember> l = new ArrayList<ICastMember>(castMembers.size());
        for (ICastMember cm : castMembers) {
            if (cm.getType() == type) l.add(cm);
        }
        return l;
    }

    public static List<IMediaArt> getMediaArt(IMediaMetadata md) {
        return md.getFanart();
    }
    
    public static List<IMediaArt> getMediaArt(IMediaMetadata md, MediaArtifactType type) {
        List<IMediaArt> mediaArt = md.getFanart();
        if (mediaArt == null || type == null) return mediaArt;

        // TODO: Cache this information
        List<IMediaArt> l = new ArrayList<IMediaArt>(mediaArt.size());
        for (IMediaArt ma : mediaArt) {
            if (ma.getType() == type) l.add(ma);
        }
        return l;
    }

    public static void addGenre(IMediaMetadata md, String genre) {
        if (genre==null || genre.trim().length()==0) return;
        md.getGenres().add(genre);
    }
    
    public static void addCastMember(IMediaMetadata md, ICastMember cm) {
        if (containsCastMember(md,cm)) return;
        md.getCastMembers().add(cm);
    }

    public static boolean containsCastMember(IMediaMetadata md, ICastMember cm) {
        boolean found = false;
        List<ICastMember> castMembers = md.getCastMembers();
        if (castMembers!=null) {
            for (ICastMember m : castMembers) {
                if (m.getType() == cm.getType() && (m.getName()!=null && m.getName().equals(cm.getName()))) {
                    found = true;
                    break;
                }
            }
        }
        
        return found;
    }

    public static void addMediaArt(IMediaMetadata md, IMediaArt ma) {
        md.getFanart().add(ma);
    }

    public static void setDescription(IMediaMetadata md, String plot) {
        md.setString(MetadataKey.DESCRIPTION, plot);
    }

    public static String getDescription(IMediaMetadata md) {
        return md.getString(MetadataKey.DESCRIPTION);
    }

    public static String getProviderDataUrl(IMediaMetadata md) {
        return md.getString(MetadataKey.METADATA_PROVIDER_DATA_URL);
    }

    public static String getProviderId(IMediaMetadata md) {
        return md.getString(MetadataKey.METADATA_PROVIDER_ID);
    }

    public static void setProviderDataUrl(IMediaMetadata md, String url) {
        md.setString(MetadataKey.METADATA_PROVIDER_DATA_URL, url);
    }

    public static void setProviderId(IMediaMetadata md, String id) {
        md.setString(MetadataKey.METADATA_PROVIDER_ID, id);
    }
    
    public static String createMetadataIDString(String id, String url) {
        return new MetadataID(id, url).toIDString();
    }
    
    public static String getCDFromMediaFile(IMediaFile mf) {
        MediaConfiguration medaiCfg = GroupProxy.get(MediaConfiguration.class);
        Pattern cds = Pattern.compile(medaiCfg.getStackingModelRegex());
        String name = mf.getBasename();
        Matcher m = cds.matcher(name);
        String cd=null;
        if (m.find()) {
            cd = m.group(2);
            if ("a".equalsIgnoreCase(cd)) cd="1";
            if ("b".equalsIgnoreCase(cd)) cd="2";
            if ("c".equalsIgnoreCase(cd)) cd="3";
            if ("d".equalsIgnoreCase(cd)) cd="4";
            if ("e".equalsIgnoreCase(cd)) cd="5";
        }
        return cd;
    }
    
    /**
     * Normalized Metadata will go through the Metadata, and fill in the blanks from other fields, etc.  
     * Ie, Media Type, MediaTitle, Display Title, Rating, etc, should all be filled in, if possible. 
     * 
     * @param md
     */
    public static IMediaMetadata normalizeMetadata(IMediaFile mf, IMediaMetadata md) {
        SageMetadataConfiguration cfg = GroupProxy.get(SageMetadataConfiguration.class);
        
        if (StringUtils.isEmpty(MetadataAPI.getDisc(md))) {
            MetadataAPI.setDisc(md, getCDFromMediaFile(mf));
        }
        
        // fix up whitespace
        for (MetadataKey k : MetadataKey.values()) {
            String s = md.getString(k);
            if (s!=null) {
                md.setString(k, s.trim());
            }
        }

        // create a simple sagetv map for rewriting purposes.
        // title masks rely on the properties
        Map<String, String> props = new HashMap<String, String>();
        props.put(SageProperty.MEDIA_TITLE.sageKey, MetadataAPI.getMediaTitle(md));
        props.put(SageProperty.DISPLAY_TITLE.sageKey, rewriteTitle(MetadataAPI.getMediaTitle(md)));
        props.put(SageProperty.DISC.sageKey, MetadataAPI.getDisc(md));
        
        // update it using the mask
        if (!StringUtils.isEmpty(MetadataAPI.getSeason(md))) {
            MetadataAPI.setMediaType(md, MediaType.TV.sageValue());
            
            // assume TV
            if (!StringUtils.isEmpty(MetadataAPI.getEpisode(md))) {
                // cough hack - need to format the season and episode so that it look liks 01, 02, etc.
                Map<String, String> mod = new HashMap<String,String>(props);
                mod.put(SageProperty.SEASON_NUMBER.sageKey, zeroPad(props.get(SageProperty.SEASON_NUMBER.sageKey), 2));
                mod.put(SageProperty.EPISODE_NUMBER.sageKey, zeroPad(props.get(SageProperty.EPISODE_NUMBER.sageKey), 2));
                MetadataAPI.setDisplayTitle(md, MediaMetadataUtils.format(cfg.getTvTitleMask(), mod));
            } else {
                // format for dvd TV disc titles
                Map mod = new HashMap(props);
                mod.put(SageProperty.SEASON_NUMBER.sageKey, zeroPad(props.get(SageProperty.SEASON_NUMBER.sageKey), 2));
                mod.put(SageProperty.DISC.sageKey, zeroPad(props.get(SageProperty.DISC.sageKey), 2));
                MetadataAPI.setDisplayTitle(md,  MediaMetadataUtils.format(cfg.getTvDvdTitleMask(), mod));
            }
        } else {
            // assume normal movie
            if (StringUtils.isEmpty(MetadataAPI.getMediaType(md))) {
                MetadataAPI.setMediaType(md, MediaType.MOVIE.sageValue());
            }
            if (StringUtils.isEmpty(MetadataAPI.getDisc(md))) {
                MetadataAPI.setDisplayTitle(md, MediaMetadataUtils.format(cfg.getTitleMask(), props));
            } else {
                MetadataAPI.setDisplayTitle(md, MediaMetadataUtils.format(cfg.getMultiCDTitleMask(), props));
            }
        }

        MetadataAPI.setUserRating(md, IMDBUtils.parseUserRating(MetadataAPI.getUserRating(md)));
        
        // TODO: Fill in year from release date
        // TODO: Fix Release data in format YYYY-MM-DD
        // TODO: Fix Rating to be "PG" instead of long verbose string
        
        return md;
    }
    
    public static void setDisc(IMediaMetadata md, String value) {
        md.setString(MetadataKey.DVD_DISC, value);
    }

    public static void setDisplayTitle(IMediaMetadata md, String value) {
        md.setString(MetadataKey.DISPLAY_TITLE, value);
    }

    public static String getDisplayTitle(IMediaMetadata md) {
        return md.getString(MetadataKey.DISPLAY_TITLE);
    }

    public static String getMediaType(IMediaMetadata md) {
        return md.getString(MetadataKey.MEDIA_TYPE);
    }

    public static void setMediaType(IMediaMetadata md, String mediaType) {
        md.setString(MetadataKey.MEDIA_TYPE, mediaType);
    }

    public static String getSeason(IMediaMetadata md) {
        return md.getString(MetadataKey.SEASON);
    }
    
    public static String getEpisode(IMediaMetadata md) {
        return md.getString(MetadataKey.EPISODE);
    }
    
    public static String getDisc(IMediaMetadata md) {
        return md.getString(MetadataKey.DVD_DISC);
    }

    private static String zeroPad(String encodeString, int padding) {
        try {
            int v = Integer.parseInt(encodeString);
            String format = "%0" + padding + "d";
            return String.format(format, v);
        } catch (Exception e) {
            return encodeString;
        }
    }
    
    private static String rewriteTitle(String title) {
        if (title == null) title = "";
        if (GroupProxy.get(SageMetadataConfiguration.class).isRewriteTitle()) {
            Pattern p = Pattern.compile(GroupProxy.get(SageMetadataConfiguration.class).getRewriteTitleRegex(), Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(title);
            return m.replaceFirst("$2, $1");
        } else {
            return title;
        }
    }

}
