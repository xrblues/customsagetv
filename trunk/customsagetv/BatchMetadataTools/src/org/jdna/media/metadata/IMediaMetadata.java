package org.jdna.media.metadata;

import sagex.phoenix.fanart.FanartUtil.MediaArtifactType;

public interface IMediaMetadata {
    /**
     * Gets a Metadata object value from the metadata store for the given key.
     * The key must be one of the keys listed in the MetadataKey enum class
     * 
     * @param metadataKey
     *            MetadataKey enum
     * @return metadata object value
     */
    public Object get(MetadataKey key);

    /**
     * Puts a Metadata Object value into the metadata store.
     * 
     * @param metadataKey
     *            MetadataKey enum
     * @param value
     *            Object that needs to be put into the store
     */
    public void set(MetadataKey key, Object value);

    /**
     * Returns a String array of the Supported Metadata fields.
     * 
     * @return
     */
    public MetadataKey[] getSupportedKeys();

    /**
     * The following are simply convenience methods for accessing the metdata
     * Implementations should simply map these accessor methods to the
     * appropriate keys
     */

    public String getMediaTitle();

    public void setMediaTitle(String title);

    public String getYear();

    public void setYear(String year);

    public IMediaArt[] getMediaArt(MediaArtifactType type);

    public void setMediaArt(IMediaArt[] art);

    public IMediaArt getPoster();

    public void setPoster(IMediaArt poster);

    public IMediaArt getBackground();

    public void setBackground(IMediaArt poster);

    public IMediaArt getBanner();

    public void setBanner(IMediaArt poster);

    public String getDescription();

    public void setDescription(String plot);

    public String[] getGenres();

    public void setGenres(String[] genres);

    public ICastMember[] getCastMembers(int type);

    public void setCastMembers(ICastMember[] memebers);

    public String getUserRating();

    public void setUserRating(String rating);

    public String getReleaseDate();

    public void setReleaseDate(String date);

    public String getRuntime();

    public void setRuntime(String runtime);

    public void setProviderDataId(MetadataID id);
    public MetadataID getProviderDataId();

    public void setProviderDataUrl(String url);
    public String getProviderDataUrl();

    public void setProviderId(String id);
    public String getProviderId();
}
