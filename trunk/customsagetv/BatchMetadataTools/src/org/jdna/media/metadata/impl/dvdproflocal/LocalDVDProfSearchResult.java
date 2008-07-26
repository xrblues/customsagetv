package org.jdna.media.metadata.impl.dvdproflocal;

import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoSearchResult;

public class LocalDVDProfSearchResult implements IVideoSearchResult {

	private int type;
	private String name;
	private String date;
	private String id;
	private float score;

	public LocalDVDProfSearchResult(int type, String name, String date,	String id, float score) {
		this.type=type;
		this.name=name;
		this.date=date;
		this.id=id;
		this.score=score;
	}

	public IVideoMetaData getMetaData() throws Exception {
		return LocalDVDProfMetaDataProvider.getInstance().getMetaData(getId());
	}

	public int getResultType() {
		return type;
	}

	public String getTitle() {
		return name;
	}

	public String getYear() {
		return date;
	}

	public String getId() {
		return id;
	}
	
	public float getScore() {
		return score;
	}
}
