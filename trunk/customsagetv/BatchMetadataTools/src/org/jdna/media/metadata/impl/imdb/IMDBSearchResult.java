package org.jdna.media.metadata.impl.imdb;

import java.io.IOException;
import java.text.MessageFormat;

import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.MetaDataException;

public class IMDBSearchResult implements IVideoSearchResult {
	private static final String TITLE_URL = "http://www.imdb.com/title/{0}/"; 
	private int resultType;
	private String title;
	private String year;
	private String titleId;
	private IMDBMovieMetaData metadata = null;
	
	public IMDBSearchResult(int type) {
		this.resultType=type;
	}
	
	public IVideoMetaData getMetaData() throws MetaDataException {
		if (metadata==null) {
			try {
				String url = MessageFormat.format(TITLE_URL, getTitleId());
				IMDBMovieMetaDataParser parser = new IMDBMovieMetaDataParser(url);
				parser.parse();
				if (parser.hasError()) throw new IOException("Failed to Parse MetaData for url: " + url);
				metadata = parser.getMetatData();
			} catch (Exception e) {
				throw new MetaDataException("Failed to get MetaData for Movie", e);
			}
		}
		
		return metadata;
	}

	public int getResultType() {
		return resultType;
	}

	public String getTitle() {
		return title;
	}

	public String getYear() {
		return year;
	}

	public void setResultType(int resultType) {
		this.resultType = resultType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

}
