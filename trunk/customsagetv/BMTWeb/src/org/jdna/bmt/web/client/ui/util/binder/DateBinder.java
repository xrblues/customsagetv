package org.jdna.bmt.web.client.ui.util.binder;

import java.util.Date;

import org.jdna.bmt.web.client.util.Property;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;

public class DateBinder extends FieldBinder<String> {
	private String mask = "yyyy-MM-dd";
	public DateBinder(Property<String> prop, String mask) {
		super(new TextBox(), prop);
		setMask(mask);
	}
	
	public void updateField() {
		((HasText) getWidget()).setText(getDate((String)getProp().get()));
	}
	
	public void updateProperty() {
		getProp().set(getDate(((HasText)getWidget()).getText()));
	}

	@Override
	public String getText() {
		return getDate(((HasText) getWidget()).getText());
	}

	@Override
	public void setText(String text) {
		((HasText) getWidget()).setText(getDate(text));
	}
	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}
	
	private String getDate(String in) {
		if (mask==null) {
			return in;
		}
		try {
			DateTimeFormat fmt = DateTimeFormat.getFormat(mask);
			Date d = fmt.parse(in);
			return fmt.format(d);
		} catch (Exception e) {
			return null;
		}
	}
	
}
