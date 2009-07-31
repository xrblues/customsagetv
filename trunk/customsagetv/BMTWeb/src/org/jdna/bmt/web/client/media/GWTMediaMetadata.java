package org.jdna.bmt.web.client.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdna.bmt.web.client.util.Property;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MetadataKey;

@SuppressWarnings("serial")
public class GWTMediaMetadata implements IMediaMetadata, Serializable {
    private Map<MetadataKey, Property<String>> metadata = new HashMap<MetadataKey, Property<String>>();
    private List<ICastMember> cast = new ArrayList<ICastMember>();
    private List<IMediaArt> fanart = new ArrayList<IMediaArt>();
    private List<String> genres = new ArrayList<String>();
    
    public GWTMediaMetadata() {
    }

    public Property<String> getProperty(MetadataKey key) {
        Property<String> p = metadata.get(key);
        if (p==null) {
            p = new Property<String>();
            metadata.put(key, p);
        }
        return p;
    }
    
    public List<ICastMember> getCastMembers() {
        return cast;
    }

    public List<IMediaArt> getFanart() {
        return fanart;
    }

    public List<String> getGenres() {
        return genres;
    }

    public float getFloat(MetadataKey key, float defValue) {
        return Float.parseFloat(getString(key));
    }

    public int getInt(MetadataKey key, int defValue) {
        return Integer.parseInt(getString(key));
    }

    public String getString(MetadataKey key) {
        return getProperty(key).get();
    }

    public void setString(MetadataKey key, String value) {
        getProperty(key).set(value);
    }
}
