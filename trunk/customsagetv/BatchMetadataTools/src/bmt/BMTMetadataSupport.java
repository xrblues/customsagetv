package bmt;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaResource;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.impl.sage.FanartStorage;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.metadata.impl.sage.SageTVWithCentralFanartFolderPersistence;

import sagex.phoenix.fanart.IMetadataProviderInfo;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.IMetadataSupport;
import sagex.phoenix.fanart.SageUtil;
import sagex.phoenix.fanart.SimpleMediaFile;
import sagex.phoenix.fanart.FanartUtil.MediaType;

/**
 * Allows BMT to provide metadata support to the Phoenix Metadata apis.
 * 
 * @author seans
 * 
 */
public class BMTMetadataSupport implements IMetadataSupport {
    public BMTMetadataSupport() {
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
            return SageTVWithCentralFanartFolderPersistence.getSageTVMetadataMap(md);
        } catch (Exception e) {
            SageUtil.Log("Failed to get metadata for restult: " + result, e);
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

            SimpleMediaFile mf = SageUtil.GetSimpleMediaFile(media);
            
            // TODO: Synchronize the Media Types
            if (mf.getMediaType()==MediaType.TV) {
                md.set(MetadataKey.MEDIA_TYPE, SageTVWithCentralFanartFolderPersistence.TV_MEDIA_TYPE);
                SageUtil.Log("Only Updating Fanart for: " + mf.getTitle());
                FanartStorage.downloadFanart(mf.getTitle(), md, true);
                return true;
            } else {
                md.set(MetadataKey.MEDIA_TYPE, SageTVWithCentralFanartFolderPersistence.MOVIE_MEDIA_TYPE);
                SageUtil.Log("Updating Fanart and Metadata for: " + mf.getTitle());
                File file = SageUtil.GetFile(media);
                if (file == null) {
                    SageUtil.Log("Unable to do a metadata lookup using object: " + media);
                    return false;
                }
                // TODO: Later when we have a better mechanism for updating existing sage items,
                // we need to import the metadata as well.
                FanartStorage.downloadFanart(mf.getTitle(), md, true);
                //IMediaResource res = MediaResourceFactory.getInstance().createResource(file.toURI());
                //res.updateMetadata(md, true);
                return true;
            }
        } catch (Exception e) {
            SageUtil.Log("Failed to update metadata!", e);
        }
        return false;
    }

    public IMetadataSearchResult[] getMetadataSearchResults(String providerId, Object media) {
        SageUtil.Log("Searching for metadata; Provider: " + providerId + "; media: " + media);
        
        if (media == null) {
            SageUtil.Log("getMetadataSearchResults() was passed a null arg");
            return null;
        }
        
        if (providerId==null) {
            providerId = ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId(); 
        }

        try {
            SearchQuery q = null;
            if (media instanceof String) {
                SageUtil.Log("Creating a new SearchQuery for string: " + media);
                q = new SearchQuery((String) media);
            } else {
                SimpleMediaFile mf = SageUtil.GetSimpleMediaFile(media);
                if (mf.getMediaType() == MediaType.TV) {
                    q = SearchQueryFactory.getInstance().createTVQuery(mf.getTitle());
                } else if (mf.getMediaType() == MediaType.MUSIC) {
                    SageUtil.Log("Music Metadata Unsupported.");
                } else {
                    // check to see if this is a TV video file
                    File f = SageUtil.GetFile(media);
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

            SageUtil.Log("Metadata Search for: " + q);
            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(providerId);
            List<IMediaSearchResult> l = prov.search(q);
            if (l == null || l.size() == 0) {
                SageUtil.Log("No matches for: " + q);
            } else {
                return l.toArray(new IMetadataSearchResult[l.size()]);
            }
        } catch (Exception e) {
            SageUtil.Log("Failed to do a metadata lookup", e);
        }
        return null;
    }
}
