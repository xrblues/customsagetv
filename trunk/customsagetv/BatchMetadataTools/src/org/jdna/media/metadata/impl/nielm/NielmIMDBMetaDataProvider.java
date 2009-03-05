package org.jdna.media.metadata.impl.nielm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.sf.sageplugins.sageimdb.DbFailureException;
import net.sf.sageplugins.sageimdb.DbNotFoundException;
import net.sf.sageplugins.sageimdb.DbObjectRef;
import net.sf.sageplugins.sageimdb.DbTitleObject;
import net.sf.sageplugins.sageimdb.ImdbWebBackend;
import net.sf.sageplugins.sageimdb.ImdbWebObjectRef;
import net.sf.sageplugins.sageimdb.Role;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;
import org.jdna.media.metadata.impl.imdb.IMDBSearchResultParser;

public class NielmIMDBMetaDataProvider implements IMediaMetadataProvider {
    private static final Logger  log                   = Logger.getLogger(NielmIMDBMetaDataProvider.class);

    private static final String  PROVIDER_THUMNAIL_URL = "http://i.media-imdb.com/images/nb15/logo2.gif";
    public static final String   PROVIDER_NAME         = "IMDB Provider (Nielm)";
    public static final String   PROVIDER_ID           = "nielm_imdb";
    private static final String  PROVIDER_DESC         = "IMDB Provider that provides very detailed results, but no exact match searches.";

    private static IProviderInfo info                  = new ProviderInfo(PROVIDER_ID, PROVIDER_NAME, PROVIDER_DESC, PROVIDER_THUMNAIL_URL);

    private ImdbWebBackend       db                    = null;
    private static final Type[] supportedSearchTypes = new SearchQuery.Type[] {SearchQuery.Type.MOVIE};

    public NielmIMDBMetaDataProvider() {
        db = new ImdbWebBackend();
    }

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        List<IMediaSearchResult> results = new ArrayList<IMediaSearchResult>();

        if (query.getType() == SearchQuery.Type.MOVIE) {
            try {
                String arg = query.get(SearchQuery.Field.TITLE);
                Vector<Role> list = db.searchTitle(arg);
                for (Role r : list) {
                    MediaSearchResult vsr = new MediaSearchResult();
                    updateTitleAndYear(vsr, r);
                    vsr.setScore((float)org.jdna.util.Similarity.getInstance().compareStrings(arg,vsr.getTitle()));
                    vsr.setProviderId(NielmIMDBMetaDataProvider.PROVIDER_ID);
                    DbObjectRef objRef = r.getName();
                    if (objRef instanceof ImdbWebObjectRef) {
                        // set the imdb url as the ID for this result.
                        // that will enable us to find it later
                        vsr.setUrl(((ImdbWebObjectRef) objRef).getImdbRef());
                        vsr.setImdbId(IMDBSearchResultParser.parseTitleId(((ImdbWebObjectRef) objRef).getImdbRef()));
                    } else {
                        log.error("Imdb Search result was incorrect type: " + objRef.getClass().getName());
                    }
                    results.add(vsr);
                }
            } catch (DbNotFoundException e) {
                throw new Exception("Database Not Found!", e);
            } catch (DbFailureException e) {
                throw new Exception("Search Failed!", e);
            }
        }

        return results;
    }

    private void updateTitleAndYear(MediaSearchResult vsr, Role r) {
        String buf = r.getName().getName();
        String title, year;
        // Nielm's titles are 'Title (Year)'
        int br = buf.indexOf("(");
        if (br > 0) {
            title = buf.substring(0, br);
            year = buf.substring(br + 1, br + 5);
        } else {
            title = buf;
            year = "n/a";
        }
        vsr.setTitle(title);
        vsr.setYear(year);
    }

    public String getIconUrl() {
        return PROVIDER_THUMNAIL_URL;
    }

    public String getName() {
        return PROVIDER_NAME;
    }

    public String getId() {
        return PROVIDER_ID;
    }

    public IMediaMetadata getMetaData(String providerDataUrl) throws IOException {
        ImdbWebObjectRef objRef = new ImdbWebObjectRef(DbObjectRef.DB_TYPE_TITLE, "IMDB Url", providerDataUrl);
        DbTitleObject title;
        try {
            title = (DbTitleObject) objRef.getDbObject(db);
            return new NeilmIMDBMetaDataParser(db, title).getMetaData();
        } catch (Exception e) {
            log.error("IMDB Lookup Failed:" + providerDataUrl, e);
            throw new IOException(e);
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
}
