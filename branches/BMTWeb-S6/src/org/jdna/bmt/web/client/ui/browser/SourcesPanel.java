package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTFactoryInfo;
import org.jdna.bmt.web.client.media.GWTFactoryInfo.SourceType;
import org.jdna.bmt.web.client.ui.util.SideMenuItem;
import org.jdna.bmt.web.client.ui.util.SideMenuPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SourcesPanel extends Composite implements FactoriesReplyHandler {
    private VerticalPanel panel = new VerticalPanel();
    private SideMenuPanel       views            = new SideMenuPanel(Application.labels().views());

    private HandlerRegistration factoriesHandler = null;
    private BrowserView           folder           = null;
    
    public SourcesPanel(BrowserView folder) {
        this.folder = folder;
        panel.setHeight("100%");
        panel.setWidth("100%");
        views.setWidth("100%");
        panel.add(views);
        panel.setCellWidth(views, "100%");
        initWidget(panel);
    }

    @Override
    protected void onAttach() {
        factoriesHandler = Application.events().addHandler(FactoriesReplyEvent.TYPE, this);
        
        // Get the views...
        BrowsingServicesManager.getInstance().getFactoryInfo(GWTFactoryInfo.SourceType.View);

        super.onAttach();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        factoriesHandler.removeHandler();
    }

    public void onFactoriesReply(FactoriesReplyEvent event) {
        GWTFactoryInfo factories[] = event.getFactoryInf0();
        if (event.getSourceType() == SourceType.View && (factories == null || factories.length == 0)) {
            Application.fireErrorEvent(Application.messages().factoryNotConfigured(event.getSourceType().name()));
            return;
        }

        updateInfo(event.getSourceType(), factories);
    }

    private void updateInfo(final GWTFactoryInfo.SourceType sourceType, GWTFactoryInfo info[]) {
        if (info==null || info.length==0) return;
        
        if (sourceType == SourceType.View) {
            views.clearItems();
            for (GWTFactoryInfo f : info) {
                final GWTFactoryInfo finalInfo = f;
                
                SideMenuItem item = new SideMenuItem(f.getLabel(), null, new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        BrowsingServicesManager.getInstance().getFolderForSource(finalInfo, folder.getFolder());
                    }
                });
                views.addItem(item);
            }
        }
    }
}
