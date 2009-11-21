package org.jdna.bmt.web.client.ui.input;


import java.util.List;

import org.jdna.bmt.web.client.util.Log;
import org.jdna.bmt.web.client.util.Property;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class InputBuilder<W, T> {
    
    public W widget = null;
    
    public InputBuilder(W w) {
        this.widget=w;
    }
    
    public static InputBuilder<TextBox, String> textbox() {
        return new InputBuilder<TextBox, String>(new TextBox());
    }

    public static InputBuilder<TextBox, String> textbox(TextBox tb) {
        return new InputBuilder<TextBox, String>(tb);
    }

    public static InputBuilder<CheckBox, Boolean> checkbox() {
        return new InputBuilder<CheckBox, Boolean>(new CheckBox());
    }

    public static InputBuilder<ListBox, String> combo(List<NVP<String>> nvp) {
        ListBox lb = new ListBox();
        for (NVP<String> p : nvp) {
            lb.addItem(p.getName(), p.getValue());
        }
        return new InputBuilder<ListBox, String>(lb);
    }

    public static InputBuilder<ListBox, String> combo(String values) {
        ListBox lb = new ListBox();
        if (!StringUtils.isEmpty(values)) {
            String p[] = values.split(",");
            for (int i=0;i<p.length;i++) {
                String s = p[i];
                lb.addItem(s, s);
            }
        }
        return new InputBuilder<ListBox, String>(lb);
    }

    public InputBuilder<W, T> bind(T propString) {
        return bind(new Property<T>(propString));
    }
    
    public InputBuilder<W, T> bind(final Property<T> prop) {
        if (widget instanceof HasValue) {
            HasValue<T> hasValue = (HasValue<T>)widget;
            hasValue.setValue(prop.get());
            hasValue.addValueChangeHandler(new ValueChangeHandler<T>() {
                public void onValueChange(ValueChangeEvent<T> event) {
                    prop.set(event.getValue());
                }
            });
        } else if (widget instanceof ListBox){
            ListBox lb = (ListBox) widget();
            
            if (lb.getItemCount()>0 && !StringUtils.isEmpty((String) prop.get())) {
                for (int i=0;i<lb.getItemCount();i++) {
                    if (lb.getValue(i).equals(prop.get())) {
                        lb.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            lb.addChangeHandler(new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    prop.set((T)((ListBox)widget).getValue(((ListBox)widget).getSelectedIndex()));
                }
            });
        } else {
            Log.error("Widget is not a HasValue widget! " + widget);
        }
        
        return this;
    }
    
    public InputBuilder<W, T> readonly() {
        if (widget instanceof FocusWidget) {
            ((FocusWidget)widget).setEnabled(false);
        }
        return this;
    }
    
    public W widget() {
        return widget;
    }
}
