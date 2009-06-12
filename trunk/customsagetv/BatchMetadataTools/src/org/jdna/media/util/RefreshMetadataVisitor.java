package org.jdna.media.util;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.util.ProgressTracker;

public class RefreshMetadataVisitor implements IMediaResourceVisitor {
    private static final Logger   log = Logger.getLogger(RefreshMetadataVisitor.class);
    private PersistenceOptions options;
    private IMediaMetadataPersistence persistence;
    private ProgressTracker<IMediaFile> tracker;

    public RefreshMetadataVisitor(IMediaMetadataPersistence persistence, PersistenceOptions options, ProgressTracker<IMediaFile> tracker) {
        this.options=options;
        this.persistence=persistence;
        this.tracker=tracker;
    }

    public void visit(IMediaResource resource) {
        if (resource.getType() == IMediaResource.Type.File) {
            try {
                log.debug("Refreshing MetaData for: " + resource.getLocationUri());
                // if we have a dataProviderUrl and id, then refresh the
                // metadata, or
                // if we only have title, then call the searchMetaData() using
                // the title
                // from the existing metadata
                IMediaMetadata md = persistence.loadMetaData(resource);

                if (md!=null && md.getProviderDataId() != null) {
                    IMediaMetadataProvider provider = MediaMetadataFactory.getInstance().getProvider(md.getProviderId());
                    if (provider == null) {
                        throw new Exception("Provider Not Registered: " + md.getProviderDataId());
                    }

                    log.debug("Refreshing: " + resource.getLocationUri());
                    IMediaMetadata updated = provider.getMetaDataByUrl(md.getProviderDataUrl());
                    persistence.storeMetaData(updated, resource, options);

                    tracker.addSuccess((IMediaFile) resource);
                } else {
                    log.debug("Skipping: " + resource.getLocationUri() + "; MetaData does not contain providerId or providerDataUrl");
                    tracker.addFailed((IMediaFile) resource, "MetaData does not contain provider Id or provider Data Url");
                }
            } catch (Exception e) {
                log.error("Failed to refresh resource: " + resource.getLocationUri(), e);
                tracker.addFailed((IMediaFile) resource,"Failed!", e);
            }
        }
    }
}
