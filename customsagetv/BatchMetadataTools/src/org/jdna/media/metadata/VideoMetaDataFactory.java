package org.jdna.media.metadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;

public class VideoMetaDataFactory {
	private static VideoMetaDataFactory instance = null;
	private static final Logger log = Logger.getLogger(VideoMetaDataFactory.class);

	public static VideoMetaDataFactory getInstance() {
		if (instance == null)
			instance = new VideoMetaDataFactory();
		return instance;
	}

	private List<IVideoMetaDataProvider> metadataProviders = new ArrayList<IVideoMetaDataProvider>();
	private List<ICoverProvider> coverProviders = new ArrayList<ICoverProvider>();
	
	private IVideoMetaDataPersistence persistence;

	public VideoMetaDataFactory() {
		try {
			// create the default persistence...
			String cl = ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "PersistenceClass", "org.jdna.media.metadata.impl.sage.SageVideoMetaDataPersistence");
			persistence = (IVideoMetaDataPersistence) Class.forName(cl).newInstance();
			log.info("Using Default Persistence Engine: " + cl);
		} catch (Exception e) {
			log.error("Failed to create the default persistence engine.");
		}

		// create the default provider list
		try {
			String providers = ConfigurationManager.getInstance().getProperty("VideoMetaData", "MetadataProviders",
					"org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider,org.jdna.media.metadata.impl.nielm.NielmIMDBMetaDataProvider,org.jdna.media.metadata.impl.dvdprof.DVDProfMetaDataProvider,org.jdna.media.metadata.impl.dvdproflocal.LocalDVDProfMetaDataProvider");
			String mdps[] = providers.split(",");
			for (String p : mdps) {
				p = p.trim();
				try {
					Class<IVideoMetaDataProvider> cl = (Class<IVideoMetaDataProvider>) Class.forName(p);
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

	}

	public List<IVideoMetaDataProvider> getMetaDataProviders() {
		return metadataProviders;
	}

	public void addMetaDataProvider(IVideoMetaDataProvider provider) {
		log.info("Adding MetaDataProvider: " + provider.getInfo().getName());
		metadataProviders.add(provider);
	}

	public void removeMetaDataProvider(IVideoMetaDataProvider provider) {
		metadataProviders.remove(provider);
	}

	public IVideoMetaData getMetaData(IVideoSearchResult result) throws Exception {
		IVideoMetaDataProvider provider = getProvider(result.getProviderId());
		return provider.getMetaData(result);
	}
	
	public List<IVideoSearchResult> search(String providerId, int searchType, String arg) throws Exception {
		log.info("Searching: providerId: " + providerId + "; searchType: " + searchType + "; query: " + arg);
		IVideoMetaDataProvider provider = getProvider(providerId);
		if (provider == null) {
			log.error("Failed to find metadata provider: " + providerId);
			throw new Exception("Unknown Provider: " + providerId);
		}
		return provider.search(searchType, arg);
	}

	public List<IVideoSearchResult> search(int searchType, String arg) throws Exception {
		if (metadataProviders.size() == 0)
			throw new Exception("No Providers Installed!");
		return metadataProviders.get(0).search(searchType, arg);
	}

	public IVideoMetaDataProvider getProvider(String providerId) {
		IVideoMetaDataProvider provider = null;
		for (IVideoMetaDataProvider p : metadataProviders) {
			if (providerId.equals(p.getInfo().getId())) {
				provider = p;
				break;
			}
		}
		return provider;
	}

	public IVideoMetaDataPersistence getDefaultPeristence() {
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
