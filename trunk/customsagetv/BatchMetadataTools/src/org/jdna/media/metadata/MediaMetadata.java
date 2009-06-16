package org.jdna.media.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sagex.phoenix.fanart.MediaArtifactType;

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
        this.supportedKeys = MetadataKey.values();
        if (md!=null) {
            // TODO: Maybe do a deep clone on objects
            for (MetadataKey k : MetadataKey.values()) {
                if (md.get(k)!=null) {
                    set(k, md.get(k));
                }
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

    public MetadataID getProviderDataId() {
        return (MetadataID) get(MetadataKey.MEDIA_PROVIDER_DATA_ID);
    }

    public void setProviderDataId(MetadataID providerDataId) {
        set(MetadataKey.MEDIA_PROVIDER_DATA_ID, providerDataId);
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

    public String getMediaTitle() {
        return (String) get(MetadataKey.MEDIA_TITLE);
    }

    public void setMediaTitle(String title) {
        set(MetadataKey.MEDIA_TITLE, title);
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

    public IMediaArt[] getMediaArt(MediaArtifactType type) {
        IMediaArt mediaArt[] = (IMediaArt[]) get(MetadataKey.MEDIA_ART_LIST);
        if (mediaArt == null || type == null) return mediaArt;

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
            IMediaArt[] posters = getMediaArt(MediaArtifactType.POSTER);
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
            IMediaArt[] posters = getMediaArt(MediaArtifactType.BACKGROUND);
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
        if (genre==null || genre.trim().length()==0) return;
        
        // TODO: Not very efficient
        String genres[] = getGenres();
        if (genres == null) {
            genres = new String[] { genre };
        } else {
            List<String> genList = new ArrayList<String>(Arrays.asList(genres));
            genList.add(genre);
            genres = genList.toArray(new String[genList.size()]);
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
            List<ICastMember> list = new ArrayList<ICastMember>(Arrays.asList(cast));
            list.add(cm);
            cast = list.toArray(new ICastMember[list.size()]);
        }
        setCastMembers(cast);
    }

    public boolean containsCastMember(ICastMember cm) {
        boolean found = false;
        ICastMember castMembers[] = (ICastMember[]) get(MetadataKey.CAST_MEMBER_LIST);
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

    public void addMediaArt(IMediaArt ma) {
        // TODO: Not very efficient
        IMediaArt art[] = getMediaArt(null);
        if (art == null) {
            art = new IMediaArt[] { ma };
        } else {
            List<IMediaArt> list = new ArrayList<IMediaArt>(Arrays.asList(art));
            list.add(ma);
            art = list.toArray(new IMediaArt[list.size()]);
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
            // manipulate some fields
            if (key == MetadataKey.SEASON || key == MetadataKey.EPISODE || key == MetadataKey.DVD_DISC) {
                if (value instanceof String) {
                    int n = toInt((String)value);
                    value = String.valueOf(n);
                }
            }
            
            store.put(key, value);
        }
    }

    private int toInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e){
            return 0;
        }
    }

    public MetadataKey[] getSupportedKeys() {
        return supportedKeys;
    }

    public void setSupportedKeys(MetadataKey[] keys) {
        this.supportedKeys = keys;
    }

    public IMediaArt getBanner() {
        IMediaArt banner = (IMediaArt) get(MetadataKey.BANNER_ART);
        // if there isn't a background set, then use the first one
        if (banner == null) {
            IMediaArt[] posters = getMediaArt(MediaArtifactType.BANNER);
            if (posters == null || posters.length==0) return null;
            setBanner(posters[0]);
            banner = posters[0];
        }
        return banner;
    }

    public void setBanner(IMediaArt poster) {
        set(MetadataKey.BANNER_ART, poster);
    }

    public String getProviderDataUrl() {
        return (String) get(MetadataKey.METADATA_PROVIDER_DATA_URL);
    }

    public String getProviderId() {
        return (String) get(MetadataKey.METADATA_PROVIDER_ID);
    }

    public void setProviderDataUrl(String url) {
        set(MetadataKey.METADATA_PROVIDER_DATA_URL, url);
    }

    public void setProviderId(String id) {
        set(MetadataKey.METADATA_PROVIDER_ID, id);
    }
    
    public Map<MetadataKey, Object> getStore() {
        return store;
    }
}
