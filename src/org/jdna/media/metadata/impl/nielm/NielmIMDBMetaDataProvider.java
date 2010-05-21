package org.jdna.media.metadata.impl.nielm;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.HasFindByIMDBID;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.impl.imdb.IMDBConfiguration;
import org.jdna.media.metadata.impl.imdb.IMDBUtils;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

public class NielmIMDBMetaDataProvider implements IMediaMetadataProvider, HasFindByIMDBID {
    private static final Logger      log                   = Logger.getLogger(NielmIMDBMetaDataProvider.class);

    private static final String      PROVIDER_THUMNAIL_URL = "http://i.media-imdb.com/images/nb15/logo2.gif";
    public static final String       PROVIDER_NAME         = "IMDb (Nielm)";
    public static final String       PROVIDER_ID           = "nielm_imdb";
    private static final String      PROVIDER_DESC         = "IMDd provider using Nielm's IMDb api.";

    private static IProviderInfo     info                  = new ProviderInfo(PROVIDER_ID, PROVIDER_NAME, PROVIDER_DESC, PROVIDER_THUMNAIL_URL);

    private ImdbWebBackend           db                    = null;
    private static final MediaType[] supportedSearchTypes  = new MediaType[] { MediaType.MOVIE };

    private IMDBConfiguration        cfg                   = new IMDBConfiguration();

    public NielmIMDBMetaDataProvider() {
        db = new ImdbWebBackend();
    }

    public List<IMetadataSearchResult> search(SearchQuery query) throws Exception {
        List<IMetadataSearchResult> results = new ArrayList<IMetadataSearchResult>();

        // search by ID, if the ID is present
        if (!StringUtils.isEmpty(query.get(SearchQuery.Field.ID))) {
            List<IMetadataSearchResult> res = MetadataUtil.searchById(this, query, query.get(SearchQuery.Field.ID));
            if (res!=null) {
                return res;
            }
        }
        
        // carry on normal search

        try {
            String arg = query.get(SearchQuery.Field.QUERY);
            Vector<Role> list = db.searchTitle(arg);
            for (Role r : list) {
                MediaSearchResult vsr = new MediaSearchResult();
                MetadataUtil.copySearchQueryToSearchResult(query, vsr);
                updateTitleAndYear(vsr, r);
                vsr.setScore(MetadataUtil.calculateScore(arg, vsr.getTitle()));
                vsr.setProviderId(NielmIMDBMetaDataProvider.PROVIDER_ID);
                DbObjectRef objRef = r.getName();
                if (objRef instanceof ImdbWebObjectRef) {
                    // set the imdb url as the ID for this result.
                    // that will enable us to find it later
                    vsr.setUrl(((ImdbWebObjectRef) objRef).getImdbRef());
                    vsr.setId(IMDBUtils.parseIMDBID(((ImdbWebObjectRef) objRef).getImdbRef()));
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

    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception {
        if (MetadataUtil.hasMetadata(result)) return MetadataUtil.getMetadata(result);
        
        if (StringUtils.isEmpty(result.getUrl())) {
            ((MediaSearchResult) result).setUrl(IMDBUtils.createDetailUrl(result.getId()));
        }
        return getMetaDataByUrl(result.getUrl());
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public MediaType[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }

    private String getUrlForId(String id) {
        return IMDBUtils.createDetailUrl(id);
    }

    private IMediaMetadata getMetaDataByUrl(String url) throws Exception {
        ImdbWebObjectRef objRef = new ImdbWebObjectRef(DbObjectRef.DB_TYPE_TITLE, "IMDB Url", url);
        DbTitleObject title;
        title = (DbTitleObject) objRef.getDbObject(db);
        return new NeilmIMDBMetaDataParser(db, title).getMetaData();
    }

    public IMediaMetadata getMetadataForIMDBId(String imdbid) {
        try {
            return getMetaDataByUrl(getUrlForId(imdbid));
        } catch (Exception e) {
            log.warn("IMDB Lookup Failed by Id: " + imdbid, e);
        }
        return null;
    }
}
