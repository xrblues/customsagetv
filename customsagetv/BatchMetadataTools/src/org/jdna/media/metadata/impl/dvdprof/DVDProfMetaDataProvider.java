package org.jdna.media.metadata.impl.dvdprof;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.url.CachedUrl;
import org.jdna.url.CookieHandler;

public class DVDProfMetaDataProvider implements IMediaMetadataProvider {
    private static final Logger  log               = Logger.getLogger(DVDProfMetaDataProvider.class);

    public static final String   PROVIDER_ID       = "dvdprofiler";
    public static final String   PROVIDER_NAME     = "Url DVD Profiler Provider";
    public static final String   PROVIDER_ICON_URL = "http://www.invelos.com/images/Logo.png";
    private static final String  PROVIDER_DESC     = "DVD Profiler Provider using remote Url (Stuckless).";

    private static IProviderInfo info              = new ProviderInfo(PROVIDER_ID, PROVIDER_NAME, PROVIDER_DESC, PROVIDER_ICON_URL);

    private CookieHandler        cookieHandler;
    private boolean              rebuildIndex      = false;
    private boolean              initialized       = false;

    public DVDProfMetaDataProvider() throws Exception {
    }

    public String getIconUrl() {
        return PROVIDER_ICON_URL;
    }

    public String getId() {
        return PROVIDER_ID;
    }

    public IMediaMetadata getMetaData(String providerDataUrl) throws Exception {
        if (!initialized) initialize();

        IMediaMetadata metadata = null;
        try {
            metadata = new DVDProfMetaDataParser(providerDataUrl, cookieHandler).getMetaData();
        } catch (Exception e) {
            // remove this url from the caced urls.... in case url caching is
            // enabled
            CachedUrl.remove(providerDataUrl);
            throw new Exception("Failed to get metadata for: " + providerDataUrl, e);
        }
        return metadata;
    }

    public String getName() {
        return PROVIDER_NAME;
    }

    public List<IMediaSearchResult> search(int searchType, String arg) throws Exception {
        if (!initialized) initialize();

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
            throw new Exception("Failed to find: " + arg, e);
        }
    }

    private void initialize() throws Exception {
        initialized = true;

        String urls = ConfigurationManager.getInstance().getDVDProfilerConfiguration().getProfileUrls();
        if (urls == null) {
            throw new Exception("No Profile Urls specified.  Please add some urls to your configuration: " + this.getClass().getName() + ".profileUrls");
        } else {
            String profs[] = urls.split(",");
            cookieHandler = new CookieHandler(profs[0]);

        }

        rebuildIndex = ConfigurationManager.getInstance().getDVDProfilerConfiguration().isForceRebuild();
    }

    private void rebuildIndexes() throws Exception {
        log.debug("Rebuilding Indexes....");

        MovieIndex.getInstance().clean();

        String urls = ConfigurationManager.getInstance().getDVDProfilerConfiguration().getProfileUrls();
        if (urls == null) {
            log.error("No Profile Urls specified.  Please add some urls to your configuration: " + this.getClass().getName() + ".profileUrls");
        } else {
            String profs[] = urls.split(",");
            MovieIndex.getInstance().beginIndexing();
            for (String u : profs) {
                log.debug("Indexing: " + u);
                DVDProfFrameParser fparser = new DVDProfFrameParser(u);
                try {
                    fparser.parse(cookieHandler);
                    String movieListUrl = fparser.getMovieListUrl();
                    if (movieListUrl == null) {
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

    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
        return getMetaData(result.getId());
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaDataByIMDBId(String imdbId) throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException("DVDProfiler Doesn't Know how to handle IMDB ids");
    }
}
