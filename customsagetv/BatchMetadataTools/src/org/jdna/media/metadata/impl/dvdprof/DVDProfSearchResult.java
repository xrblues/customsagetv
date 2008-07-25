package org.jdna.media.metadata.impl.dvdprof;

import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.MetaDataException;
import org.jdna.url.CachedUrl;

public class DVDProfSearchResult implements IVideoSearchResult {

	private int type;
	private String title;
	private String year;
	private String dataUrl;
	private float score;
	private DVDProfMetaData metadata = null;
	private CookieHandler handler= null;

	public DVDProfSearchResult(int type, String title, String year, String url, float score, CookieHandler handler) {
		this.type = type;
		this.title =title;
		this.year=year;
		this.dataUrl = url;
		this.score = score;
		this.handler=handler;
	}
	
	public IVideoMetaData getMetaData() throws MetaDataException {
		if (metadata==null) {
			try {
				metadata = new DVDProfMetaData(this, handler);
			} catch (Exception e) {
				// remove this url from the caced urls.... in case url caching is enabled
				CachedUrl.remove(this.dataUrl);
				throw new MetaDataException("Failed to get metadata for: " + dataUrl, e);
			}
		}
		return metadata;
	}

	public int getResultType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getYear() {
		return year;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public float getScore() {
		return score;
	}
	
	
	public String toString() {
		return String.format("DVDProfResult: Title: %s; Year: %s; Match: %s; Score: %s; Url: %s;",title, year, IVideoSearchResult.SEARCH_TYPE_NAMES[type], score, dataUrl);
	}
}
