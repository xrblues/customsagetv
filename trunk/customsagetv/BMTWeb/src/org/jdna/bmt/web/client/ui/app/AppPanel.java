package org.jdna.bmt.web.client.ui.app;

import org.jdna.bmt.web.client.ui.browser.MediaEditor;
import org.jdna.bmt.web.client.ui.browser.ScanOptionsPanel;
import org.jdna.bmt.web.client.ui.prefs.PreferencesPanel;
import org.jdna.bmt.web.client.ui.status.StatusPanel;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AppPanel extends Composite implements ResizeHandler, HasResizeHandlers {
    public static AppPanel INSTANCE = null;
    
    private DockPanel dp = new DockPanel();
    private Widget curPanel = null;
    
    public AppPanel() {
        INSTANCE = this;
        dp.setWidth("100%");
        dp.setHeight("100%");
        
        Hyperlink status = new Hyperlink("Status", "status");
        status.addClickHandler(new ClickHandler(){
            public void onClick(ClickEvent event) {
                setStatusPanel();
            }
        }) ;
        status.addStyleName("App-Status");
        
        Hyperlink configure = new Hyperlink("Configure", "configure");
        configure.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setConfigurePanel();
            }
        });
        configure.setStyleName("App-Configure");

        // TODO
        Hyperlink scan = new Hyperlink("Scan", "scan");
        scan.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setScanPanel();
            }
        });
        scan.addStyleName("App-Scan");

        Hyperlink refresh = new Hyperlink("Refresh Library", "refresh");
        refresh.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setRefreshPanel();
            }
        });
        refresh.addStyleName("App-Refresh");

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
        
        Window.addResizeHandler(this);
        Window.enableScrolling(false);
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

    public void onResize(ResizeEvent event) {
        System.out.println("AppPanel(): Resize Window: " + event.getWidth() + ";" + event.getHeight());
        adjustSize(event.getWidth(), event.getHeight());
        if (curPanel instanceof ResizeHandler) {
            ((ResizeHandler) curPanel).onResize(event);
        }
    }

    private void adjustSize(int width, int height) {
        setPixelSize(width, height);
    }
    
    public static void adjustWindowSize() {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                ResizeEvent evt = new ResizeEvent(Window.getClientWidth(), Window.getClientHeight()) {
                };
                INSTANCE.onResize(evt);
            }
        });
    }

    public HandlerRegistration addResizeHandler(ResizeHandler handler) {
        return addHandler(handler, ResizeEvent.getType());
    }
}
