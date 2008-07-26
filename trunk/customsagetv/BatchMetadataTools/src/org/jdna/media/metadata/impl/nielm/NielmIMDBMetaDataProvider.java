package org.jdna.media.metadata.impl.nielm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.sf.sageplugins.sageimdb.DbFailureException;
import net.sf.sageplugins.sageimdb.DbNotFoundException;
import net.sf.sageplugins.sageimdb.ImdbWebBackend;
import net.sf.sageplugins.sageimdb.Role;

import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.IVideoSearchResult;

public class NielmIMDBMetaDataProvider implements IVideoMetaDataProvider {
	private static final String PROVIDER_THUMNAIL_URL = "http://i.media-imdb.com/images/nb15/logo2.gif";
	public static final String PROVIDER_NAME = "IMDB Provider (Nielm)";
	public static final String PROVIDER_ID = "nielm_imdb";
	private ImdbWebBackend db = null;
	
	public NielmIMDBMetaDataProvider() {
		db = new ImdbWebBackend();
	}
	
	
	public List<IVideoSearchResult> search(int type, String arg) throws Exception {
		List<IVideoSearchResult> results = new ArrayList<IVideoSearchResult>();
		
		if (type==IVideoMetaDataProvider.SEARCH_TITLE) {
			try {
				Vector<Role> list = db.searchTitle(arg);
				for (Role r: list) {
					results.add(new NielmVideoSearchResult(db, r));
				}
			} catch (DbNotFoundException e) {
				throw new Exception("Database Not Found!", e);
			} catch (DbFailureException e) {
				throw new Exception("Search Failed!", e);
			}
		}
		
		return results;
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


	public IVideoMetaData getMetaData(String providerDataUrl) throws IOException {
		throw new UnsupportedOperationException("getMetaData(url) is not supported!");
	}

}
