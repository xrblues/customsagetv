package org.jdna.bmt.web.client.ui.util.binder;

import org.jdna.bmt.web.client.util.Property;

import com.google.gwt.user.client.ui.TextArea;

public class TextAreaBinder extends TextBinder {
	public TextAreaBinder(TextArea w, Property<String> prop) {
		super(w, prop);
	}
	
	public TextAreaBinder(Property<String> prop) {
		this(new TextArea(), prop);
	}
}
