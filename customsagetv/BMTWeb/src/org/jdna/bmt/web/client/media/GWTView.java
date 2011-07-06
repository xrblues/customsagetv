package org.jdna.bmt.web.client.media;

import java.io.Serializable;

public class GWTView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id, label;
	private boolean visible=true;
	
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

	public GWTView() {
	}
	
	public GWTView(String id, String label) {
		this.id=id;
		this.label=label;
	}

	public GWTView(String id, String label, boolean visible) {
		this.id=id;
		this.label=label;
		this.visible=visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
