package org.jdna.media.metadata.impl.dvdprof;

import org.jdna.media.metadata.ICastMember;

public class DVDProfCastMember implements ICastMember {
	private String id;
	private String name;
	private String part;
	private int type;
	private String providerDataUrl;

	public DVDProfCastMember() {
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPart() {
		return part;
	}

	public int getType() {
		return type;
	}

	public String getProviderDataUrl() {
		return providerDataUrl;
	}

	public void setProviderDataUrl(String providerDataUrl) {
		this.providerDataUrl = providerDataUrl;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public void setType(int type) {
		this.type = type;
	}
}
