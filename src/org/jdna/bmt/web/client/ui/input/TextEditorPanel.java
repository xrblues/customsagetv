package org.jdna.bmt.web.client.ui.input;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Override the Constructor, call super(HasValue) and then proceed to create your widget and call initWidget()
 * 
 * overrride setFocus() if you want to focus a widget on the panel
 * 
 * @author seans
 *
 */
public abstract class TextEditorPanel extends Composite implements HasValue<String>, Focusable {
    private HasValue<String> value = null;
    private Focusable focus = null;
    private String preferredWidth = null;
    private String preferredHeight= null;
    
    public TextEditorPanel(HasValue<String> valueWidget) {
        this.value = valueWidget;
        if (value instanceof Focusable) {
            focus = ((Focusable)value);
        }
    }
    
    protected HasValue<String> getHasValueWidget() {
        return value;
    }
    
    public String getValue() {
        return value.getValue();
    }

    public void setValue(String v) {
        value.setValue(v);
    }

    public void setValue(String v, boolean fireEvents) {
        value.setValue(v, fireEvents);
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return value.addValueChangeHandler(handler);
    }
    
    public int getTabIndex() {
        return focus==null?0:focus.getTabIndex();
    }

    public void setAccessKey(char key) {
        if (focus!=null) focus.setAccessKey(key);
    }

    public void setFocus(boolean focused) {
        if (focus!=null) focus.setFocus(focused);
    }

    public void setTabIndex(int index) {
        if (focus!=null) focus.setTabIndex(index);
    }

    protected String getPreferredHeight() {
        return preferredHeight;
    }

    protected void setPreferredHeight(String preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    protected void setPreferredWidth(String preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    protected String getPreferredWidth() {
        return preferredWidth;
    }

}
