package org.jdna.bmt.web.client.ui.input;


import org.jdna.bmt.web.client.util.Property;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;

public class InputFactory {
    public static TextBox createIntTextbox() {
        TextBox tb = new TextBox();
        tb.setWidth("100px");
        tb.setTextAlignment(TextBoxBase.ALIGN_RIGHT);
        return tb;
    }
    
    public static TextBox createLongTextbox() {
        TextBox tb = new TextBox();
        tb.setWidth("150px");
        tb.setTextAlignment(TextBoxBase.ALIGN_RIGHT);
        return tb;
    }

    public static TextBox createFloatTextbox() {
        TextBox tb = new TextBox();
        tb.setWidth("150px");
        tb.setTextAlignment(TextBoxBase.ALIGN_RIGHT);
        return tb;
    }
    
    public static TextBox bind(final TextBox textbox, final Property<String> model) {
        textbox.setValue(model.get());
        textbox.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                model.set(String.valueOf(event.getValue()));
            }
        });
        return textbox;
    }

    public static CheckBox bind(final CheckBox checkbox, final Property<String> model) {
        checkbox.setValue("true".equals(model.get()));
        checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                model.set(String.valueOf(event.getValue()));
            }
        });
        return checkbox;
    }
}
