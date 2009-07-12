package org.jdna.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.DirectoryWalker;

public class FileToucher extends DirectoryWalker {
    private long time=0;
    
    public FileToucher() {
        super();
    }

    public static void touch(File startDirectory, long time) {
        new FileToucher().touchFiles(startDirectory, time);
    }
    
    public void touchFiles(File startDirectory, long time) {
        try {
            this.time=time;
            walk(startDirectory, Collections.EMPTY_LIST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleDirectoryEnd(File directory, int depth, Collection results) throws IOException {
        directory.setLastModified(time);
    }

    @Override
    protected void handleFile(File file, int depth, Collection results) {
        file.setLastModified(time);
    }
}
