package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HorizontalButtonBar extends Composite {
    private HorizontalPanel panel = new HorizontalPanel();
    private HorizontalPanel buttons = new HorizontalPanel();
    
    public HorizontalButtonBar() {
        panel.setWidth("100%");
        panel.setStyleName("HorizontalButtonBar");
        panel.setSpacing(5);
        
        buttons.setSpacing(6);
        panel.add(buttons);
        panel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_LEFT);
        panel.setCellVerticalAlignment(buttons, HasVerticalAlignment.ALIGN_MIDDLE);
        initWidget(panel);
    }
    
    public void add(Widget w) {
        buttons.add(w);
    }
    
    public void clear() {
        buttons.clear();
    }

    public void basicStyle() {
        panel.removeStyleName("HorizontalButtonBar");
        panel.setSpacing(1);
        buttons.setSpacing(3);
    }
}
