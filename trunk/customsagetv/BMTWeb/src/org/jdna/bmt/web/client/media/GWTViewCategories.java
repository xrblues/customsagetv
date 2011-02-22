package org.jdna.bmt.web.client.media;

import java.io.Serializable;
import java.util.ArrayList;

public class GWTViewCategories implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id, label;
	private ArrayList<GWTView> views = new ArrayList<GWTView>();

	public GWTViewCategories() {
	}
	
	public GWTViewCategories(String id, String label) {
		this.id=id;
		this.label=label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ArrayList<GWTView> getViews() {
		return views;
	}
}
