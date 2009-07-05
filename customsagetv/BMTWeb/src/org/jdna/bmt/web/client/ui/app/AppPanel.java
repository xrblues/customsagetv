package org.jdna.bmt.web.client.ui.app;

import org.jdna.bmt.web.client.ui.browser.MediaEditor;
import org.jdna.bmt.web.client.ui.browser.MediaResult;
import org.jdna.bmt.web.client.ui.browser.ScanOptionsPanel;
import org.jdna.bmt.web.client.ui.prefs.PreferencesPanel;
import org.jdna.bmt.web.client.ui.status.StatusPanel;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AppPanel extends Composite {
    private DockPanel dp = new DockPanel();
    private Widget curPanel = null;
    
    public AppPanel() {
        dp.setWidth("100%");
        dp.setHeight("100%");
        
        Hyperlink status = new Hyperlink("Status", "status");
        status.addClickHandler(new ClickHandler(){
            public void onClick(ClickEvent event) {
                setStatusPanel();
            }
        }) ;
        
        Hyperlink configure = new Hyperlink("Configure", "configure");
        configure.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setConfigurePanel();
            }
        });

        // TODO
        Hyperlink scan = new Hyperlink("Scan", "scan");
        scan.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setScanPanel();
            }
        });

        Hyperlink refresh = new Hyperlink("Refresh Library", "refresh");
        refresh.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setRefreshPanel();
            }
        });

        Grid header = new Grid(1,2);
        header.setWidth("100%");
        header.addStyleName("AppPanel-Header");

        Label l = new Label("Metadata Tools");
        l.addStyleName("AppPanel-Title");
        header.setWidget(0, 0, l);
        header.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(10);
        hp.add(status);
        hp.add(configure);
        hp.add(scan);
        hp.add(refresh);
        
        header.setWidget(0,1,hp);
        header.getCellFormatter().setHorizontalAlignment(0,1,HasHorizontalAlignment.ALIGN_RIGHT);
        
        dp.add(header, DockPanel.NORTH);
        dp.setCellHorizontalAlignment(header, HasHorizontalAlignment.ALIGN_RIGHT);
        
        initWidget(dp);
        
        setStatusPanel();
    }
    
    protected void setScanPanel() {
        ScanOptionsPanel.showDialog(new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                Log.error("Service return an error", caught);
            }
            public void onSuccess(String result) {
                if (result==null) {
                    Log.error("No Results");
                } else {
                    setPanel(new MediaEditor(result));
                }
            }
        });
    }

    protected void setRefreshPanel() {
        RefreshOptionsPanel.showDialog();
    }

    protected void setConfigurePanel() {
        setPanel(new PreferencesPanel());
    }

    private void setStatusPanel() {
        setPanel(new StatusPanel());
    }

    private void setPanel(Widget panel) {
        if (curPanel!=null) {
            dp.remove(curPanel);
        }
        dp.add(panel, DockPanel.CENTER);
        dp.setCellHeight(panel, "100%");
        curPanel = panel;
    }
}