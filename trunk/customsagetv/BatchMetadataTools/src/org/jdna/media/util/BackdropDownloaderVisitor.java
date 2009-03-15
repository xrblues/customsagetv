package org.jdna.media.util;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;

public class BackdropDownloaderVisitor implements IMediaResourceVisitor {
    public static final Logger log = Logger.getLogger(BackdropDownloaderVisitor.class);
    
    private IMediaMetadataProvider provider;
    private IMediaResourceVisitor handled;
    private IMediaResourceVisitor skipped;
    private boolean overwrite;
    
    public BackdropDownloaderVisitor(String providerId, boolean overwrite, IMediaResourceVisitor handled, IMediaResourceVisitor skipped) {
        provider = MediaMetadataFactory.getInstance().getProvider(providerId);
        this.handled = handled;
        this.skipped = skipped;
        this.overwrite=overwrite;
    }
    
    public void visit(IMediaResource resource) {
        if (resource.getType() == IMediaFile.TYPE_FILE) {
            try {
                IMediaMetadata md = resource.getMetadata();
                if (md==null) {
                   log.debug("Can't download backdrops for media that does not have existing metadata.");
                   skipped.visit(resource);
                   return;
                }
                
                File f = new File(new URI(resource.getLocalBackdropUri()));
                if (f.exists()) {
                    log.debug("Skipping since backdrop already exists: " + f.getAbsolutePath());
                    skipped.visit(resource);
                    return;
                }
                
                if (md.getBackground()!=null) {
                    log.debug("Updating Background using user defined backdrop url: " + md.getBackground().getDownloadUrl());
                    updateBackground(resource, md, md.getBackground());
                    return;
                }
                
                // nothing left to do, but search
                SearchQuery query = SearchQueryFactory.getInstance().createQuery(resource);
                query.set(SearchQuery.Field.TITLE, md.getMediaTitle());
                List<IMediaSearchResult> results = provider.search(query);
                if (MediaMetadataFactory.getInstance().isGoodSearch(results)) {
                    IMediaMetadata backdrop = provider.getMetaData(results.get(0));
                    if (backdrop!=null) { 
                        updateBackground(resource, md, backdrop.getBackground());
                    } else {
                        log.warn("Search Result for "+resource.getLocationUri()+" didn't return anything useful. Skipping.");
                        skipped.visit(resource);
                    }
                    return;
                } else {
                    skipped.visit(resource);
                }
            } catch (Exception e) {
                log.error("Failed to process uri: " + resource.getLocationUri(), e);
                skipped.visit(resource);
            }
        }
    }
    
    private void updateBackground(IMediaResource resource, IMediaMetadata md, IMediaArt background) throws Exception {
        log.debug("Updating Background: " + background.getDownloadUrl() + " for resource: " + resource.getLocationUri());
        md.setBackground(background);
        resource.updateMetadata(md, overwrite);
        handled.visit(resource);
    }
}
