package org.jdna.metadataupdater;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class Tools {

    public Tools() {
    }

    public static int removeMetadataProperties(File[] dirs) {
        if (dirs==null || dirs.length==0) return 0;
        
        int removed = 0;
        for (File dir: dirs) {
            if (dir.exists() && dir.isDirectory()) {
                Iterator<File> iter = FileUtils.iterateFiles(dir, new String[] {"properties"}, true);
                for (;iter.hasNext();) {
                    File f = iter.next();
                    f.delete();
                    removed++;
                }
            }
        }
        
        return removed;
    }
}
