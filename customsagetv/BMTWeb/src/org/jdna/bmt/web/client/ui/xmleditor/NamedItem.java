package org.jdna.bmt.web.client.ui.xmleditor;

import java.io.Serializable;

public class NamedItem implements Serializable {
	private String name;
	private String value;
	
	public NamedItem() {
	}
	public NamedItem(String name, String value) {
		this.name=name;
		this.value=value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
