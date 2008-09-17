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
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.VideoSearchResult;

public class NielmIMDBMetaDataProvider implements IVideoMetaDataProvider {
	private static final Logger log  = Logger.getLogger(NielmIMDBMetaDataProvider.class);
	
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
					VideoSearchResult vsr = new VideoSearchResult();
					updateTitleAndYear(vsr, r);
					vsr.setResultType(IVideoSearchResult.RESULT_TYPE_UNKNOWN);
					vsr.setProviderId(NielmIMDBMetaDataProvider.PROVIDER_ID);
					DbObjectRef objRef = r.getName();
					if (objRef instanceof ImdbWebObjectRef) {
						// set the imdb url as the ID for this result.
						// that will enable us to find it later
						vsr.setId(((ImdbWebObjectRef) objRef).getImdbRef());
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

	private void updateTitleAndYear(VideoSearchResult vsr, Role r) {
		String buf = r.getName().getName();
		String title, year;
		// Nielm's titles are 'Title (Year)'
		int br = buf.indexOf("(");
		if (br>0)  {
			title = buf.substring(0, br);
			year = buf.substring(br+1, br+5);
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

	public IVideoMetaData getMetaData(String providerDataUrl) throws IOException {
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

	public IVideoMetaData getMetaData(IVideoSearchResult result) throws Exception {
		return getMetaData(result.getId());
	}

}
