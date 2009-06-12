package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jdna.media.metadata.MetadataKey;

public class MediaItem implements Serializable {
    private Map<MetadataKey, Object> metadata = new HashMap<MetadataKey, Object>();
    
    public MediaItem() {
    }
    
}
