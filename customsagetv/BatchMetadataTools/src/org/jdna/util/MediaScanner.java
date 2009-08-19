package org.jdna.util;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResourceVisitor;

public class MediaScanner implements IRunnableWithProgress<ProgressTracker<IMediaFile>> {
    private static final Logger log = Logger.getLogger(MediaScanner.class);
    
    private IMediaFolder          folder;
    private IMediaResourceVisitor visitor;

    public MediaScanner(IMediaFolder folder, IMediaResourceVisitor visitor) {
        this.folder = folder;
        this.visitor = visitor;
    }

    public void run(ProgressTracker<IMediaFile> monitor) {
        try {
            log.debug("Scanning " + folder.members().size() + " items");
            monitor.beginTask("Scanning Media Items", folder.members().size());
            folder.accept(visitor);
        } finally {
            monitor.done();
        }
    }

    public IMediaFolder getFolder() {
        return folder;
    }

    public void setFolder(IMediaFolder folder) {
        this.folder = folder;
    }

    public IMediaResourceVisitor getVisitor() {
        return visitor;
    }

    public void setVisitor(IMediaResourceVisitor visitor) {
        this.visitor = visitor;
    }
}
