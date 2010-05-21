package org.jdna.media.metadata;

import java.util.List;


public interface IMediaMetadata {
    /**
     * Puts a Metadata Object value into the metadata store.
     * 
     * @param metadataKey
     *            MetadataKey enum
     * @param value
     *            Object that needs to be put into the store
     */
    public void setString(MetadataKey key, String value);

    /**
     * Gets a Metadata object value from the metadata store for the given key.
     * The key must be one of the keys listed in the MetadataKey enum class
     * 
     * @param metadataKey
     *            MetadataKey enum
     * @return metadata object value
     */
    public String getString(MetadataKey key);
    public int getInt(MetadataKey key, int defValue);
    public float getFloat(MetadataKey key, float defValue);
    
    public void remove(MetadataKey key);
    
    public List<ICastMember> getCastMembers();
    public List<String> getGenres();
    public List<IMediaArt> getFanart();
}
