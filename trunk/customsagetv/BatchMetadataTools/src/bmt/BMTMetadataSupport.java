package bmt;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.media.util.BackgroundMetadataUpdater;
import org.jdna.sage.MetadataPluginOptions;
import org.jdna.sage.media.SageMediaFile;
import org.jdna.sage.media.SageMediaFolder;
import org.jdna.sage.media.SageShowPeristence;

import sagex.api.AiringAPI;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.phoenix.fanart.IMetadataProviderInfo;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.IMetadataSupport;

/**
 * Allows BMT to provide metadata support to the Phoenix Metadata apis.
 * 
 * @author seans
 * 
 */
public class BMTMetadataSupport implements IMetadataSupport {
    private static final Logger log = Logger.getLogger(BMTMetadataSupport.class);
    private PersistenceOptions options;
    
    public BMTMetadataSupport() {
        log.info("Using BMTMetadataSupport: " + bmt.api.GetVersion());
        new CentralFanartPersistence();
        options=new PersistenceOptions();
        options.setOverwriteFanart(false);
    }

    public void addActiveProvider(IMetadataProviderInfo pi) {
        bmt.api.AddDefaultMetadataProvider(pi);
    }

    public void decreaseProviderPriority(IMetadataProviderInfo pi) {
        bmt.api.DecreaseMetadataProviderPriority(pi);
    }

    public IMetadataProviderInfo[] getInstalledProviders() {
        return bmt.api.getMetadataProviders(true);
    }

    public void increaseProviderPriority(IMetadataProviderInfo pi) {
        bmt.api.IncreaseMetadataProviderPriority(pi);
    }

    public boolean isMetadataSupportEnabled() {
        return true;
    }

    public void removeActiveProvider(IMetadataProviderInfo pi) {
        bmt.api.RemoveDefaultMetadataProvider(pi);
    }

    public IMetadataProviderInfo[] getActiveProviders() {
        return bmt.api.getMetadataProviders(false);
    }

    public Map<String, String> getMetadataForResult(IMetadataSearchResult result) {
        // first we need to update the central fanart properties based on the ui
        // settings
        try {
            ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setCentralFanartFolder(phoenix.api.GetFanartCentralFolder());
            ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setFanartEnabled(phoenix.api.IsFanartEnabled());
            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(result.getProviderId());
            IMediaMetadata md = prov.getMetaData((IMediaSearchResult) result);
            return SageTVPropertiesPersistence.getSageTVMetadataMap(md);
        } catch (Exception e) {
            log.error("Failed to get metadata for restult: " + result, e);
        }
        return null;
    }

    public String[] getMetadataKeys(KeyType type) {
        List<String> l = new LinkedList<String>();
        
        for (SageProperty p : SageProperty.values()) {
            if (p.sageKey!=null) {
                l.add(p.sageKey);
            }
        }
        
        return l.toArray(new String[l.size()]);
    }

    public boolean updateMetadataForResult(Object media, IMetadataSearchResult result) {
        try {
            // first we need to update the central fanart properties based on
            // the ui settings
            ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setCentralFanartFolder(phoenix.api.GetFanartCentralFolder());
            ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setFanartEnabled(phoenix.api.IsFanartEnabled());

            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(result.getProviderId());
            IMediaMetadata md = prov.getMetaData((IMediaSearchResult) result);
            IMediaMetadataPersistence persistence = null; 

            if (AiringAPI.IsAiringObject(media)) {
                if (AiringAPI.GetMediaFileForAiring(media)==null) {
                    persistence = MetadataPluginOptions.getFanartOnlyPersistence();
                } else {
                    persistence = MetadataPluginOptions.getOnDemandUpdaterPersistence();
                }
            }
            
            if (MediaFileAPI.IsMediaFileObject(media)) {
                persistence = MetadataPluginOptions.getOnDemandUpdaterPersistence();
            }
            
            IMediaFile smf = new SageMediaFile(media);
            // Save the sage properties, the update sage's wiz.bin, then download the fanart.
            persistence.storeMetaData(md, smf, options);
            
            if (MediaFileAPI.IsMediaFileObject(media)) {
                if (ConfigurationManager.getInstance().getMetadataConfiguration().isImportTVAsRecordedShows()) {
                    log.debug("Importing Show as a SageRecording....");
                    SageShowPeristence sp = new SageShowPeristence();
                    sp.storeMetaData(md, smf, options);
                }
            }
            
            if (MediaFileAPI.IsMediaFileObject(media)) {
                log.debug("Running Library Import Scan to pick up the changes.");
                Global.RunLibraryImportScan(false);
            }
        } catch (Exception e) {
            log.error("Failed to update metadata!", e);
        }
        return false;
    }

    public IMetadataSearchResult[] getMetadataSearchResults(String providerId, Object media) {
        log.debug("Searching for metadata; Provider: " + providerId + "; media: " + media);
        
        if (media == null) {
            log.debug("getMetadataSearchResults() was passed a null media item");
            return null;
        }
        
        if (providerId==null) {
            providerId = ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId(); 
        }

        try {
            SageMediaFile smf = new SageMediaFile(media);

            SearchQuery q = SearchQueryFactory.getInstance().createQuery(smf);
            if (smf.getContentType() == IMediaFile.CONTENT_TYPE_TV) {
                q.setType(SearchQuery.Type.TV);
            }

            log.debug("Metadata Search for: " + q + "; media: " + media);
            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(providerId);
            List<IMediaSearchResult> l = prov.search(q);
            if (l == null || l.size() == 0) {
                log.debug("No matches for: " + q);
            } else {
                return l.toArray(new IMetadataSearchResult[l.size()]);
            }
        } catch (Exception e) {
            log.error("Failed to do a metadata lookup", e);
        }
        return null;
    }

    public float getMetadataScanComplete() {
        return BackgroundMetadataUpdater.getCompleted();
    }

    public boolean isMetadataScanRunning() {
        return BackgroundMetadataUpdater.isRunning();
    }

    public void startMetadataScan(String provider, Object[] sageMediaFiles) {
        try {
            if (provider==null) {
                provider = ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId();
            }
            BackgroundMetadataUpdater.startScan(new SageMediaFolder(sageMediaFiles), provider, MetadataPluginOptions.getOnDemandUpdaterPersistence(), MetadataPluginOptions.getPersistenceOptions());
        } catch (Exception e) {
            log.error("Scan Failed!", e);
        }
    }
}
