package org.jdna.media.metadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.impl.composite.CompositeMetadataConfiguration;
import org.jdna.media.metadata.impl.composite.CompositeMetadataProvider;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.metadata.impl.nielm.NielmIMDBMetaDataProvider;
import org.jdna.media.metadata.impl.themoviedb.TheMovieDBMetadataProvider;

public class MediaMetadataFactory {

    private static MediaMetadataFactory instance = null;
    private static final Logger         log      = Logger.getLogger(MediaMetadataFactory.class);

    public static MediaMetadataFactory getInstance() {
        if (instance == null) instance = new MediaMetadataFactory();
        return instance;
    }

    private List<IMediaMetadataProvider> metadataProviders = new ArrayList<IMediaMetadataProvider>();
    private List<ICoverProvider>         coverProviders    = new ArrayList<ICoverProvider>();

    private IMediaMetadataPersistence    persistence;

    public MediaMetadataFactory() {
        try {
            // create the default persistence...
            String cl = ConfigurationManager.getInstance().getMetadataConfiguration().getPersistenceClass();
            persistence = (IMediaMetadataPersistence) Class.forName(cl).newInstance();
            log.info("Using Default Persistence Engine: " + cl);
        } catch (Exception e) {
            log.error("Failed to create the default persistence engine.");
        }

        // create the default provider list
        try {
            String providers = ConfigurationManager.getInstance().getMetadataConfiguration().getMediaMetadataProviders();
            String mdps[] = providers.split(",");
            for (String p : mdps) {
                p = p.trim();
                try {
                    Class<IMediaMetadataProvider> cl = (Class<IMediaMetadataProvider>) Class.forName(p);
                    addMetaDataProvider(cl.newInstance());
                } catch (Throwable e) {
                    log.error("Failed to register new Metadata Provider: " + p, e);
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
        
        // This provider uses the default imdb for saerching, and Neil's for details
        if (getProvider(IMDBMetaDataProvider.PROVIDER_ID)!=null && getProvider(NielmIMDBMetaDataProvider.PROVIDER_ID)!=null) {
            CompositeMetadataConfiguration cmc = new CompositeMetadataConfiguration();
            cmc.setName("Composite Stuckless/Nielm IMDB Provider");
            cmc.setDescription("Composite Provider that uses the default IMDB provider for searching, and Neil's IMDB provider for details.  This useful if you want better plot descriptions.");
            cmc.setId(IMDBMetaDataProvider.PROVIDER_ID + "-2");
            cmc.setSearchProviderId(IMDBMetaDataProvider.PROVIDER_ID);
            cmc.setDetailProviderId(NielmIMDBMetaDataProvider.PROVIDER_ID);
            cmc.setIconUrl(IMDBMetaDataProvider.PROVIDER_ICON_URL);
            
            // Take User Rating and Genre information from IMDB, if it exists.
            cmc.setFieldsFromSearchProvider(MetadataKey.USER_RATING.getId() + ";" + MetadataKey.GENRE_LIST.getId());
            
            addMetaDataProvider(new CompositeMetadataProvider(cmc));
        }
        
        // This provider uses the imdb for searching and themoviedb.com for details
        if (getProvider(IMDBMetaDataProvider.PROVIDER_ID)!=null && getProvider(TheMovieDBMetadataProvider.PROVIDER_ID)!=null) {
            CompositeMetadataConfiguration cmc = new CompositeMetadataConfiguration();
            cmc.setName("Composite Stuckless IMDB/TheMovieDB Provider");
            cmc.setDescription("Composite Provider that uses the default IMDB provider for searching, and themoviedb.com for details.  This provider can provide decent results for posters and backdrop coverart.");
            cmc.setId(TheMovieDBMetadataProvider.PROVIDER_ID + "-2");
            cmc.setSearchProviderId(IMDBMetaDataProvider.PROVIDER_ID);
            cmc.setDetailProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
            cmc.setIconUrl(IMDBMetaDataProvider.PROVIDER_ICON_URL);
            
            // Take User Rating and Genre information from IMDB, if it exists.
            cmc.setFieldsFromSearchProvider(MetadataKey.USER_RATING.getId() + ";" + MetadataKey.GENRE_LIST.getId());

            addMetaDataProvider(new CompositeMetadataProvider(cmc));
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

    public List<IMediaSearchResult> search(String providerId, int searchType, String arg) throws Exception {
        log.info("Searching: providerId: " + providerId + "; searchType: " + searchType + "; query: " + arg);
        IMediaMetadataProvider provider = getProvider(providerId);
        if (provider == null) {
            log.error("Failed to find metadata provider: " + providerId);
            throw new Exception("Unknown Provider: " + providerId);
        }
        return provider.search(searchType, arg);
    }

    public List<IMediaSearchResult> search(int searchType, String arg) throws Exception {
        if (metadataProviders.size() == 0) throw new Exception("No Providers Installed!");
        return metadataProviders.get(0).search(searchType, arg);
    }

    public IMediaMetadataProvider getProvider(String providerId) {
        IMediaMetadataProvider provider = null;
        for (IMediaMetadataProvider p : metadataProviders) {
            if (providerId.equals(p.getInfo().getId())) {
                provider = p;
                break;
            }
        }
        return provider;
    }

    public IMediaMetadataPersistence getDefaultPeristence() {
        return persistence;
    }

    public List<ICoverProvider> getCoverProviders() {
        return coverProviders;
    }

    public void addCoverProvider(ICoverProvider provider) {
        log.info("Adding Cover Provider: " + provider.getInfo().getName());
        coverProviders.add(provider);
    }

    public void removeCoverProvider(ICoverProvider provider) {
        metadataProviders.remove(provider);
    }

}
