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

	private List<IVideoMetaDataProvider> providers = new ArrayList<IVideoMetaDataProvider>();
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
					addProvider(cl.newInstance());
				} catch (Throwable e) {
					log.error("Failed to register new Metadata Provider: " + p, e);
				}
			}
		} catch (Exception e) {
			log.error("Failed while registering providers", e);
		}

		if (getProvider(IMDBMetaDataProvider.PROVIDER_ID) == null) {
			log.debug("Adding in the default IMDB Provider.");
			addProvider(new IMDBMetaDataProvider());
		}

	}

	public List<IVideoMetaDataProvider> getProviders() {
		return providers;
	}

	public void addProvider(IVideoMetaDataProvider provider) {
		log.info("Adding MetaDataProvider: " + provider.getName());
		providers.add(provider);
	}

	public void removeProvider(IVideoMetaDataProvider provider) {
		providers.remove(provider);
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
		if (providers.size() == 0)
			throw new Exception("No Providers Installed!");
		return providers.get(0).search(searchType, arg);
	}

	public IVideoMetaDataProvider getProvider(String providerId) {
		IVideoMetaDataProvider provider = null;
		for (IVideoMetaDataProvider p : providers) {
			if (providerId.equals(p.getId())) {
				provider = p;
				break;
			}
		}
		return provider;
	}

	public IVideoMetaDataPersistence getDefaultPeristence() {
		return persistence;
	}
}
