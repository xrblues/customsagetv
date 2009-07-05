package org.jdna.sage;

import java.io.File;

import sage.MediaFileMetadataParser;

public class PauseMetadataParser implements MediaFileMetadataParser {
    public PauseMetadataParser() {
    }

    public Object extractMetadata(File file, String arg) {
        long pauseMS = 1000 * 60 * 20;
        System.out.printf("Pausing for %,d ms to lock the IsDoingLibraryImportScan\n", pauseMS);
        try {
            Thread.sleep(pauseMS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Pause Complete");
        return null;
    }
}
