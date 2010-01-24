package org.jdna.media.metadata;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.impl.composite.CompositeMetadataConfiguration;
import org.jdna.media.metadata.impl.composite.CompositeMetadataProvider;
import org.jdna.media.metadata.impl.composite.MetadataProviderContainer;
import org.jdna.media.metadata.impl.dvdproflocal.LocalDVDProfMetaDataProvider;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.metadata.impl.mymovies.MyMoviesMetadataProvider;
import org.jdna.media.metadata.impl.themoviedb.TheMovieDBMetadataProvider;
import org.jdna.media.metadata.impl.xbmc.XbmcMetadataProvider;
import org.jdna.media.metadata.impl.xbmc.XbmcScraper;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperParser;

import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

public class MediaMetadataFactory {

    private static MediaMetadataFactory instance = null;
    private static final Logger         log      = Logger.getLogger(MediaMetadataFactory.class);
    
    private MetadataConfiguration metadataCfg = GroupProxy.get(MetadataConfiguration.class);

    public static MediaMetadataFactory getInstance() {
        if (instance == null) instance = new MediaMetadataFactory();
        return instance;
    }

    private List<IMediaMetadataProvider> metadataProviders = new ArrayList<IMediaMetadataProvider>();

    public MediaMetadataFactory() {
        // create the default provider list
        try {
            String providers = metadataCfg.getMediaMetadataProviders();
            String mdps[] = providers.split(",");
            for (String p : mdps) {
                p = p.trim();
                if (!StringUtils.isEmpty(p)) {
                    try {
                        Class<IMediaMetadataProvider> cl = (Class<IMediaMetadataProvider>) Class.forName(p);
                        addMetaDataProvider(cl.newInstance());
                    } catch (NoClassDefFoundError cnfe) {
                        log.warn("No Class Implementation for provider: " + p);
                    } catch (ClassNotFoundException cnfe) {
                        log.warn("No Class Implementation for provider: " + p);
                    } catch (Throwable e) {
                        log.error("Failed to register new Metadata Provider: " + p, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed while registering providers", e);
        }

        if (getProvider(IMDBMetaDataProvider.PROVIDER_ID, MediaType.MOVIE) == null) {
            log.debug("Adding in the default IMDB Provider.");
            addMetaDataProvider(new IMDBMetaDataProvider());
        }
        
        // Now add in a couple of composite providers
        addCompositeProvider(MediaType.MOVIE, IMDBMetaDataProvider.PROVIDER_ID, TheMovieDBMetadataProvider.PROVIDER_ID, "IMDb plus TheMovieDB.org provider");
        addCompositeProvider(MediaType.MOVIE, LocalDVDProfMetaDataProvider.PROVIDER_ID, TheMovieDBMetadataProvider.PROVIDER_ID, "DVDProfiler plus TheMovieDB.org provider");
        addCompositeProvider(MediaType.MOVIE, MyMoviesMetadataProvider.PROVIDER_ID, TheMovieDBMetadataProvider.PROVIDER_ID, "MyMovies plus TheMovieDB.org provider");
        
        // for legacy compatibility, create the themoviedb-2 (which is an alias for imdb-2)
        addCompositeProvider(TheMovieDBMetadataProvider.PROVIDER_ID + "-2", MediaType.MOVIE, IMDBMetaDataProvider.PROVIDER_ID, TheMovieDBMetadataProvider.PROVIDER_ID, "Legacy Alias for the newer IMDb-2 provider");
        
        
        // now let's find all the xbmc scrapers and add them as well...
        File videos = new File("scrapers/xbmc/video");
        if (videos.exists()) {
            File[] files = videos.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".xml")) return true;
                    return false;
                }
            });
            
            for (File f : files) {
                try {
                    XbmcScraperParser parser = new XbmcScraperParser();
                    XbmcScraper scraper = parser.parseScraper(f);
                    if (scraper.isVideoScraper()) {
                        XbmcMetadataProvider p = new XbmcMetadataProvider(scraper);
                        addMetaDataProvider(p);
                    } else {
                        log.warn("Ignoring Xbmc Scraper: " + f.getAbsolutePath() + "; not a video scraper; Content: " + scraper.getContent());
                    }
                } catch (Exception e) {
                    log.error("Failed to create Xbmc Scraper: " + f.getAbsolutePath(), e);
                }
            }
        }
    }

    protected void addCompositeProvider(String id, MediaType type, String searcher, String details, String description) {
        IMediaMetadataProvider sprov = getProvider(searcher, type);
        IMediaMetadataProvider dprov = getProvider(details, type);
        IMediaMetadataProvider newProv = null;
        String newId = null;
        if (sprov!=null) {
            newId = sprov.getInfo().getId() +"-2";
        }
        
        if (id!=null) {
            newId = id;
        }
        
        newProv = getProvider(newId, type);
        
        if (newProv == null && getProvider(searcher, type)!=null && getProvider(details, type)!=null) {
            CompositeMetadataConfiguration cmc = new CompositeMetadataConfiguration();
            cmc.setName(sprov.getInfo().getName() + " + " + dprov.getInfo().getName());
            cmc.setDescription(description);
            cmc.setId(newId);
            cmc.setSearchProviderId(searcher);
            cmc.setDetailProviderId(details);
            cmc.setIconUrl(sprov.getInfo().getIconUrl());
            cmc.setCompositeDetailsMode(CompositeMetadataProvider.MODE_PREFER_SEARCHER);

            addMetaDataProvider(new CompositeMetadataProvider(cmc));
        }
    }

    protected void addCompositeProvider(MediaType type, String searcher, String details, String description) {
        addCompositeProvider(null, type, searcher, details, description);
    }

    public List<IMediaMetadataProvider> getMetaDataProviders() {
        return metadataProviders;
    }

    public void addMetaDataProvider(IMediaMetadataProvider provider) {
        log.info("Adding MetaDataProvider: " + provider.getInfo().getName());
        metadataProviders.add(provider);
    }

    public void removeMetaDataProvider(IMediaMetadataProvider provider) {
        metadataProviders.remove(provider);
    }

    public List<IMetadataSearchResult> search(String providerId, SearchQuery query) throws Exception {
        log.info("Searching: providerId: " + providerId + "; Query: " + query);
        IMediaMetadataProvider provider = getProvider(providerId, query.getMediaType());
        if (provider == null) {
            log.error("Failed to find metadata provider: " + providerId);
            throw new Exception("Unknown Provider: " + providerId);
        }
        return provider.search(query);
    }

    public List<IMetadataSearchResult> search(SearchQuery query) throws Exception {
        if (metadataProviders.size() == 0) throw new Exception("No Providers Installed!");
        return metadataProviders.get(0).search(query);
    }
    
    public IMediaMetadataProvider getProvider(String providerId, MediaType forType) {
        if (providerId==null) {
            if (MediaType.TV.equals(forType)) {
                providerId=metadataCfg.getTVProviders();
            } else if (MediaType.MOVIE.equals(forType)) {
                providerId=metadataCfg.getMovieProviders();
            } else if (MediaType.MUSIC.equals(forType)) {
                providerId=metadataCfg.getMusicProviders();
            } else {
                log.warn("No default metadata provider for type: " + forType);
                return null;
            }
        }
        
        if (!StringUtils.isEmpty(providerId) && providerId.contains(",")) {
            MetadataProviderContainer container = new MetadataProviderContainer(providerId);
            log.debug("Multiple Provider Ids were passed; " + providerId + "; Creating a MetadataProviderContainer for them...");
            Pattern p = Pattern.compile("([^,]+)");
            Matcher m = p.matcher(providerId);
            while (m.find()) {
                String id = m.group(1).trim();
                IMediaMetadataProvider provider = getProvider(id, forType);
                if (provider==null) {
                    log.warn("Skipping Provider because it does not exist: " + id);
                } else {
                    log.info("Added Provider: " + id + " to the Provider Search Container");
                    container.add(provider);
                }
            }
            return container;
        }
        
        // just a normal single id provider
        IMediaMetadataProvider provider = null;
        for (IMediaMetadataProvider p : metadataProviders) {
            if (providerId.equals(p.getInfo().getId())) {
                provider = p;
                break;
            }
        }
        
        if (provider==null) {
            log.warn("Mising or Unknown Provider: " + providerId);
        }
        
        return provider;
    }
    
    public IMediaMetadataProvider findById(String providerId) {
        // just a normal single id provider
        IMediaMetadataProvider provider = null;
        for (IMediaMetadataProvider p : metadataProviders) {
            if (providerId.equals(p.getInfo().getId())) {
                provider = p;
                break;
            }
        }
        return provider;
    }

    public boolean canProviderAcceptQuery(IMediaMetadataProvider provider, SearchQuery query) {
       if (provider.getSupportedSearchTypes()==null) return true;

       boolean accept =false;
       for (MediaType t : provider.getSupportedSearchTypes()) {
           if (t == query.getMediaType()) return true;
       }
       
       return accept;
    }
    
    @Deprecated
    public boolean isGoodSearch(List<IMetadataSearchResult> results) {
    	float goodScore = metadataCfg.getGoodScoreThreshold();
    	return results!=null && (results.size() > 0 && results.get(0).getScore() >= goodScore);
    }
    
    public IMetadataSearchResult getBestResultForQuery(List<IMetadataSearchResult> results, SearchQuery query) {
        IMetadataSearchResult res = null;
        
        String year = query.get(Field.YEAR);
        float goodScore = metadataCfg.getGoodScoreThreshold();
        boolean haveResults = results!=null && (results.size() > 0 && results.get(0).getScore() >= goodScore);
        
        if (results!=null && (results.size() > 0 && results.get(0).getScore() >= goodScore)) {
            if (StringUtils.isEmpty(year)) {
                // no year, so just return the first good match
                res = results.get(0);
            } else {
                // attempt to find a good match that also includes the right year
                for (IMetadataSearchResult r : results) {
                    if (r.getScore() >= goodScore && year.equals(r.getYear())) {
                        res = r;
                        break;
                    }
                }
            }
        }
        
        return res;
    }
    

}
