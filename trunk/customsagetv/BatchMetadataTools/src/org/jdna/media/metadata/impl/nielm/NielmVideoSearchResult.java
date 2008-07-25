package org.jdna.media.metadata.impl.nielm;

import net.sf.sageplugins.sageimdb.DbTitleObject;
import net.sf.sageplugins.sageimdb.ImdbWebBackend;
import net.sf.sageplugins.sageimdb.Role;

import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.MetaDataException;

public class NielmVideoSearchResult implements IVideoSearchResult {
	private ImdbWebBackend db;
	private Role role;
	private NeilmIMDBMetaData metaData = null;
	private String title = null;
	private String year = null;
	
	public NielmVideoSearchResult(ImdbWebBackend db, Role r) {
		this.db=db;
		this.role = r;
		String buf = r.getName().getName();
		// Nielm's titles are 'Title (Year)'
		int br = buf.indexOf("(");
		if (br>0)  {
			title = buf.substring(0, br);
			year = buf.substring(br+1, br+5);
		} else {
			title = buf;
			year = "n/a";
		}
	}

	public IVideoMetaData getMetaData() throws MetaDataException {
		
		if (metaData==null) {
			DbTitleObject title;
			try {
				title = (DbTitleObject) role.getName().getDbObject(db);
			} catch (Exception e) {
				throw new MetaDataException("Failed to get metadata!", e);
			}
			metaData = new NeilmIMDBMetaData(db, title);
		}
		return metaData;
	}

	public int getResultType() {
		return RESULT_TYPE_UNKNOWN;
	}

	public String getTitle() {
		return title;
	}

	public String getYear() {
		// TODO Auto-generated method stub
		return year;
	}

}
