package org.jdna.metadataupdater;

import org.jdna.media.IMediaFile;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.util.ProgressTracker;

public class ConsoleProgressTracker extends ProgressTracker<IMediaFile> {
    private IMediaMetadataPersistence persistence = null;
    
    public ConsoleProgressTracker(IMediaMetadataPersistence persistence) {
        super();
        this.persistence=persistence;
    }

    @Override
    public void addFailed(IMediaFile item, String msg, Throwable t) {
        super.addFailed(item, msg, t);
        System.out.println("Skipping: " + item.getLocationUri() + "; Message: " + msg);
    }

    @Override
    public void addSuccess(IMediaFile item) {
        super.addSuccess(item);
        
        IMediaMetadata md = persistence.loadMetaData(item);
        String title = item.getTitle();
        if (md!=null) {
            title=md.getMediaTitle();
        }
        
        System.out.printf("Updated: %s; %s\n", title, item.getLocationUri());
        
        // touch the resource, so that Sage will reload.
        item.touch();

    }
}
