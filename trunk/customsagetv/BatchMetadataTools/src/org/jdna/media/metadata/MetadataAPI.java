package org.jdna.media.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sagex.phoenix.fanart.MediaArtifactType;

/**
 * Static class for working with metadata fields
 * 
 * @author seans
 *
 */
public class MetadataAPI {
    public static IMediaMetadata copy(IMediaMetadata src, IMediaMetadata dest) {
        for (MetadataKey k : MetadataKey.values()) {
            dest.set(k, src.get(k));
        }
        return dest;
    }

    public static IMediaMetadata copyNonNull(IMediaMetadata src, IMediaMetadata dest) {
        for (MetadataKey k : MetadataKey.values()) {
            if (src.get(k)!=null) {
                dest.set(k, src.get(k));
            }
        }
        return dest;
    }

    public static String getAspectRatio(IMediaMetadata md) {
        return (String) md.get(MetadataKey.ASPECT_RATIO);
    }

    public static void setAspectRatio(IMediaMetadata md, String aspectRatio) {
        md.set(MetadataKey.ASPECT_RATIO, aspectRatio);
    }

    public static String getCompany(IMediaMetadata md) {
        return (String) md.get(MetadataKey.COMPANY);
    }

    public static void setCompany(IMediaMetadata md,String company) {
        md.set(MetadataKey.COMPANY, company);
    }

    public static String[] getGenres(IMediaMetadata md) {
        return (String[]) md.get(MetadataKey.GENRE_LIST);
    }

    public static void setGenres(IMediaMetadata md, String[] genres) {
        md.set(MetadataKey.GENRE_LIST, genres);
    }

    public static String getMPAARating(IMediaMetadata md) {
        return (String) md.get(MetadataKey.MPAA_RATING);
    }

    public static void setMPAARating(IMediaMetadata md, String rating) {
        md.set(MetadataKey.MPAA_RATING, rating);
    }

    public static MetadataID getProviderDataId(IMediaMetadata md) {
        return (MetadataID) md.get(MetadataKey.MEDIA_PROVIDER_DATA_ID);
    }

    public static void setProviderDataId(IMediaMetadata md, MetadataID providerDataId) {
        md.set(MetadataKey.MEDIA_PROVIDER_DATA_ID, providerDataId);
    }

    public static String getReleaseDate(IMediaMetadata md) {
        return (String) md.get(MetadataKey.RELEASE_DATE);
    }

    public static void setReleaseDate(IMediaMetadata md, String releaseDate) {
        md.set(MetadataKey.RELEASE_DATE, releaseDate);
    }

    public static String getRuntime(IMediaMetadata md) {
        return (String) md.get(MetadataKey.RUNNING_TIME);
    }

    public static void setRuntime(IMediaMetadata md, String runtime) {
        md.set(MetadataKey.RUNNING_TIME, runtime);
    }

    public static String getMediaTitle(IMediaMetadata md) {
        return (String) md.get(MetadataKey.MEDIA_TITLE);
    }

    public static void setMediaTitle(IMediaMetadata md, String title) {
        md.set(MetadataKey.MEDIA_TITLE, title);
    }

    public static String getUserRating(IMediaMetadata md) {
        return (String) md.get(MetadataKey.USER_RATING);
    }

    public static void setUserRating(IMediaMetadata md, String userRating) {
        md.set(MetadataKey.USER_RATING, userRating);
    }

    public static String getYear(IMediaMetadata md) {
        return (String) md.get(MetadataKey.YEAR);
    }

    public static void setYear(IMediaMetadata md, String year) {
        md.set(MetadataKey.YEAR, year);
    }

    public static ICastMember[] getCastMembers(IMediaMetadata md, int type) {
        ICastMember castMembers[] = (ICastMember[]) md.get(MetadataKey.CAST_MEMBER_LIST);
        if (castMembers == null || type == ICastMember.ALL) return castMembers;

        // TODO: cache this information
        List<ICastMember> l = new ArrayList<ICastMember>(castMembers.length);
        for (ICastMember cm : castMembers) {
            if (cm.getType() == type) l.add(cm);
        }
        return l.toArray(new ICastMember[l.size()]);
    }

    public static IMediaArt[] getMediaArt(IMediaMetadata md, MediaArtifactType type) {
        IMediaArt mediaArt[] = (IMediaArt[]) md.get(MetadataKey.MEDIA_ART_LIST);
        if (mediaArt == null || type == null) return mediaArt;

        // TODO: Cache this information
        List<IMediaArt> l = new ArrayList<IMediaArt>(mediaArt.length);
        for (IMediaArt ma : mediaArt) {
            if (ma.getType() == type) l.add(ma);
        }
        return l.toArray(new IMediaArt[l.size()]);
    }

    public static void setCastMembers(IMediaMetadata md, ICastMember[] memebers) {
        md.set(MetadataKey.CAST_MEMBER_LIST, memebers);
    }

    public static void setMediaArt(IMediaMetadata md, IMediaArt[] art) {
        md.set(MetadataKey.MEDIA_ART_LIST, art);
    }

    public static IMediaArt getPoster(IMediaMetadata md) {
        IMediaArt poster = (IMediaArt) md.get(MetadataKey.POSTER_ART);
        // if there isn't a specific poster set, the use the first one
        if (poster == null) {
            IMediaArt[] posters = getMediaArt(md,MediaArtifactType.POSTER);
            if (posters == null || posters.length==0) return null;
            setPoster(md,posters[0]);
            poster=posters[0];
        }
        return poster;
    }

    public static void setPoster(IMediaMetadata md, IMediaArt poster) {
        md.set(MetadataKey.POSTER_ART, poster);
    }

    public static IMediaArt getBackground(IMediaMetadata md) {
        IMediaArt background = (IMediaArt) md.get(MetadataKey.BACKGROUND_ART);
        // if there isn't a background set, then use the first one
        if (background == null) {
            IMediaArt[] posters = getMediaArt(md,MediaArtifactType.BACKGROUND);
            if (posters == null || posters.length==0) return null;
            MetadataAPI.setBackground(md, posters[0]);
            background = posters[0];
        }
        return background;
    }

    public static void setBackground(IMediaMetadata md, IMediaArt background) {
        md.set(MetadataKey.BACKGROUND_ART, background);
    }

    public static void addGenre(IMediaMetadata md, String genre) {
        if (genre==null || genre.trim().length()==0) return;
        
        // TODO: Not very efficient
        String genres[] = getGenres(md);
        if (genres == null) {
            genres = new String[] { genre };
        } else {
            List<String> genList = new ArrayList<String>(Arrays.asList(genres));
            genList.add(genre);
            genres = genList.toArray(new String[genList.size()]);
        }
        setGenres(md,genres);
    }
    
    public static void addCastMember(IMediaMetadata md, ICastMember cm) {
        if (containsCastMember(md,cm)) return;
        
        // TODO: Not very efficient
        ICastMember cast[] = getCastMembers(md,ICastMember.ALL);
        if (cast == null) {
            cast = new ICastMember[] { cm };
        } else {
            List<ICastMember> list = new ArrayList<ICastMember>(Arrays.asList(cast));
            list.add(cm);
            cast = list.toArray(new ICastMember[list.size()]);
        }
        setCastMembers(md,cast);
    }

    public static boolean containsCastMember(IMediaMetadata md, ICastMember cm) {
        boolean found = false;
        ICastMember castMembers[] = (ICastMember[]) md.get(MetadataKey.CAST_MEMBER_LIST);
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
        // TODO: Not very efficient
        IMediaArt art[] = getMediaArt(md, null);
        if (art == null) {
            art = new IMediaArt[] { ma };
        } else {
            List<IMediaArt> list = new ArrayList<IMediaArt>(Arrays.asList(art));
            list.add(ma);
            art = list.toArray(new IMediaArt[list.size()]);
        }
        setMediaArt(md,art);
    }

    public static void setDescription(IMediaMetadata md, String plot) {
        md.set(MetadataKey.DESCRIPTION, plot);
    }

    public static String getDescription(IMediaMetadata md) {
        return (String) md.get(MetadataKey.DESCRIPTION);
    }

    private static int toInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e){
            return 0;
        }
    }

    public static IMediaArt getBanner(IMediaMetadata md) {
        IMediaArt banner = (IMediaArt) md.get(MetadataKey.BANNER_ART);
        // if there isn't a background set, then use the first one
        if (banner == null) {
            IMediaArt[] posters = getMediaArt(md, MediaArtifactType.BANNER);
            if (posters == null || posters.length==0) return null;
            setBanner(md,posters[0]);
            banner = posters[0];
        }
        return banner;
    }

    public static void setBanner(IMediaMetadata md, IMediaArt poster) {
        md.set(MetadataKey.BANNER_ART, poster);
    }

    public static String getProviderDataUrl(IMediaMetadata md) {
        return (String) md.get(MetadataKey.METADATA_PROVIDER_DATA_URL);
    }

    public static String getProviderId(IMediaMetadata md) {
        return (String) md.get(MetadataKey.METADATA_PROVIDER_ID);
    }

    public static void setProviderDataUrl(IMediaMetadata md, String url) {
        md.set(MetadataKey.METADATA_PROVIDER_DATA_URL, url);
    }

    public static void setProviderId(IMediaMetadata md, String id) {
        md.set(MetadataKey.METADATA_PROVIDER_ID, id);
    }
}
