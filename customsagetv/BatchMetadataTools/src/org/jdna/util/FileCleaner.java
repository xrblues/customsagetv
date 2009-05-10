package org.jdna.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

public class FileCleaner extends DirectoryWalker {
    private static FileCleaner instance = new FileCleaner(); 
    public FileCleaner() {
        super();
    }

    public static List clean(File start) {
        return instance.cleanDirectory(start);
    }
    
    public List cleanDirectory(File startDirectory) {
        List results = new ArrayList();
        try {
            walk(startDirectory, results);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    protected void handleDirectoryEnd(File directory, int depth, Collection results) throws IOException {
        directory.delete();
    }

    protected void handleFile(File file, int depth, Collection results) {
        // delete file and add to list of deleted
        file.delete();
        results.add(file);
    }
    
    
}
