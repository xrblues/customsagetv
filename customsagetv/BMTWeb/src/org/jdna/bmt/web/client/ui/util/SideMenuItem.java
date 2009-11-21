package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SideMenuItem extends Composite implements HasClickHandlers, MouseOutHandler, MouseOverHandler, ClickHandler {
    private Image waiting = new Image("waiting-sidemenu.gif");
    private HorizontalPanel    panel   = new HorizontalPanel();
    private ClickHandler onclick;
    private String label;

    public SideMenuItem(String label, String url, ClickHandler onclick) {
        this.label=label;
        this.onclick=onclick;
        
        panel.setStyleName("SideMenuItem");
        panel.setWidth("100%");

        // setup the icon, if any
        Widget w = null;
        if (url!=null) {
            w = new Image(url);
        } else {
            w = new Label("");
        }
        
        panel.add(w);
        panel.setCellHorizontalAlignment(w, HasHorizontalAlignment.ALIGN_CENTER);
        panel.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setCellWidth(w, "30px");
        
        
        // add the label
        Label l = new Label(this.label);
        l.setStyleName("SideMenuItem-Label");
        l.setWordWrap(false);
        panel.add(l);
        panel.setCellVerticalAlignment(l, HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setCellHorizontalAlignment(l, HasHorizontalAlignment.ALIGN_LEFT);
        panel.setHeight("30px");
        
        waiting.setVisible(false);
        panel.add(waiting);
        panel.setCellHorizontalAlignment(waiting, HasHorizontalAlignment.ALIGN_RIGHT);
        panel.setCellVerticalAlignment(waiting, HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setCellWidth(w, "20px");
        
        initWidget(panel);

        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
        addDomHandler(this, ClickEvent.getType());
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public void onMouseOut(MouseOutEvent event) {
        panel.removeStyleName("hover");
    }

    public void onMouseOver(MouseOverEvent event) {
        panel.addStyleName("hover");
    }

    public void onClick(ClickEvent event) {
        onclick.onClick(event);
    }
    
    public void setBusy(boolean busy) {
        waiting.setVisible(busy);
    }
}
