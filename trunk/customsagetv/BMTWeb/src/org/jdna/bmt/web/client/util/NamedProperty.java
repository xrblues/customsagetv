package org.jdna.bmt.web.client.util;

import com.google.gwt.user.client.ui.HasName;

/**
 * Property that has a label
 * @author sean
 *
 * @param <T>
 */
public class NamedProperty<T> extends Property<T> implements HasName {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NamedProperty() {
	}

	public NamedProperty(String name, T value) {
		super(value);
		this.name=name;
	}
}
