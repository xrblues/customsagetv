package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.media.GWTViewCategories;
import org.jdna.bmt.web.client.ui.util.SideMenuItem;
import org.jdna.bmt.web.client.ui.util.SideMenuPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SourcesPanel extends Composite implements HasViews, HasViewCategories {
    private VerticalPanel panel = new VerticalPanel();
    private SideMenuPanel views = new SideMenuPanel(Application.labels().views());

    private BrowsePanel           controller           = null;
	private ArrayList<GWTView> viewCategories;
    
    public SourcesPanel(BrowsePanel controller) {
        this.controller = controller;
        panel.setHeight("100%");
        panel.setWidth("100%");
        views.setWidth("100%");
        panel.add(views);
        panel.setCellWidth(views, "100%");
        initWidget(panel);

        // Get the views...
        controller.getViews(null, this);
        controller.getViewCategories(this);
    }

    private void updateInfo(final GWTViewCategories cats) {
        if (cats==null || cats.getViews().size()==0) return;
        
        views.clearItems();
        for (GWTView v : cats.getViews()) {
            final GWTView finalView = v;
            
            SideMenuItem item = new SideMenuItem(v.getLabel(), null, new ClickHandler() {
                public void onClick(ClickEvent event) {
                    controller.getView(finalView);
                }
            });
            views.addItem(item);
        }
    }

	@Override
	public void setViewCategories(ArrayList<GWTView> cats) {
		this.viewCategories = cats;
	}

	@Override
	public void setViews(GWTViewCategories result) {
        if (result.getViews().size() == 0) {
            Application.fireErrorEvent(Application.messages().factoryNotConfigured(result.getLabel()));
            return;
        }

        updateInfo(result);
	}
}
