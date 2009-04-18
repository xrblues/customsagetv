package org.jdna.sage.media;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataKey;

/**
 * Sage Metadata object that provides read/write support to Sage metadata.
 * A Sage Show object is created once getSageShow() is called.  So use with caution, since
 * calling getSageShow() multiple times will create multiple new instances of show objects
 * in the sage database.
 * 
 * @author seans
 *
 */
public class SageMetadata extends MediaMetadata {

    public SageMetadata() {
        super();
    }

    public SageMetadata(Object sageShow) {
        // TODO: fill metadata from a sage show
    }

    public SageMetadata(IMediaMetadata md) {
        super(md);
    }

    public SageMetadata(MetadataKey[] keys) {
        super(keys);
    }
    
    public Object getSageShow() {
        // TODO: create a new sage show from the given metadata
        return null;
    }
}
