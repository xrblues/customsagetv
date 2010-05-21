package org.jdna.metadataupdater;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MetadataAPI;

import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.util.PathUtils;

public class ConsoleProgressTracker extends ProgressTracker<IMediaFile> {
    private IMediaMetadataPersistence persistence = null;
    
    public ConsoleProgressTracker(IMediaMetadataPersistence persistence) {
        super();
        this.persistence=persistence;
    }

    @Override
    public void addFailed(IMediaFile item, String msg, Throwable t) {
        super.addFailed(item, msg, t);
        System.out.println("Skipping: " + PathUtils.getLocation(item) + "; Message: " + msg);
    }

    @Override
    public void addSuccess(IMediaFile item) {
        super.addSuccess(item);
        
        IMediaMetadata md = persistence.loadMetaData(item);
        String title = item.getTitle();
        if (md!=null) {
            title=MetadataAPI.getMediaTitle(md);
        }
        
        System.out.printf("Updated: %s; %s\n", title, PathUtils.getLocation(item));
    }
}
