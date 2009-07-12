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
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;
import org.xml.sax.SAXException;

public class IMDBMetaDataProvider implements IMediaMetadataProvider {
    private static final Logger  log               = Logger.getLogger(IMDBMetaDataProvider.class);

    public static final String IMDB_TITLE_URL     = "http://{0}/find?s=tt&q={1}&x=0&y=0";
    
    public static final String   PROVIDER_ID       = "imdb";
    public static final String   PROVIDER_NAME     = "IMDb";
    public static final String   PROVIDER_ICON_URL = "http://i.media-imdb.com/images/nb15/logo2.gif";
    private static final String  PROVIDER_DESC     = "Fast, bare bones IMDb provider, no fanart, limited cast metadata";

    private static IProviderInfo info              = new ProviderInfo(PROVIDER_ID, PROVIDER_NAME, PROVIDER_DESC, PROVIDER_ICON_URL);
    private static final Type[] supportedSearchTypes = new SearchQuery.Type[] {SearchQuery.Type.MOVIE};

    private IMDBConfiguration cfg = new IMDBConfiguration();
    
    public IMDBMetaDataProvider() {
    }
    
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
            String url = MessageFormat.format(IMDB_TITLE_URL, cfg.getIMDbDomain(), eArg);
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

    private List<IMediaSearchResult> returnSingleResult(String redirectUrl) throws Exception {
        List<IMediaSearchResult> result = new ArrayList<IMediaSearchResult>();

        IMediaMetadata md = getMetaDataByUrl(redirectUrl);
        MediaSearchResult vsr = new MediaSearchResult();
        vsr.setProviderId(PROVIDER_ID);
        vsr.setUrl(MetadataAPI.getProviderDataUrl(md));
        vsr.setTitle(MetadataAPI.getMediaTitle(md));
        vsr.setYear(MetadataAPI.getYear(md));
        vsr.setScore(1.0f);
        vsr.setMetadataId(new MetadataID(IMDBMetaDataProvider.PROVIDER_ID, IMDBUtils.parseIMDBID(redirectUrl)));

        result.add(vsr);

        return result;
    }

    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
        return getMetaDataByUrl(result.getUrl());
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public Type[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }

    public IMediaMetadata getMetaDataById(MetadataID id) throws Exception {
        return getMetaDataByUrl(String.format(IMDB_TITLE_URL, cfg.getIMDbDomain(), id));
    }

    public IMediaMetadata getMetaDataByUrl(String url) throws Exception {
        try {
            IMDBMovieMetaDataParser parser = new IMDBMovieMetaDataParser(url);
            parser.parse();
            if (parser.hasError()) throw new IOException("Failed to Parse MetaData for url: " + url);
            return parser.getMetatData();
        } catch (SAXException e) {
            log.error("Failed to getMetaData for url: " + url);
            throw new IOException("Failed to parse providerDataUrl: " + url, e);
        }
    }
}
