package org.jdna.media.util;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;

public class RefreshMetadataVisitor implements IMediaResourceVisitor {
    private static final Logger   log = Logger.getLogger(RefreshMetadataVisitor.class);
    private IMediaResourceVisitor onUpdateVisitor;
    private IMediaResourceVisitor onSkipMediaResourceVisitor;
    private boolean overwrite;

    public RefreshMetadataVisitor(boolean overwrite, IMediaResourceVisitor onUpdateVisitor, IMediaResourceVisitor onSkipMediaResourceVisitor) {
        this.overwrite = overwrite;
        this.onUpdateVisitor = onUpdateVisitor;
        this.onSkipMediaResourceVisitor = onSkipMediaResourceVisitor;
    }

    public void visit(IMediaResource resource) {
        if (resource.getType() == IMediaFile.TYPE_FILE) {
            try {
                log.debug("Refreshing MetaData for: " + resource.getLocationUri());
                // if we have a dataProviderUrl and id, then refresh the
                // metadata, or
                // if we only have title, then call the searchMetaData() using
                // the title
                // from the existing metadata
                IMediaMetadata md = resource.getMetadata();

                if (md.getProviderDataId() != null) {
                    IMediaMetadataProvider provider = MediaMetadataFactory.getInstance().getProvider(md.getProviderId());
                    if (provider == null) {
                        throw new Exception("Provider Not Registered: " + md.getProviderDataId());
                    }

                    log.debug("Refreshing: " + resource.getLocationUri());
                    IMediaMetadata updated = provider.getMetaDataByUrl(md.getProviderDataUrl());
                    resource.updateMetadata(updated, overwrite);

                    onUpdateVisitor.visit(resource);
                } else {
                    log.debug("Skipping: " + resource.getLocationUri() + "; MetaData does not contain providerId or providerDataUrl");
                    onSkipMediaResourceVisitor.visit(resource);
                }
            } catch (Exception e) {
                log.error("Failed to refresh resource: " + resource.getLocationUri(), e);
                onSkipMediaResourceVisitor.visit(resource);
            }
        } else {
            onSkipMediaResourceVisitor.visit(resource);
        }
    }

}
