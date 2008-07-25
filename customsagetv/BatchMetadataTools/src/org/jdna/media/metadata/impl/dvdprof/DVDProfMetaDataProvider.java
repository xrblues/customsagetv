package org.jdna.media.metadata.impl.dvdprof;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.SearchException;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;

public class DVDProfMetaDataProvider implements IVideoMetaDataProvider {
	private static final Logger log = Logger.getLogger(IMDBMetaDataProvider.class);

	public static final String PROVIDER_ID = "dvdprofiler";
	public static final String PROVIDER_NAME = "DVD Profiler Provider by Stuckless";
	public static final String PROVIDER_ICON_URL = "http://www.invelos.com/images/Logo.png";

	private CookieHandler cookieHandler;
	private boolean rebuildIndex = false;
	
	public DVDProfMetaDataProvider() throws Exception {
		String urls = ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "profileUrls", null);
		if (urls == null) {
			throw new Exception("No Profile Urls specified.  Please add some urls to your configuration: " + this.getClass().getName() + ".profileUrls");
		} else {
			String profs[] = urls.split(",");
			cookieHandler  = new CookieHandler(profs[0]);
			
		}
		
		rebuildIndex = Boolean.parseBoolean(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "forceRebuild", "false"));
	}
	
	public String getIconUrl() {
		return PROVIDER_ICON_URL;
	}

	public Object getId() {
		return PROVIDER_ID;
	}

	public IVideoMetaData getMetaData(String providerDataUrl) throws IOException {
		return null;
	}

	public String getName() {
		return PROVIDER_NAME;
	}

	public List<IVideoSearchResult> search(int searchType, String arg)	throws SearchException {
		if (shouldRebuildIndexes()) {
			try {
				rebuildIndexes();
			} catch (Exception e) {
				log.error("Failed to rebuild the indexes for DVD Profiler!", e);
			}
		}
		
		try {
			return MovieIndex.getInstance().searchTitle(arg, cookieHandler);
		} catch (Exception e) {
			throw new SearchException("Failed to find: " + arg, e);
		}
	}

	private void rebuildIndexes() throws Exception {
		log.debug("Rebuilding Indexes....");

		MovieIndex.getInstance().clean();
		
		String urls = ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "profileUrls", null);
		if (urls == null) {
			log.error("No Profile Urls specified.  Please add some urls to your configuration: " + this.getClass().getName() + ".profileUrls");
		} else {
			String profs[] = urls.split(",");
			MovieIndex.getInstance().beginIndexing();
			for (String u : profs) {
				DVDProfFrameParser fparser = new DVDProfFrameParser(u);
				try {
					fparser.parse(cookieHandler);
					String movieListUrl = fparser.getMovieListUrl();
					if (movieListUrl==null) {
						throw new Exception("Failed to get a movie list url from this url: " + u);
					} else {
						log.debug("Got a Movie List Url: [" + movieListUrl + "]; Begin the Index Parser.");
					}
					
					// Now that we have a movie list url, parse and index it.
					MovieListIndexerParser indexer = new MovieListIndexerParser(movieListUrl);
					indexer.parse(cookieHandler);
					
				} catch (Exception e) {
					log.error("Skipping DVD Profiler Url: " + u, e);
				}
			}
			MovieIndex.getInstance().endIndexing();
		}
		
		rebuildIndex = false;
	}

	private boolean shouldRebuildIndexes() {
		return MovieIndex.getInstance().isNew() || rebuildIndex;
	}

	
}
