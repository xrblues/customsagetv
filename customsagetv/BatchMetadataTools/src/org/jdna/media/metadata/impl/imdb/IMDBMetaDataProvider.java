package org.jdna.media.metadata.impl.imdb;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;
import org.xml.sax.SAXException;

public class IMDBMetaDataProvider implements IMediaMetadataProvider {
    private static final Logger  log               = Logger.getLogger(IMDBMetaDataProvider.class);

    public static final String   IMDB_FIND_URL     = "http://www.imdb.com/find?s=tt&q={0}&x=0&y=0";
    public static final String   PROVIDER_ID       = "imdb";
    public static final String   PROVIDER_NAME     = "IMDB Provider (Stuckless)";
    public static final String   PROVIDER_ICON_URL = "http://i.media-imdb.com/images/nb15/logo2.gif";
    private static final String  PROVIDER_DESC     = "IMDB Provider that provides very resonable results, AND exact match searches.";

    private static IProviderInfo info              = new ProviderInfo(PROVIDER_ID, PROVIDER_NAME, PROVIDER_DESC, PROVIDER_ICON_URL);
    private static final Type[] supportedSearchTypes = new SearchQuery.Type[] {SearchQuery.Type.MOVIE};

    public String getIconUrl() {
        return PROVIDER_ICON_URL;
    }

    public String getId() {
        return PROVIDER_ID;
    }

    public String getName() {
        return PROVIDER_NAME;
    }

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        List<IMediaSearchResult> results = null;
        log.debug("Search Query: " + query);
        if (query.getType() == SearchQuery.Type.MOVIE) {
            String arg = query.get(SearchQuery.Field.TITLE);
            String eArg = URLEncoder.encode(arg);
            String url = MessageFormat.format(IMDB_FIND_URL, eArg);
            log.debug("IMDB Search Url: " + url);
            IMDBSearchResultParser parser = new IMDBSearchResultParser(url, arg);
            // don't follow the redirected urls
            parser.setFollowRedirects(false);
            try {
                parser.parse();
                if (parser.getFollowRedirects() == false && parser.isRedirecting()) {
                    log.debug("Returing Single Result for Search: " + arg);
                    return returnSingleResult(parser.getRedirectUrl());
                }
                if (parser.hasError()) {
                    throw new IOException("Failed to Parse the IMDB url correctly");
                }
                results = parser.getResults();
            } catch (IOException e) {
                log.error("Error Performing Search: " + arg, e);
            } catch (SAXException e) {
                log.error("Error Parsing Search: " + arg, e);
            }
        } else {
            log.error("Search Type no Supported: " + query.getType());
            throw new Exception("Seach Type Not Supported: " + query.getType());
        }
        
        return results;
    }

    private List<IMediaSearchResult> returnSingleResult(String redirectUrl) throws IOException {
        List<IMediaSearchResult> result = new ArrayList<IMediaSearchResult>();

        IMediaMetadata md = getMetaData(redirectUrl);
        MediaSearchResult vsr = new MediaSearchResult();
        vsr.setProviderId(PROVIDER_ID);
        vsr.setUrl(md.getProviderDataUrl());
        vsr.setTitle(md.getTitle());
        vsr.setYear(md.getYear());
        vsr.setScore(1.0f);
        vsr.setUniqueId(IMDBSearchResultParser.parseTitleId(redirectUrl));

        // the IMDBMovieMetaData implements the IVideoSearchResult interface
        result.add(vsr);

        return result;
    }

    public IMediaMetadata getMetaData(String providerDataUrl) throws IOException {
        try {
            String url = providerDataUrl;
            IMDBMovieMetaDataParser parser = new IMDBMovieMetaDataParser(url);
            parser.parse();
            if (parser.hasError()) throw new IOException("Failed to Parse MetaData for url: " + url);
            return parser.getMetatData();
        } catch (SAXException e) {
            log.error("Failed to getMetaData for url: " + providerDataUrl);
            throw new IOException("Failed to parse providerDataUrl: " + providerDataUrl, e);
        }
    }

    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
        return getMetaData(result.getUrl());
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaDataByIMDBId(String imdbId) throws Exception, UnsupportedOperationException {
        return getMetaData(String.format(IMDBSearchResultParser.TITLE_URL, imdbId));
    }

    public Type[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }

	public IMediaMetadata getMetaDataFromCompositeId(String compositeId)
			throws Exception {
		if (compositeId.startsWith("tt"))
			return getMetaDataByIMDBId(compositeId);
		else
			return null;
	}
}
