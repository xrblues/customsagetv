package org.jdna.bmt.web.client.ui.util.binder;

import org.jdna.bmt.web.client.util.Property;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

public abstract class FieldBinder<T> {
	private Widget widget = null;
	private Property<T> prop = null;
	
	public FieldBinder(Widget w, Property<T> prop) {
		this.widget=w;
		this.prop = prop;
	}
	
	public abstract void updateField();	
	public abstract void updateProperty();
	public abstract String getText();
	public abstract void setText(String text);

	public Widget getWidget() {
		return widget;
	}

	public Property<T> getProp() {
		return prop;
	}
	
	public void setEnabled(boolean enabled) {
		Widget w = getWidget();
		if (w instanceof HasEnabled) {
			((HasEnabled) w).setEnabled(enabled);
		}
	}
	
	public boolean isEnabled() {
		Widget w = getWidget();
		if (w instanceof HasEnabled) {
			return ((HasEnabled) w).isEnabled();
		}
		return true;
	}

	public FieldBinder<T> setWidth(String width) {
		widget.setWidth(width);
		return this;
	}
	
	public FieldBinder<T> addStyle(String style) {
		widget.addStyleName(style);
		return this;
	}
}
