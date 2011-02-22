package org.jdna.bmt.web.client.ui.util;

import org.jdna.bmt.web.client.event.EventBus;
import org.jdna.bmt.web.client.event.WaitingEvent;
import org.jdna.bmt.web.client.event.WaitingHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SideMenuPanel extends Composite implements ClickHandler, WaitingHandler {
    private VerticalPanel panel = new VerticalPanel();
    private HorizontalPanel labelPanel = new HorizontalPanel();
    private VerticalPanel items = new VerticalPanel();
    private Label label = new Label();
    private HandlerRegistration handler;
    
    public SideMenuPanel(String label) {
        this.label.setWordWrap(false);
        this.label.setText(label);
        this.label.setStyleName("SideMenuPanel-LabelText");
        labelPanel.add(this.label);
        labelPanel.setWidth("100%");
        labelPanel.setStyleName("SideMenuPanel-Label");
        
        panel.setStyleName("SideMenuPanel");
        panel.add(labelPanel);
        panel.setWidth("100%");
        
        items.setWidth("100%");
        panel.add(items);
        
        initWidget(panel);
    }
    
    public void addItem(SideMenuItem item) {
        items.add(item);
        item.addClickHandler(this);
    }
    
    public HasWidgets getItems() {
    	return items;
    }
    
    public void clearItems() {
        items.clear();
    }

    public void onClick(ClickEvent event) {
        for (Widget w : items) {
            w.removeStyleName("SideMenuItem-selected");
            if (w instanceof SideMenuItem) {
                ((SideMenuItem) w).setBusy(false);
            }
        }
        ((Widget)event.getSource()).addStyleName("SideMenuItem-selected");
        ((SideMenuItem)event.getSource()).setBusy(true);
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        handler = EventBus.getHandlerManager().addHandler(WaitingEvent.TYPE, this);
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onDetach()
     */
    @Override
    protected void onDetach() {
        super.onDetach();
        handler.removeHandler();
    }

    public void onWaiting(WaitingEvent event) {
        // ideally, it should check the id, for for now, just cancel on any finished waiting event
        if (!event.isWaiting()) {
            for (Widget w : items) {
                if (w instanceof SideMenuItem) {
                    ((SideMenuItem) w).setBusy(false);
                }
            }
        }
    }
}
