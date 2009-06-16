package org.jdna.media.metadata;


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
}
