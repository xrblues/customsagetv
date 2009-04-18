package org.jdna.media.metadata;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.impl.composite.CompositeMetadataConfiguration;
import org.jdna.media.metadata.impl.composite.CompositeMetadataProvider;
import org.jdna.media.metadata.impl.composite.MetadataProviderContainer;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.metadata.impl.themoviedb.TheMovieDBMetadataProvider;
import org.jdna.media.metadata.impl.xbmc.XbmcMetadataProvider;
import org.jdna.media.metadata.impl.xbmc.XbmcScraper;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperParser;

public class MediaMetadataFactory {

    private static MediaMetadataFactory instance = null;
    private static final Logger         log      = Logger.getLogger(MediaMetadataFactory.class);

    public static MediaMetadataFactory getInstance() {
        if (instance == null) instance = new MediaMetadataFactory();
        return instance;
    }

    private List<IMediaMetadataProvider> metadataProviders = new ArrayList<IMediaMetadataProvider>();
    private List<ICoverProvider>         coverProviders    = new ArrayList<ICoverProvider>();

    public MediaMetadataFactory() {
        // create the default provider list
        try {
            String providers = ConfigurationManager.getInstance().getMetadataConfiguration().getMediaMetadataProviders();
            String mdps[] = providers.split(",");
            for (String p : mdps) {
                p = p.trim();
                if (!StringUtils.isEmpty(p)) {
                    try {
                        Class<IMediaMetadataProvider> cl = (Class<IMediaMetadataProvider>) Class.forName(p);
                        addMetaDataProvider(cl.newInstance());
                    } catch (Throwable e) {
                        log.error("Failed to register new Metadata Provider: " + p, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed while registering providers", e);
        }

        if (getProvider(IMDBMetaDataProvider.PROVIDER_ID) == null) {
            log.debug("Adding in the default IMDB Provider.");
            addMetaDataProvider(new IMDBMetaDataProvider());
        }
        
        // Now add in a couple of composite providers
        
        // This provider uses the imdb for searching and themoviedb.com for details
        if (getProvider(IMDBMetaDataProvider.PROVIDER_ID)!=null && getProvider(TheMovieDBMetadataProvider.PROVIDER_ID)!=null) {
            CompositeMetadataConfiguration cmc = new CompositeMetadataConfiguration();
            cmc.setName("IMDb/themoviedb.org");
            cmc.setDescription("Metadata Provider that uses IMDb for searching, but themoviedb.org for metadata completion and fanart.");
            cmc.setId(TheMovieDBMetadataProvider.PROVIDER_ID + "-2");
            cmc.setSearchProviderId(IMDBMetaDataProvider.PROVIDER_ID);
            cmc.setDetailProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
            cmc.setIconUrl(IMDBMetaDataProvider.PROVIDER_ICON_URL);
            cmc.setCompositeDetailsMode(CompositeMetadataProvider.MODE_PREFER_SEARCHER);

            addMetaDataProvider(new CompositeMetadataProvider(cmc));
        }
        
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

    public List<IMediaSearchResult> search(String providerId, SearchQuery query) throws Exception {
        log.info("Searching: providerId: " + providerId + "; Query: " + query);
        IMediaMetadataProvider provider = getProvider(providerId);
        if (provider == null) {
            log.error("Failed to find metadata provider: " + providerId);
            throw new Exception("Unknown Provider: " + providerId);
        }
        return provider.search(query);
    }

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        if (metadataProviders.size() == 0) throw new Exception("No Providers Installed!");
        return metadataProviders.get(0).search(query);
    }

    public IMediaMetadataProvider getProvider(String providerId) {
        if (providerId==null) providerId=ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId();
        if (!StringUtils.isEmpty(providerId) && providerId.contains(",")) {
            MetadataProviderContainer container = new MetadataProviderContainer(providerId);
            log.debug("Multiple Provider Ids were passed; " + providerId + "; Creating a MetadataProviderContainer for them...");
            Pattern p = Pattern.compile("([^,]+)");
            Matcher m = p.matcher(providerId);
            while (m.find()) {
                String id = m.group(1).trim();
                container.add(getProvider(id));
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

    public List<ICoverProvider> getCoverProviders() {
        return coverProviders;
    }

    public void addCoverProvider(ICoverProvider provider) {
        log.info("Adding Cover Provider: " + provider.getInfo().getName());
        coverProviders.add(provider);
    }

    public void removeCoverProvider(ICoverProvider cprovider) {
        coverProviders.remove(cprovider);
    }
    
    public boolean canProviderAcceptQuery(IMediaMetadataProvider provider, SearchQuery query) {
       boolean accept =false;
       
       for (SearchQuery.Type t : provider.getSupportedSearchTypes()) {
           if (t == query.getType()) return true;
       }
       
       return accept;
    }
    
    public boolean isGoodSearch(List<IMediaSearchResult> results) {
    	float goodScore = ConfigurationManager.getInstance().getMetadataConfiguration().getGoodScoreThreshold();
    	return results!=null && (results.size() > 0 && results.get(0).getScore() >= goodScore);
        //return results!=null && (results.size() > 0 && (results.get(0).getResultType() == SearchResultType.POPULAR || results.get(0).getResultType() == SearchResultType.EXACT));
    }

}
