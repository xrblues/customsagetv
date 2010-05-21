package org.jdna.media.util;

import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;

/**
 * Very Simple visitor that will add each visited item to a Tracker.  This is normally used with a FilteredVisitor so that
 * all filtered items are passed to the tracker.
 * 
 * @author seans
 */
public class ProgressTrackerVisitor implements IMediaResourceVisitor {
    private ProgressTracker<IMediaFile> tracker;

    public ProgressTrackerVisitor(ProgressTracker<IMediaFile> tracker) {
        this.tracker=tracker;
    }
    
    public boolean visit(IMediaResource resource) {
        if (resource instanceof IMediaFile) {
            tracker.addSuccess((IMediaFile) resource);
            tracker.worked(1);
        }
        return true;
    }
}
