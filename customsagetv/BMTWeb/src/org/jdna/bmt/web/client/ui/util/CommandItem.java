package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CommandItem extends Composite implements HasClickHandlers, MouseOutHandler, MouseOverHandler, ClickHandler {
    private VerticalPanel container = new VerticalPanel();
    private DockPanel layout = new DockPanel();
    private Command action = null;
    private Widget icon = null;
    private Widget label = null;
    private String hoverStyleName = "CommandItem-Hover";
    
    public CommandItem(String icon, String label, Command action) {
        this((icon!=null?new Image(icon):null), (label!=null?new Label(label):null), action);
    }
    
    public CommandItem(Widget icon, Widget label, Command action) {
        initWidget(container);
        container.add(layout);
        container.setWidth("100%");
        container.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        layout.setHeight("20px");
        this.icon=icon;
        this.label=label;
        this.action=action;
        
        if (icon!=null) {
            layout.add(icon, DockPanel.WEST);
            layout.setCellHorizontalAlignment(icon, HasHorizontalAlignment.ALIGN_CENTER);
            layout.setCellVerticalAlignment(icon, HasVerticalAlignment.ALIGN_MIDDLE);
            icon.addStyleName("CommandItem-Icon");
        }
        
        if (label!=null) {
            layout.add(label, DockPanel.CENTER);
            layout.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_CENTER);
            layout.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
        }
        
        addStyleName("CommandItem");
        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
        addDomHandler(this, ClickEvent.getType());
    }
    
    public void setHoveStyleName(String name) {
        this.hoverStyleName=name;
    }

    /**
     * @return the hoverStyleName
     */
    public String getHoverStyleName() {
        return hoverStyleName;
    }

    /**
     * @param hoverStyleName the hoverStyleName to set
     */
    public void setHoverStyleName(String hoverStyleName) {
        this.hoverStyleName = hoverStyleName;
    }

    /**
     * @return the icon
     */
    public Widget getIcon() {
        return icon;
    }

    /**
     * @return the label
     */
    public Widget getLabel() {
        return label;
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public void onMouseOut(MouseOutEvent event) {
        if (hoverStyleName!=null) {
            removeStyleName(hoverStyleName);
        }
    }

    public void onMouseOver(MouseOverEvent event) {
        if (hoverStyleName!=null) {
            addStyleName(hoverStyleName);
        }
    }

    public void onClick(ClickEvent event) {
        if (action!=null) {
            action.execute();
            event.stopPropagation();
        }
    }
}
