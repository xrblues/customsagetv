package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jdna.bmt.web.client.util.Property;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MetadataKey;

@SuppressWarnings("serial")
public class MediaItem implements IMediaMetadata, Serializable {
    public static enum NonEditField {FILE_URI, MEDIA_ID, POSTER_COUNT, BACKGROUND_COUNT, BANNER_COUNT, POSTER_URI, BACKGROUND_URI, BANNER_URI};

    private Map<MetadataKey, Property<String>> metadata = new HashMap<MetadataKey, Property<String>>();
    private Map<NonEditField, Property<String>> nonedit = new HashMap<NonEditField, Property<String>>();
    
    private String sageMediaItemString = null;
    
    public MediaItem() {
    }

    public MediaItem(IMediaMetadata metadata) {
        System.out.println("Begin Copy Metadata");
        for (MetadataKey k : MetadataKey.values()) {
            if (k.equals(MetadataKey.MEDIA_ART_LIST)) {
            } else if (k.equals(MetadataKey.CAST_MEMBER_LIST)) {
            } else if (k.equals(MetadataKey.POSTER_ART)) {
            } else if (k.equals(MetadataKey.BACKGROUND_ART)) {
            } else if (k.equals(MetadataKey.BANNER_ART)) {
            } else if (k.equals(MetadataKey.CAST_MEMBER_LIST)) {
            } else {
                Object o = metadata.get(k);
                if (o!=null) {
                    if (!(o instanceof String)) {
                        System.out.println("**** Should be a string: " + k + "; " + o.getClass().getName() + "; " + o);
                    }
                    
                    // GWT has issues with non string data, even Long, Integer etc, can cause issues
                    set(k, String.valueOf(o));
                }
            }
        }
        System.out.println("End Copy Metadata");
    }

    public Property<String> getNonEditField(NonEditField field) {
        Property<String> p = nonedit.get(field);
        if (p==null) {
            p=new Property<String>();
            nonedit.put(field, p);
        }
        return p;
    }

    public Property<String> getProperty(MetadataKey key) {
        Property<String> p = metadata.get(key);
        if (p==null) {
            p = new Property<String>();
            metadata.put(key, p);
        }
        return p;
    }
    
    public Object get(MetadataKey key) {
        return getProperty(key).get();
    }

    public MetadataKey[] getSupportedKeys() {
        return MetadataKey.values();
    }

    public void set(MetadataKey key, Object value) {
        getProperty(key).set(String.valueOf(value));
    }

    public String getSageMediaItemString() {
        return sageMediaItemString;
    }

    public void setSageMediaItemString(String sageMediaItemString) {
        this.sageMediaItemString = sageMediaItemString;
    }
    
    public void removeProperty(MetadataKey key) {
        metadata.remove(key);
    }
}
