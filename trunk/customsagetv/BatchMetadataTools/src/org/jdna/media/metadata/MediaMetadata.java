package org.jdna.media.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class MediaMetadata implements IMediaMetadata, Serializable {
    private static final long        serialVersionUID = 1;

    private Map<MetadataKey, Object> store            = new HashMap<MetadataKey, Object>();

    private MetadataKey[]            supportedKeys;

    public MediaMetadata() {
        supportedKeys = MetadataKey.values();
    }

    public MediaMetadata(MetadataKey[] keys) {
        this.supportedKeys = keys;
    }

    public MediaMetadata(IMediaMetadata md) {
        if (md!=null) {
            // TODO: Maybe do a deep clone on objects
            for (MetadataKey k : MetadataKey.values()) {
                set(k, md.get(k));
            }
        }
    }

    public String getAspectRatio() {
        return (String) get(MetadataKey.ASPECT_RATIO);
    }

    public void setAspectRatio(String aspectRatio) {
        set(MetadataKey.ASPECT_RATIO, aspectRatio);
    }

    public String getCompany() {
        return (String) get(MetadataKey.COMPANY);
    }

    public void setCompany(String company) {
        set(MetadataKey.COMPANY, company);
    }

    public String[] getGenres() {
        return (String[]) get(MetadataKey.GENRE_LIST);
    }

    public void setGenres(String[] genres) {
        set(MetadataKey.GENRE_LIST, genres);
    }

    public String getMPAARating() {
        return (String) get(MetadataKey.MPAA_RATING);
    }

    public void setMPAARating(String rating) {
        set(MetadataKey.MPAA_RATING, rating);
    }

    public String getProviderDataUrl() {
        return (String) get(MetadataKey.PROVIDER_DATA_URL);
    }

    public void setProviderDataUrl(String providerDataUrl) {
        set(MetadataKey.PROVIDER_DATA_URL, providerDataUrl);
    }

    public String getReleaseDate() {
        return (String) get(MetadataKey.RELEASE_DATE);
    }

    public void setReleaseDate(String releaseDate) {
        set(MetadataKey.RELEASE_DATE, releaseDate);
    }

    public String getRuntime() {
        return (String) get(MetadataKey.RUNNING_TIME);
    }

    public void setRuntime(String runtime) {
        set(MetadataKey.RUNNING_TIME, runtime);
    }

    public String getTitle() {
        return (String) get(MetadataKey.TITLE);
    }

    public void setTitle(String title) {
        set(MetadataKey.TITLE, title);
    }

    public String getUserRating() {
        return (String) get(MetadataKey.USER_RATING);
    }

    public void setUserRating(String userRating) {
        set(MetadataKey.USER_RATING, userRating);
    }

    public String getYear() {
        return (String) get(MetadataKey.YEAR);
    }

    public void setYear(String year) {
        set(MetadataKey.YEAR, year);
    }

    public String getProviderId() {
        return (String) get(MetadataKey.PROVIDER_ID);
    }

    public void setProviderId(String providerId) {
        set(MetadataKey.PROVIDER_ID, providerId);
    }

    public ICastMember[] getCastMembers(int type) {
        ICastMember castMembers[] = (ICastMember[]) get(MetadataKey.CAST_MEMBER_LIST);
        if (castMembers == null || type == ICastMember.ALL) return castMembers;

        // TODO: cache this information
        List<ICastMember> l = new ArrayList<ICastMember>(castMembers.length);
        for (ICastMember cm : castMembers) {
            if (cm.getType() == type) l.add(cm);
        }
        return l.toArray(new ICastMember[l.size()]);
    }

    public IMediaArt[] getMediaArt(int type) {
        IMediaArt mediaArt[] = (IMediaArt[]) get(MetadataKey.MEDIA_ART_LIST);
        if (mediaArt == null || type == IMediaArt.ALL) return mediaArt;

        // TODO: Cache this information
        List<IMediaArt> l = new ArrayList<IMediaArt>(mediaArt.length);
        for (IMediaArt ma : mediaArt) {
            if (ma.getType() == type) l.add(ma);
        }
        return l.toArray(new IMediaArt[l.size()]);
    }

    public void setCastMembers(ICastMember[] memebers) {
        set(MetadataKey.CAST_MEMBER_LIST, memebers);
    }

    public void setMediaArt(IMediaArt[] art) {
        set(MetadataKey.MEDIA_ART_LIST, art);
    }

    public IMediaArt getPoster() {
        IMediaArt poster = (IMediaArt) get(MetadataKey.POSTER_ART);
        // if there isn't a specific poster set, the use the first one
        if (poster == null) {
            IMediaArt[] posters = getMediaArt(IMediaArt.POSTER);
            if (posters == null || posters.length==0) return null;
            setPoster(posters[0]);
            poster=posters[0];
        }
        return poster;
    }

    public void setPoster(IMediaArt poster) {
        set(MetadataKey.POSTER_ART, poster);
    }

    public IMediaArt getBackground() {
        IMediaArt background = (IMediaArt) get(MetadataKey.BACKGROUND_ART);
        // if there isn't a background set, then use the first one
        if (background == null) {
            IMediaArt[] posters = getMediaArt(IMediaArt.BACKGROUND);
            if (posters == null || posters.length==0) return null;
            setBackground(posters[0]);
            background = posters[0];
        }
        return background;
    }

    public void setBackground(IMediaArt background) {
        set(MetadataKey.BACKGROUND_ART, background);
    }

    public void addGenre(String genre) {
        if (StringUtils.isEmpty(genre)) return;
        
        // TODO: Not very efficient
        String genres[] = getGenres();
        if (genres == null) {
            genres = new String[] { genre };
        } else {
            genres = Arrays.copyOf(genres, genres.length + 1);
            genres[genres.length - 1] = genre;
        }
        setGenres(genres);
    }

    public void addCastMember(ICastMember cm) {
        if (containsCastMember(cm)) return;
        
        // TODO: Not very efficient
        ICastMember cast[] = getCastMembers(ICastMember.ALL);
        if (cast == null) {
            cast = new ICastMember[] { cm };
        } else {
            cast = Arrays.copyOf(cast, cast.length + 1);
            cast[cast.length - 1] = cm;
        }
        setCastMembers(cast);
    }

    public boolean containsCastMember(ICastMember cm) {
        boolean found = false;
        ICastMember castMembers[] = (ICastMember[]) get(MetadataKey.CAST_MEMBER_LIST);
        if (castMembers!=null) {
            for (ICastMember m : castMembers) {
                if (m.getType() == cm.getType() && m.getName()==cm.getName()) {
                    found = true;
                    break;
                }
            }
        }
        
        return found;
    }

    public void addMediaArt(IMediaArt ma) {
        // TODO: Not very efficient
        IMediaArt art[] = getMediaArt(IMediaArt.ALL);
        if (art == null) {
            art = new IMediaArt[] { ma };
        } else {
            art = Arrays.copyOf(art, art.length + 1);
            art[art.length - 1] = ma;
        }
        setMediaArt(art);
    }

    public void setDescription(String plot) {
        set(MetadataKey.DESCRIPTION, plot);
    }

    public String getDescription() {
        return (String) get(MetadataKey.DESCRIPTION);
    }

    public Object get(MetadataKey key) {
        return store.get(key);
    }

    public void set(MetadataKey key, Object value) {
        if (value!=null) {
            store.put(key, value);
        }
    }

    public MetadataKey[] getSupportedKeys() {
        return supportedKeys;
    }

    public void setSupportedKeys(MetadataKey[] keys) {
        this.supportedKeys = keys;
    }
}
