package org.jdna.sage;

import java.io.File;

import org.apache.log4j.Logger;

import sage.MediaFileMetadataParser;

/**
 * Plugin Rules - run only for mediafiles that do not have properties - ignore
 * metadata on tv types, but try to download fanart
 * 
 * @author seans
 * 
 */
public class MetadataUpdaterPlugin implements MediaFileMetadataParser {
    private final Logger log          = Logger.getLogger(MetadataUpdaterPlugin.class);
    private static boolean      init         = false;

    public MetadataUpdaterPlugin() {
    }

    /**
     * For a given file, find the metadata and return back a Map of SageTV
     * properties.
     * 
     */
    public Object extractMetadata(File file, @SuppressWarnings("unused") String arg) {
        if (!init) {
            init = true;
        }
        throw new UnsupportedOperationException("extractMetadata() not implemeneted");
    }
}
