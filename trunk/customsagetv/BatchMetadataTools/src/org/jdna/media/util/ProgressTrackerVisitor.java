package org.jdna.media.util;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.util.ProgressTracker;

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
    
    public void visit(IMediaResource resource) {
        if (resource.getType() == IMediaResource.Type.File) {
            tracker.addSuccess((IMediaFile) resource);
            tracker.worked(1);
        }
    }
}
