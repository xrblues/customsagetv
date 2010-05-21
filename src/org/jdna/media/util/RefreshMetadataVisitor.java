package org.jdna.media.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.process.MetadataItem;

import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;
import sagex.phoenix.vfs.util.PathUtils;

public class RefreshMetadataVisitor implements IMediaResourceVisitor {
    private static final Logger           log = Logger.getLogger(RefreshMetadataVisitor.class);
    private PersistenceOptions            options;
    private IMediaMetadataPersistence     persistence;
    private ProgressTracker<MetadataItem> tracker;

    public RefreshMetadataVisitor(IMediaMetadataPersistence persistence, PersistenceOptions options, ProgressTracker<MetadataItem> tracker) {
        this.options = options;
        this.persistence = persistence;
        this.tracker = tracker;
    }

    public boolean visit(IMediaResource resource) {
        if (resource instanceof IMediaFile) {
            try {
                File f = FanartUtil.resolvePropertiesFile(PathUtils.getFirstFile((IMediaFile) resource));
                if (f == null || !f.exists() || !FileUtils.isFileNewer(f, resource.lastModified())) {
                    tracker.addFailed(new MetadataItem((IMediaFile) resource, null, options, null, persistence, null), "Skipping resource since it has not been modified");
                } else {
                    log.debug("Refreshing MetaData for: " + PathUtils.getLocation(resource));
                    // if we have a dataProviderUrl and id, then refresh the
                    // metadata, or
                    // if we only have title, then call the searchMetaData()
                    // using
                    // the title
                    // from the existing metadata
                    IMediaMetadata md = persistence.loadMetaData(resource);
                    if (md != null && MetadataAPI.getProviderDataId(md) != null) {
                        MediaType mediaType = MediaType.toMediaType(MetadataAPI.getMediaType(md));
                        IMediaMetadataProvider provider = MediaMetadataFactory.getInstance().getProvider(MetadataAPI.getProviderId(md), mediaType);
                        if (provider == null) {
                            throw new Exception("Provider Not Registered: " + MetadataAPI.getProviderDataId(md));
                        }

                        log.debug("Refreshing: " + PathUtils.getLocation(resource));
                        throw new RuntimeException("NOT IMPLEMENTED");
                        //IMediaMetadata updated = provider.getMetaDataByUrl(MetadataAPI.getProviderDataUrl(md));
                        //persistence.storeMetaData(updated, resource, options);

                        //tracker.addSuccess(new MetadataItem((IMediaFile) resource, null, options, provider, persistence, md));
                    } else {
                        log.debug("Skipping: " + PathUtils.getLocation(resource) + "; MetaData does not contain providerId or providerDataUrl");
                        tracker.addFailed(new MetadataItem((IMediaFile) resource, null, options, null, persistence, md), "MetaData does not contain provider Id or provider Data Url");
                    }
                }
            } catch (Exception e) {
                log.error("Failed to refresh resource: " + PathUtils.getLocation(resource), e);
                tracker.addFailed(new MetadataItem((IMediaFile) resource, null, options, null, persistence, null), "Failed to refresh resouce!", e);
            }
        }
        return true;
    }
}
