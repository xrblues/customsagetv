package org.jdna.bmt.web.client.ui.util.binder;

import org.jdna.bmt.web.client.util.Property;

import com.google.gwt.user.client.ui.CheckBox;

public class CheckBinder extends FieldBinder<Boolean> {
	public CheckBinder(CheckBox w, Property<Boolean> prop) {
		super(w, prop);
	}

	public CheckBinder(Property<Boolean> prop) {
		this(new CheckBox(), prop);
	}
	
	public void updateField() {
		((CheckBox)getWidget()).setValue((Boolean)getProp().get());
	}
	
	public void updateProperty() {
		getProp().set(((CheckBox)getWidget()).getValue());
	}

	@Override
	public String getText() {
		return String.valueOf(((CheckBox)getWidget()).getValue());
	}

	@Override
	public void setText(String text) {
		((CheckBox)getWidget()).setValue("true".equals(text));
	}
}
