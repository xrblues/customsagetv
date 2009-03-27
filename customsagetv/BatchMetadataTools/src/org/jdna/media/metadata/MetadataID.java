package org.jdna.media.metadata;

// TODO: Make this contain a map of ids
public class MetadataID {
    private String key, id;
    public MetadataID(String key, String id) {
        this.key=key;
        this.id=id;
    }
    public MetadataID(String id) {
        String parts[] = MetadataUtil.getMetadataIdParts(id);
        if (parts==null || parts.length !=2) {
            // TODO: Error
        } else {
            key = parts[0];
            this.id=parts[1];
        }
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String toIDString() {
        return key + ":" + id;
    }
    
    public String toString() {
        return toIDString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj!=null && (obj instanceof MetadataID) && toIDString().equals(((MetadataID)obj).toIDString()); 
    }
}