package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WaitingPanel extends Composite {
    public WaitingPanel() {
        Image img = new Image("waiting.gif");
        
        VerticalPanel p = new VerticalPanel();
        p.setWidth("100%");
        p.setHeight("100%");
        p.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        p.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        p.add(img);
        
        initWidget(p);
    }
}
