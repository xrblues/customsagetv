package bmt;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaResource;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.FanartStorage;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.media.util.BackgroundMetadataUpdater;
import org.jdna.sage.MetadataPluginOptions;
import org.jdna.sage.media.SageMediaFolder;

import sagex.phoenix.fanart.IMetadataProviderInfo;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.IMetadataSupport;
import sagex.phoenix.fanart.SageFanartUtil;
import sagex.phoenix.fanart.SimpleMediaFile;
import sagex.phoenix.fanart.FanartUtil.MediaType;

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

            SimpleMediaFile mf = SageFanartUtil.GetSimpleMediaFile(media);
            
            // TODO: Synchronize the Media Types
            // TODO: Fix this so that it will use persistence, maybe set MEDIA_TITLE using mf.getTitle()
            if (mf.getMediaType()==MediaType.TV) {
                md.set(MetadataKey.MEDIA_TYPE, MetadataUtil.TV_MEDIA_TYPE);
                log.debug("Only Updating Fanart for: " + mf.getTitle());
                FanartStorage.downloadFanart(mf.getTitle(), md, options);
                return true;
            } else {
                md.set(MetadataKey.MEDIA_TYPE, MetadataUtil.MOVIE_MEDIA_TYPE);
                log.debug("Updating Fanart and Metadata for: " + mf.getTitle());
                File file = SageFanartUtil.GetFile(media);
                if (file == null) {
                    log.debug("Unable to do a metadata lookup using object: " + media);
                    return false;
                }
                // TODO: Later when we have a better mechanism for updating existing sage items,
                // we need to import the metadata as well.
                FanartStorage.downloadFanart(mf.getTitle(), md, options);
                //IMediaResource res = MediaResourceFactory.getInstance().createResource(file.toURI());
                //res.updateMetadata(md, true);
                return true;
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
            SearchQuery q = null;
            if (media instanceof String) {
                log.debug("Creating a new SearchQuery for string: " + media);
                q = new SearchQuery((String) media);
            } else {
                SimpleMediaFile mf = SageFanartUtil.GetSimpleMediaFile(media);
                if (mf.getMediaType() == MediaType.TV) {
                    q = SearchQueryFactory.getInstance().createTVQuery(mf.getTitle());
                } else if (mf.getMediaType() == MediaType.MUSIC) {
                    log.warn("Music Metadata Unsupported.");
                } else {
                    // check to see if this is a TV video file
                    File f = SageFanartUtil.GetFile(media);
                    if (f==null) {
                        q = SearchQueryFactory.getInstance().createMovieQuery(mf.getTitle());
                    } else {
                        IMediaResource res = MediaResourceFactory.getInstance().createResource(f.toURI());
                        q = SearchQueryFactory.getInstance().createQuery(res);
                        
                        if (q.getType() == SearchQuery.Type.TV) {
                            // this is a parsed tv file, use it
                        } else {
                            // use the file mediafile title
                            q = SearchQueryFactory.getInstance().createMovieQuery(mf.getTitle());
                        }
                    }
                }
            }

            log.debug("Metadata Search for: " + q);
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
            BackgroundMetadataUpdater.startScan(new SageMediaFolder(sageMediaFiles), provider, MetadataPluginOptions.getFanartPersistence(), MetadataPluginOptions.getPersistenceOptions());
        } catch (Exception e) {
            log.error("Scan Failed!", e);
        }
    }
}
