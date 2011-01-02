package org.jdna.bmt.web.client.ui.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.Version;
import org.jdna.bmt.web.client.event.Notification;
import org.jdna.bmt.web.client.event.NotificationEvent;
import org.jdna.bmt.web.client.event.NotificationEvent.MessageType;
import org.jdna.bmt.web.client.event.NotificationEventHandler;
import org.jdna.bmt.web.client.ui.BatchOperation;
import org.jdna.bmt.web.client.ui.BatchOperations;
import org.jdna.bmt.web.client.ui.browser.BrowsePanel;
import org.jdna.bmt.web.client.ui.browser2.BrowserPanel;
import org.jdna.bmt.web.client.ui.debug.BackupPanel;
import org.jdna.bmt.web.client.ui.prefs.PreferencesPanel;
import org.jdna.bmt.web.client.ui.prefs.PreferencesService;
import org.jdna.bmt.web.client.ui.prefs.PreferencesServiceAsync;
import org.jdna.bmt.web.client.ui.status.StatusPanel;
import org.jdna.bmt.web.client.ui.toast.Toaster;
import org.jdna.bmt.web.client.ui.util.CommandItem;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AppPanel extends Composite implements ValueChangeHandler<String>, NotificationEventHandler {
    public static AppPanel INSTANCE = null;
   
    private VerticalPanel vp = new VerticalPanel();
    private Widget curPanel = null;
    
    final GlobalServiceAsync global = GWT.create(GlobalService.class);

    Toaster toaster = new Toaster();
    
    public AppPanel() {
        INSTANCE = this;
        vp.setWidth("100%");
        vp.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        
        Hyperlink status = new Hyperlink(Application.labels().status(), "status");
        status.addStyleName("App-Status");
        
        Hyperlink configure = new Hyperlink(Application.labels().configure(), "configure");
        configure.setStyleName("App-Configure");

        Hyperlink browse = new Hyperlink(Application.labels().browse(), "browsing/source:tv");
        browse.addStyleName("App-Browse");

        Label refresh = new Label(Application.labels().refreshLibrary());
        refresh.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                SageAPI.refreshLibrary(false, new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        Application.fireErrorEvent(Application.messages().failedToStartScan(), caught);
                    }

                    public void onSuccess(String result) {
                        Application.fireNotification(result);
                    }
                });
            }
        });
        refresh.addStyleName("App-Refresh");
        refresh.addStyleName("clickable");

        
        Label help = new Label(Application.labels().help());
        help.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	showHelp();
            }

        });
        help.addStyleName("App-Help");
        help.addStyleName("clickable");

        final Label toolMenu = new Label(Application.labels().toolMenu());
        toolMenu.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showToolsMenu(toolMenu);
            }

            private void showToolsMenu(Widget offset) {
                final PopupPanel pp = new PopupPanel();
                pp.setAutoHideEnabled(true);
                VerticalPanel vp = new VerticalPanel();
                vp.add(new CommandItem(null, "Create Support Request", new Command() {
                    public void execute() {
                        pp.hide();
                        showSupportRequestDialog();
                    }
                }));
                
                vp.add(new CommandItem(null, "Manage Backups", new Command() {
                    public void execute() {
                        pp.hide();
                        History.newItem("backup");
                    }
                }));

                vp.add(new CommandItem(null, "Fix Custom Metadata Fields", new Command() {
                    public void execute() {
                    	fixCustomMetadataFields();
                    }
                }));
                
                for (BatchOperation op: BatchOperations.getInstance().getBatchOperations()) {
                	final BatchOperation opFinal = op;
                    vp.add(new CommandItem(null, op.getLabel(), new Command() {
                        public void execute() {
                            pp.hide();
                            Application.runBatchOperation(null, opFinal);
                        }
                    }));
                }
                
                pp.setWidget(vp);
                pp.showRelativeTo(offset);
            }
        });
        toolMenu.addStyleName("App-Toolmenu");
        toolMenu.addStyleName("clickable");

        Grid header = new Grid(1,2);
        header.setWidth("100%");
        header.addStyleName("AppPanel-Header");

        header.setWidget(0, 0, new HeaderTitleSection());
        header.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(10);
        hp.add(status);
        hp.add(configure);
        hp.add(browse);
        hp.add(toolMenu);
        hp.add(refresh);
        hp.add(help);
        
        header.setWidget(0,1,hp);
        header.getCellFormatter().setHorizontalAlignment(0,1,HasHorizontalAlignment.ALIGN_RIGHT);
        
        vp.add(header);
        vp.setCellHorizontalAlignment(header, HasHorizontalAlignment.ALIGN_RIGHT);
        vp.setCellVerticalAlignment(header, HasVerticalAlignment.ALIGN_MIDDLE);
        
        initWidget(vp);
        
        History.addValueChangeHandler(this);

        String initToken = History.getToken();
        if (initToken.length() == 0) {
            Log.debug("Setting status into the history state");
          History.newItem("status");
        } else {
            Log.debug("Using init history: " + initToken);
        }

        Application.events().addHandler(NotificationEvent.TYPE, this);
        
        History.fireCurrentHistoryState();
        
        final Timer t = new Timer() {
			@Override
			public void run() {
				global.getNotices(new AsyncCallback<ArrayList<Notification>>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(ArrayList<Notification> result) {
						for (Notification ne : result) {
							Application.events().fireEvent(new NotificationEvent(ne));
						}
					}
				});
			}
		};
		t.scheduleRepeating(1000);

		global.getLastVersion(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String lastVersion) {
				if (lastVersion==null || !lastVersion.equals(Version.VERSION)) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							DialogBox d = Dialogs.showAsDialog("Batch Metadata Tools Updated", new AboutDialog());
							d.addCloseHandler(new CloseHandler<PopupPanel>() {
								@Override
								public void onClose(CloseEvent<PopupPanel> event) {
								}
							});
						}
					});
				}
			}
		});
    }

    private void showSupportRequestDialog() {
        DataDialog.showDialog(new SupportDialog());
    }
    
    protected void setBrowsePanel(Map<String,String> params) {
    	if (Application.BMT5) {
    		setPanel(new BrowserPanel());
    	} else {
	        if (!(curPanel instanceof BrowsePanel)) {
	            setPanel(new BrowsePanel()); 
	        }
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

    private void setBackupPanel() {
        setPanel(new BackupPanel());
    }
    
	private void showHelp() {
		Dialogs.showAsDialog("Help", new HelpDialog());
	}

    private void setPanel(Widget panel) {
        if (curPanel!=null) {
            vp.remove(curPanel);
        }
        vp.add(panel);
        vp.setCellHeight(panel, "100%");
        curPanel = panel;
    }

        
    public void onValueChange(ValueChangeEvent<String> event) {
        Map<String,String> params = parseHistoryTokens(event.getValue());
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
        } else if ("support".equals(section)) {
            showSupportRequestDialog();
        } else if ("backup".equals(section)) {
            setBackupPanel();
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

    public void onNotification(org.jdna.bmt.web.client.event.NotificationEvent event) {
        if (event.getMessageType()==MessageType.ERROR) {
            Log.error(event.getMessage(), event.getException());
        } else {
            Log.debug(event.getMessage());
        }
        
        if (event.getMessageType()==MessageType.ERROR) {
            toaster.addErrorMessage(event.getMessage());
        } else if (event.getMessageType()==MessageType.WARN) {
            toaster.addWarnMessage(event.getMessage());
        } else {
        	toaster.addMessage(event.getMessage());
        }
    }

    private void fixCustomMetadataFields() {
    	 PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);
    	 preferencesService.refreshCustomMetadataFields(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to refresh fields", caught);
			}

			@Override
			public void onSuccess(Void result) {
				Application.fireNotification("The custom metadata fields have been reset.  SageTV will need to be restarted for the changes to take effect.");
			}
		});
	}
}
