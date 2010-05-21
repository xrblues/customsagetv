package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TitlePanel extends Composite {
    private VerticalPanel vp = new VerticalPanel();
    
    public TitlePanel(String title) {
        this(new Label(title));
    }

    public TitlePanel(Widget title) {
        vp.addStyleName("TitlePanel");
        vp.setSpacing(5);
        vp.setWidth("100%");
        vp.add(title);
        title.addStyleName("TitlePanel-Title");
        initWidget(vp);
    }
    
    public void setContent(Widget w) {
        vp.add(w);
    }
}
