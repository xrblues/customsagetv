package org.jdna.media.metadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class VideoMetaDataFinder {
	private static VideoMetaDataFinder instance = null;
	private static final Logger log = Logger.getLogger(VideoMetaDataFinder.class);

	public static VideoMetaDataFinder getInstance() {
		if (instance==null) instance=new VideoMetaDataFinder();
		return instance;
	}
	
	private List<IVideoMetaDataProvider> providers = new ArrayList<IVideoMetaDataProvider>();

	public VideoMetaDataFinder() {
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
		if (providers.size() == 0)	throw new Exception("No Providers Installed!");
		return providers.get(0).search(searchType, arg);
	}

	public IVideoMetaDataProvider getProvider(String providerId) {
		IVideoMetaDataProvider provider=null;
		for (IVideoMetaDataProvider p : providers) {
			if (providerId.equals(p.getId())) {
				provider = p;
				break;
			}
		}
		return provider;
	}
}
