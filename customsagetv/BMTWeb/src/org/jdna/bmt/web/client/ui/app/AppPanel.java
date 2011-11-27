package org.jdna.bmt.web.client.ui.app;

import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.event.Notification;
import org.jdna.bmt.web.client.event.NotificationEvent;
import org.jdna.bmt.web.client.event.NotificationEvent.MessageType;
import org.jdna.bmt.web.client.event.NotificationEventHandler;
import org.jdna.bmt.web.client.ui.BatchOperation;
import org.jdna.bmt.web.client.ui.BatchOperations;
import org.jdna.bmt.web.client.ui.HTMLTemplates;
import org.jdna.bmt.web.client.ui.browser.BrowsePanel;
import org.jdna.bmt.web.client.ui.debug.BackupPanel;
import org.jdna.bmt.web.client.ui.portal.Portal;
import org.jdna.bmt.web.client.ui.prefs.ConfigError;
import org.jdna.bmt.web.client.ui.prefs.PreferencesPanel;
import org.jdna.bmt.web.client.ui.prefs.PreferencesService;
import org.jdna.bmt.web.client.ui.prefs.PreferencesServiceAsync;
import org.jdna.bmt.web.client.ui.status.StatusPanel;
import org.jdna.bmt.web.client.ui.toast.Toaster;
import org.jdna.bmt.web.client.ui.util.AsyncServiceReply;
import org.jdna.bmt.web.client.ui.util.CommandItem;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.MessageDialog;
import org.jdna.bmt.web.client.ui.xmleditor.XMLEditorWindow;
import org.jdna.bmt.web.client.util.Log;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
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
    private final PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);
    
    public static AppPanel INSTANCE = null;
   
    private VerticalPanel vp = new VerticalPanel();
    private Widget curPanel = null;
    
    final GlobalServiceAsync global = GWT.create(GlobalService.class);

    Toaster toaster = new Toaster();

    private static final HTMLTemplates templates = GWT.create(HTMLTemplates.class);
    
    public AppPanel() {
        INSTANCE = this;
        vp.setWidth("100%");
        vp.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

        //Hyperlink portal = new Hyperlink(Application.labels().status(), "portal");
        //portal.addStyleName("App-Portal");
        
        Hyperlink status = new Hyperlink(Application.labels().status(), "status");
        status.addStyleName("App-Status");
        
        Hyperlink configure = new Hyperlink(Application.labels().configure(), "configure");
        configure.setStyleName("App-Configure");

        Hyperlink browse = new Hyperlink(Application.labels().browse(), "browse");
        browse.addStyleName("App-Browse");

        final Label refresh = new Label(Application.labels().refreshLibrary());
        refresh.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                final PopupPanel pp = new PopupPanel();
                pp.setAutoHideEnabled(true);
                VerticalPanel vp = new VerticalPanel();
                vp.add(new CommandItem(null, "Notify SageTV to Look for new Media", new Command() {
                    public void execute() {
                        pp.hide();
                        SageAPI.refreshLibrary(false, new AsyncCallback<String>() {
                            public void onFailure(Throwable caught) {
                                Application.fireErrorEvent(Application.messages().failedToStartScan(), caught);
                            }

                            public void onSuccess(String result) {
                                Application.fireNotification(result);
                            }
                        });
                    }
                }));
                
                vp.add(new CommandItem(null, "Refresh Configurations", new Command() {
                    public void execute() {
                        pp.hide();
                        refreshConfigurations();
                    }
                }));

                vp.add(new CommandItem(null, "Clear Fanart Caches", new Command() {
                    public void execute() {
                        pp.hide();
                    	clearFanartCaches();
                    }
                }));
                
                pp.setWidget(vp);
                pp.showRelativeTo(refresh);
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
                vp.add(new CommandItem(null, "Edit Xml Configurations (VFS, Menus, etc)", new Command() {
                    public void execute() {
                        pp.hide();
                        History.newItem("xmleditor");
                    }
                }));

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
        //hp.add(portal);
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

		global.showAboutDialog(new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Boolean showDialog) {
				if (showDialog!=null && showDialog) {
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
    
	private void refreshConfigurations() {
		Dialogs.showWaiting("Refreshing configurations...");
	    preferencesService.refreshConfigurations(new AsyncServiceReply<ArrayList<ConfigError>>() {
			@Override
			public void onOK(ArrayList<ConfigError> result) {
				if (result.size()==0) {
					Application.fireNotification("Configurations are refreshed");
				} else {
					ArrayList<String> al = new ArrayList<String>();
					for (ConfigError ce : result) {
						SafeHtml msg = templates.createConfigError(ce.file, ce.message, ce.line, ce.column);
						al.add(msg.asString());
					}
					
					MessageDialog dlg = new MessageDialog("Some configurations have errors", al);
					dlg.center();
					dlg.show();
				}
			}
		});
	}

    private void showSupportRequestDialog() {
        DataDialog.showDialog(new SupportDialog());
    }
    
    protected void setBrowsePanel(List<String> params) {
        if (!(curPanel instanceof BrowsePanel)) {
            setPanel(new BrowsePanel(params)); 
        } else {
        	((BrowsePanel)curPanel).setParams(params);
        }
    }

    protected void setRefreshPanel() {
        RefreshOptionsPanel.showDialog();
    }

    protected void setConfigurePanel() {
        setPanel(new PreferencesPanel());
    }

    protected void setPortalPanel() {
        setPanel(new Portal());
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
    	String section="status";
    	List<String> parts = new ArrayList<String>();
    	if (event.getValue()!=null) {
    		String arr[] = event.getValue().split("/");
    		section=arr[0];
    		if (arr.length>1) {
    			for (int i=1;i<arr.length;i++) {
    				parts.add(arr[i]);
    			}
    		}
    	}
        
        Log.debug("Setting Section: " + event.getValue());
        if ("status".equals(section) || StringUtils.isEmpty(event.getValue())) {
            setStatusPanel();
        } else if ("configure".equals(section)) {
            setConfigurePanel();
        } else if ("portal".equals(section)) {
            setPortalPanel();
        } else if ("browse".equals(section)) {
            setBrowsePanel(parts);
        } else if ("support".equals(section)) {
            showSupportRequestDialog();
        } else if ("backup".equals(section)) {
            setBackupPanel();
        } else if ("xmleditor".equals(section)) {
            showEditXml();
        } else {
        	//setStatusPanel();
        }
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
    
	private void clearFanartCaches() {
		preferencesService.refreshConfiguration(PreferencesService.REFRESH_IMAGE_CACHE, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Void result) {
				Application.fireNotification("Fanart caches are cleared");
			}
		});
	}
	
	private void showEditXml() {
		setPanel(new XMLEditorWindow());
	}
}
