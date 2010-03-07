package org.jdna.bmt.web.client.ui.app;

import java.util.HashMap;
import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.browser.BrowsePanel;
import org.jdna.bmt.web.client.ui.browser.MetadataService;
import org.jdna.bmt.web.client.ui.browser.MetadataServiceAsync;
import org.jdna.bmt.web.client.ui.prefs.PreferencesPanel;
import org.jdna.bmt.web.client.ui.status.StatusPanel;
import org.jdna.bmt.web.client.ui.util.CommandItem;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AppPanel extends Composite implements ResizeHandler, HasResizeHandlers, ValueChangeHandler<String>, ErrorEventHandler {
    public static AppPanel INSTANCE = null;
    private final MetadataServiceAsync browserService = GWT.create(MetadataService.class);
    
    private DockPanel dp = new DockPanel();
    private Widget curPanel = null;
    
    public AppPanel() {
        INSTANCE = this;
        dp.setWidth("100%");
        dp.setHeight("100%");
        
        Hyperlink status = new Hyperlink(Application.labels().status(), "status");
        status.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setStatusPanel();
            }
        });
        status.addStyleName("App-Status");
        
        Hyperlink configure = new Hyperlink(Application.labels().configure(), "configure");
        configure.setStyleName("App-Configure");

        Hyperlink browse = new Hyperlink(Application.labels().browse(), "browsing/source:tv");
        browse.addStyleName("App-Browse");

        Hyperlink refresh = new Hyperlink(Application.labels().refreshLibrary(), "refresh");
        refresh.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setRefreshPanel();
            }
        });
        refresh.addStyleName("App-Refresh");

        final Hyperlink toolMenu = new Hyperlink(Application.labels().toolMenu(), "toolmenu");
        toolMenu.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showToolsMenu(toolMenu);
            }

            private void showToolsMenu(Widget offset) {
                PopupPanel pp = new PopupPanel();
                pp.setAutoHideEnabled(true);
                pp.setAnimationEnabled(true);
                VerticalPanel vp = new VerticalPanel();
                //vp.add(new CommandItem(null, "Find/Remove Property Files", null));
                vp.add(new CommandItem(null, "Create Support Request", new Command() {
                    public void execute() {
                        showSupportRequestDialog();
                    }
                }));
                vp.add(new CommandItem(null, "Clean .properties", new Command() {
                    public void execute() {
                        showCleanPropertiesDialog();
                    }
                }));
                pp.setWidget(vp);
                pp.showRelativeTo(offset);
            }
        });
        toolMenu.addStyleName("App-Toolmenu");

        Grid header = new Grid(1,2);
        header.setWidth("100%");
        header.addStyleName("AppPanel-Header");

        Label l = new Label(Application.labels().appTitle());
        l.addStyleName("AppPanel-Title");
        header.setWidget(0, 0, l);
        header.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(10);
        hp.add(status);
        hp.add(configure);
        hp.add(browse);
        hp.add(toolMenu);
        hp.add(refresh);
        
        header.setWidget(0,1,hp);
        header.getCellFormatter().setHorizontalAlignment(0,1,HasHorizontalAlignment.ALIGN_RIGHT);
        
        dp.add(header, DockPanel.NORTH);
        dp.setCellHorizontalAlignment(header, HasHorizontalAlignment.ALIGN_RIGHT);
        
        initWidget(dp);
        
        History.addValueChangeHandler(this);

        String initToken = History.getToken();
        if (initToken.length() == 0) {
            Log.debug("Setting status into the history state");
          History.newItem("status");
        } else {
            Log.debug("Using init history: " + initToken);
        }

        Application.events().addHandler(ErrorEvent.TYPE, this);
        
        History.fireCurrentHistoryState();
        
        //setStatusPanel();
        
        Window.addResizeHandler(this);
        Window.enableScrolling(false);
        
    }

    private void showSupportRequestDialog() {
        DataDialog.showDialog(new SupportDialog());
    }
    
    private void showCleanPropertiesDialog() {
        DataDialog.showDialog(new CleanPropertiesDialog());
    }
    
    protected void setBrowsePanel(Map<String,String> params) {
        if (!(curPanel instanceof BrowsePanel)) {
            setPanel(new BrowsePanel()); 
        }
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

    public void onValueChange(ValueChangeEvent<String> event) {
        Map<String,String> params = parseHistoryTokens(event.getValue());
        for (Map.Entry<String, String> me : params.entrySet()) {
            System.out.println("param: " + me.getKey() + "; value: " + me.getValue());
        }
        String section = params.get("section");
        if(section==null) section=event.getValue();
        if (section==null || section.length()==0) section="status";
        
        Log.debug("Setting Section: " + section);
        if ("status".equals(section)) {
            setStatusPanel();
        } else if ("configure".equals(section)) {
            setConfigurePanel();
        } else if ("browsing".equals(section)) {
            setBrowsePanel(params);
        //} else if ("scan".equals(section)) {
        //    setScanPanel();
        //} else if ("refresh".equals(section)) {
        //    setRefreshPanel();
        //} else {
        //    setStatusPanel();
        }
    }
    
    /**
     * History Tokens are like,
     * section/name:value/name:value
     * @return
     */
    private Map<String,String> parseHistoryTokens(String in) {
        Map<String, String> params = new HashMap<String, String>();
        
        if (in!=null) {
            String parts[] = in.split("/");
            if (parts.length>=1) {
                params.put("section", parts[0]);
            }
            if (parts.length>1) {
                for (int i=1;i<parts.length;i++) {
                    String nvp[] = parts[i].split(":");
                    params.put(nvp[0], (nvp.length>1)?nvp[1]:null);
                }
            }
        }
        
        return params;
    }

    public void onError(ErrorEvent event) {
        Log.error(event.getMessage(), event.getException());
    }
}
