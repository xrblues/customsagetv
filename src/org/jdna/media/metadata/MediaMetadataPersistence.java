package org.jdna.media.metadata;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.metadataupdater.BMTSageAPIProvider;
import org.jdna.sage.media.SageCustomMetadataPersistence;
import org.jdna.sage.media.SageShowPeristence;

import sagex.SageAPI;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.sage.SageMediaFile;

/**
 * Default persistence implementation. This is a basically a 'smart' peristence,
 * in that it will call other peristence implementations based on conditions,
 * such as importing as tv, etc.
 * 
 * @author seans
 * 
 */
public class MediaMetadataPersistence implements IMediaMetadataPersistence {
    private Logger                        log       = Logger.getLogger(MediaMetadataPersistence.class);

    private SageTVPropertiesPersistence   props     = new SageTVPropertiesPersistence();
    private SageCustomMetadataPersistence sageExtra = new SageCustomMetadataPersistence();
    private CentralFanartPersistence      fanart    = new CentralFanartPersistence();
    private SageShowPeristence            sageShow  = new SageShowPeristence();

    public MediaMetadataPersistence() {
    }

    public String getDescription() {
        return "Smart Peristence";
    }

    public String getId() {
        return "smart";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        IMediaMetadata md = new MediaMetadata();
        log.debug("Begin Loading Metadata for: " + mediaFile);

        if (SageAPI.getProvider() instanceof BMTSageAPIProvider) {
            log.debug("BMT Commandline; Loading Metadata from Properties File for: " + mediaFile);
            IMediaMetadata md2 = props.loadMetaData(mediaFile);
            MetadataAPI.copyNonNull(md2, md);
        } else {
            log.debug("Loading Metadata from Wiz.bin for: " + mediaFile);
            IMediaMetadata md2 = sageShow.loadMetaData(mediaFile);
            MetadataAPI.copyNonNull(md2, md);

            log.debug("Loading Metadata from Custom Metadata Fields in Wiz.bin for: " + mediaFile);
            IMediaMetadata md3 = sageExtra.loadMetaData(mediaFile);
            MetadataAPI.copyNonNull(md3, md);
        }

        log.debug("Done Loading Metadata for: " + mediaFile);
        return md;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        log.info("Begin updating metadata for: " + mediaFile + "; With Options: " + options);
        /**
         * if options.createProperties==true || isRemote -- write properties
         * persistence
         * 
         * if (options.updateWiz || options.importAsTV) -- sage import
         * persistence
         * 
         * -- sage custome metadata persistence
         * 
         * if central fanart -- sage central fanart persistent
         */

        // Normalize the metadata, in case it hasn't been done.
        MetadataAPI.normalizeMetadata((IMediaFile) mediaFile, md, options);

        if (options.isUpdateMetadata()) {
            try {
                // we need to create the props for automatic plugin
                if (options.isCreateProperties() || options.isUsingAutomaticPlugin()) {
                    props.storeMetaData(md, mediaFile, options);
                }
            } catch (Throwable t) {
                log.warn("Failed to store properties for: " + mediaFile, t);
            }

            if (!options.isUsingAutomaticPlugin()) {
                // do not update the wiz.bin directly, if we are running from bmt
                // commandline
                if (!(SageAPI.getProvider() instanceof BMTSageAPIProvider)) {
                    if (options.isUpdateWizBin() || options.isImportAsTV()) {
                        log.debug("Updating Wiz.Bin for: " + mediaFile);
                        try {
                            sageShow.storeMetaData(md, mediaFile, options);
                        } catch (Throwable t) {
                            log.warn("Failed to update wiz.bin for: " + mediaFile, t);
                        }
                    }
                } else {
                    log.debug("BMT Commandline; Skipping Direct Wiz.Bin updates.");
                }
            }
        }

        // don't update the custom metadata if you are using automatic plugin
        if (!options.isUsingAutomaticPlugin()) {
            if (options.isUpdateFanart() || options.isUpdateMetadata()) {
                if (!(SageAPI.getProvider() instanceof BMTSageAPIProvider)) {
                    try {
                        sageExtra.storeMetaData(md, mediaFile, options);
                    } catch (Throwable t) {
                        log.warn("Failed to update Custom Metadata in wiz.bin for: " + mediaFile, t);
                    }
                }
            }
        }

        if (options.isUpdateFanart()) {
            try {
                fanart.storeMetaData(md, mediaFile, options);
            } catch (Throwable t) {
                log.warn("Failedt to update Fanart/Images for: " + mediaFile, t);
            }
        }

        // if we need to touch files, the do so...
        if (!options.isUsingAutomaticPlugin() && options.isTouchingFiles()) {
            log.debug("Updating Timestamp on file for: " + mediaFile);
            try {
                // TODO: Touch Files to the date/time of the recording (or
                // current time +1ms)
                mediaFile.touch(mediaFile.lastModified() + SageMediaFile.MIN_TOUCH_ADJUSTMENT);
            } catch (Throwable t) {
                log.warn("Failed to update file timestamp for mediafile: " + mediaFile, t);
            }
        }
        
        log.debug("Done updating metadata for: " + mediaFile);
    }
}
