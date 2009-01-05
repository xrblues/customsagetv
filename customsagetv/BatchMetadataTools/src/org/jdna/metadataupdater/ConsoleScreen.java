package org.jdna.metadataupdater;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaMetadata;

/**
 * IMetaDataUpdaterScreen implemenation that uses a standard text based console.
 * 
 * 
 * 
 * @author seans
 * 
 */
public class ConsoleScreen {
    private static final Logger log = Logger.getLogger(ConsoleScreen.class);

    private class Failed {
        public Failed(IMediaResource r, Exception e) {
            this.res = r;
            this.error = e;
        }

        public IMediaResource res;
        public Exception      error;
    }

    private int          processed = 0;
    private int          skipped   = 0;
    private int          updated   = 0;
    private List<Failed> failed    = new ArrayList<Failed>();
    private int          manual    = 0;

    public void renderStats() {
        System.out.println("\n\nMetaData Stats...");
        System.out.printf("Processed: %d; Updated: %d (%d manual), Skipped: %d; Errors: %d.\n\n", processed, updated, manual, skipped, failed.size());
        if (failed.size() > 0) {
            System.out.println("Listing Failed Videos....");
            for (Failed f : failed) {
                System.out.printf("Video: %s\n", f.res.getLocationUri());
                if (f.error != null) {
                    System.out.printf("Error: %s\n", f.error.getMessage());
                }
                System.out.println("");
            }
        }

        if (updated > 0) {
            System.out.println("\nBe sure to go into SageTV and select\n'Media Center -> Videos -> Options -> Advanced Options -> Refresh Imported Media'\nTo Ensure that your metadata and images get refreshed.\n");
        }

    }

    public void nofifySkippedFile(IMediaResource r) {
        skipped++;
        System.out.printf("%10s: %s\n", "Skip", r.getTitle());
    }

    public void notifyFailedFile(IMediaResource r, Exception e) {
        failed.add(new Failed(r, e));
        System.out.printf("%10s: %s\n", "Failed", r.getTitle());
    }

    public void notifyProcessingFile(IMediaResource r) {
        processed++;
    }

    public void notifyManualUpdate(IMediaResource r, IMediaMetadata md) {
        manual++;
    }

    public void notifyUpdatedFile(IMediaResource r, IMediaMetadata md) {
        updated++;
        if (md == null) {
            System.out.println("Updated: " + r.getTitle());
        } else {
            System.out.printf("%10s: %-30s (%s)\n", "Updated", md.getTitle(), r.getName());
        }
    }
}
