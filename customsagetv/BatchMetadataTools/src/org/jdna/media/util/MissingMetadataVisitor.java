package org.jdna.media.util;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.impl.URIAdapter;
import org.jdna.media.impl.URIAdapterFactory;
import org.jdna.media.metadata.IMediaMetadata;

/**
 * Resource Visitor that collects ONLY resources that contain missing metadata.
 * 
 * @author seans
 * 
 */
public class MissingMetadataVisitor implements IMediaResourceVisitor {
    private IMediaResourceVisitor missingVisitor;
    private IMediaResourceVisitor skippedVisitor;

    public MissingMetadataVisitor(IMediaResourceVisitor missingVisitor) {
        this(missingVisitor, null);
    }

    /**
     * 
     * @param persistence
     *            Where to load the metadata (required)
     * @param missingVisitor
     *            visitor to receive the missing metadata resources (optional)
     * @param skippedVisitor
     *            visitor to receive the skipped metadata resources (optional)
     */
    public MissingMetadataVisitor(IMediaResourceVisitor missingVisitor, IMediaResourceVisitor skippedVisitor) {
        this.missingVisitor = missingVisitor;
        this.skippedVisitor = skippedVisitor;
    }

    public MissingMetadataVisitor() {
        this(null, null);
    }

    public void visit(IMediaResource resource) {
        if (resource.getType() == IMediaFile.TYPE_FILE) {
            if (isMissingMetadata(resource)) {
                if (missingVisitor != null) missingVisitor.visit(resource);
            } else {
                if (skippedVisitor != null) skippedVisitor.visit(resource);
            }
        }
    }

    public static boolean isMissingMetadata(IMediaResource resource) {
        if (resource.getType() == IMediaFile.TYPE_FILE) {
            try {
                // if the physical files does not exists, then it's missing metadata
                URIAdapter ua  = URIAdapterFactory.getAdapter(resource.getLocalPosterUri());
                IMediaMetadata md = resource.getMetadata();
                
                if (md == null || StringUtils.isEmpty(md.getTitle()) || (md.getPoster() == null && !ua.exists()) ) {
                    return true;
                } // else skip
            } catch (Exception e) {
                return true;
            }
        } // else skip
        return false;
    }

    protected IMediaResourceVisitor getMissingVisitor() {
        return missingVisitor;
    }

    protected IMediaResourceVisitor getSkippedVisitor() {
        return skippedVisitor;
    }
}
