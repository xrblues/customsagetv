package org.jdna.bmt.web.client.ui.util.binder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdna.bmt.web.client.util.Property;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;

public class DateBinder extends FieldBinder<String> {
	private List<String> masks = new ArrayList<String>();
	
	public DateBinder(Property<String> prop, String mask) {
		super(new TextBox(), prop);
		masks.add("yyyy-MM-dd");
		masks.add("yyyy-MM-dd HH:mm");
		masks.add("yyyy-MM-dd HH:mm:ss");
		if (!mask.contains(mask)) {
			masks.add(mask);
		}
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
	
	private String getDate(String in) {
		for (String mask : masks) {
			try {
				DateTimeFormat fmt = DateTimeFormat.getFormat(mask);
				Date d = fmt.parse(in);
				return fmt.format(d);
			} catch (Exception e) {
				//e.printStackTrace();
				//return null;
			}
		}
		return null;
	}
	
}
