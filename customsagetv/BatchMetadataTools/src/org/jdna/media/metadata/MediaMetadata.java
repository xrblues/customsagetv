package org.jdna.media.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import sagex.phoenix.fanart.MediaArtifactType;

public class MediaMetadata implements IMediaMetadata, Serializable {
    private static final long        serialVersionUID = 1;

    private Map<MetadataKey, String> store            = new HashMap<MetadataKey, String>();
    private List<ICastMember> castMembers = new ArrayList<ICastMember>();
    private List<IMediaArt> fanart = new ArrayList<IMediaArt>();
    private List<String> genres = new ArrayList<String>();

    public MediaMetadata() {
    }

    public MediaMetadata(IMediaMetadata md) {
        MetadataAPI.copy(md, this);
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

    public List<String> getGenres() {
        return genres;
    }

    public String getMPAARating() {
        return (String) get(MetadataKey.MPAA_RATING);
    }

    public void setMPAARating(String rating) {
        set(MetadataKey.MPAA_RATING, rating);
    }

    public String getProviderDataId() {
        return get(MetadataKey.MEDIA_PROVIDER_DATA_ID);
    }

    public void setProviderDataId(String providerDataId) {
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

    public List<ICastMember> getCastMembers(int type) {
        return MetadataAPI.getCastMembers(this, type);
    }

    public List<IMediaArt> getMediaArt(MediaArtifactType type) {
        return MetadataAPI.getMediaArt(this, type);
    }

    public void addGenre(String genre) {
        MetadataAPI.addGenre(this, genre);
    }
    
    public void addCastMember(ICastMember cm) {
        MetadataAPI.addCastMember(this, cm);
    }

    public void addMediaArt(IMediaArt ma) {
        MetadataAPI.addMediaArt(this, ma);
    }

    public void setDescription(String plot) {
        set(MetadataKey.DESCRIPTION, plot);
    }

    public String getDescription() {
        return (String) get(MetadataKey.DESCRIPTION);
    }

    public String get(MetadataKey key) {
        return store.get(key);
    }

    public void set(MetadataKey key, String value) {
        if (value!=null) {
            // manipulate some fields
            if (key == MetadataKey.SEASON || key == MetadataKey.EPISODE || key == MetadataKey.DVD_DISC) {
                int n = toInt((String)value);
                value = String.valueOf(n);
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

    public List<ICastMember> getCastMembers() {
        return castMembers;
    }

    public List<IMediaArt> getFanart() {
        return fanart;
    }
    
    public float getFloat(MetadataKey key, float defValue) {
        String value = getString(key);
        if (value!=null) {
            return NumberUtils.toFloat(value, defValue);
        } else {
            return defValue;
        }
    }

    public int getInt(MetadataKey key, int defValue) {
        String value = getString(key);
        if (value!=null) {
            return NumberUtils.toInt(value, defValue);
        } else {
            return defValue;
        }
    }

    public String getString(MetadataKey key) {
        return get(key);
    }

    public void setString(MetadataKey key, String value) {
        set(key, value);
    }
}
