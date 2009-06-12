package org.jdna.media.metadata;

import java.io.Serializable;

// TODO: Make this contain a map of ids
public class MetadataID implements Serializable {
    public static String[] getMetadataIdParts(String id) {
        if (id==null) return null;
        String parts[] = id.split(":");
        if (parts==null || parts.length!=2) {
            return new String[] {id};
        }
        return parts;
    }
    
    private String key, id;
    public MetadataID(String key, String id) {
        this.key=key;
        this.id=id;
    }
    public MetadataID(String id) {
        String parts[] = getMetadataIdParts(id);
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
    
    @Override
    public String toString() {
        return toIDString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj!=null && (obj instanceof MetadataID) && toIDString().equals(((MetadataID)obj).toIDString()); 
    }
}
