package org.jdna.bmt.web.client.ui.util.binder;

import org.jdna.bmt.web.client.util.Property;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;

public class NumberBinder extends TextBinder {
	private boolean zeroIsNull;
	
	public void setZeroIsNull(boolean zeroIsNull) {
		this.zeroIsNull = zeroIsNull;
	}

	public boolean isZeroNull() {
		return zeroIsNull;
	}

	public NumberBinder(HasText w, Property<String> prop) {
		super(w, prop);
	}
	
	public NumberBinder(Property<String> prop) {
		super(prop);
	}

	public NumberBinder(Property<String> prop, boolean zeroIsNull) {
		this(new TextBox(), prop);
		setZeroIsNull(zeroIsNull);
	}
	
	public void updateField() {
		((HasText) getWidget()).setText(getNumber((String)getProp().get()));
	}
	
	private String getNumber(String s) {
		String text = s;
		int val = StringUtils.parseInt(text, 0);
		if (isZeroNull() && val==0) return null;
		return String.valueOf(val);
	}
	
	public void updateProperty() {
		getProp().set(getText());
	}

	@Override
	public String getText() {
		return getNumber(super.getText());
	}

	@Override
	public void setText(String text) {
		super.setText(getNumber(String.valueOf(StringUtils.parseInt(text, 0))));
	}
}
