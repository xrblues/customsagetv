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
public class ProgressTrackerVisitor extends ProgressTracker<IMediaFile> implements IMediaResourceVisitor {
    public void visit(IMediaResource resource) {
        if (resource.getType() == IMediaResource.Type.File) {
            addSuccess((IMediaFile) resource);
            worked(1);
        }
    }
}
