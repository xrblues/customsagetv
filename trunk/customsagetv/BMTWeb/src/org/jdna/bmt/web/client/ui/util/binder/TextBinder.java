package org.jdna.bmt.web.client.ui.util.binder;

import org.jdna.bmt.web.client.util.Property;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TextBinder extends FieldBinder<String> {
	public TextBinder(HasText w, Property<String> prop) {
		super((Widget)w, prop);
	}
	
	public TextBinder(Property<String> prop) {
		this(new TextBox(), prop);
	}
	
	public void updateField() {
		((HasText) getWidget()).setText((String)getProp().get());
	}
	
	public void updateProperty() {
		getProp().set(((HasText)getWidget()).getText());
	}

	@Override
	public String getText() {
		return ((HasText) getWidget()).getText();
	}

	@Override
	public void setText(String text) {
		((HasText) getWidget()).setText(text);
	}
}
