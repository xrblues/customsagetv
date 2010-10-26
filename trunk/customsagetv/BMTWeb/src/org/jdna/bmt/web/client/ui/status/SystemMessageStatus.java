package org.jdna.bmt.web.client.ui.status;

import java.util.Date;
import java.util.List;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.util.CommandItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SystemMessageStatus implements HasStatus {
    private final StatusServicesAsync statusServices = GWT.create(StatusServices.class);
    VerticalPanel panel = new VerticalPanel();
    HorizontalPanel actions = new HorizontalPanel();
    
    public SystemMessageStatus() {
        CommandItem cmd = new CommandItem(null, "Clear All", new Command() {
            public void execute() {
                statusServices.clearSystemMessages(new AsyncCallback<Void>() {
                    public void onFailure(Throwable caught) {
                        Application.fireErrorEvent("Unabled to clear system messages", null);
                    }

                    public void onSuccess(Void result) {
                        panel.clear();
                    }
                });
            }
        });
        actions.add(cmd);
    }
    
    public String getHelp() {
        return "SageTV System Mesages";
    }

    public String getStatus() {
        return "System Messages";
    }

    public Widget getStatusWidget() {
        return panel;
    }

    public void update(final AsyncCallback<Void> callback) {
        statusServices.getSystemMessages(new AsyncCallback<List<SystemMessage>>() {
            public void onFailure(Throwable caught) {
                panel.clear();
                callback.onFailure(caught);
            }

            public void onSuccess(List<SystemMessage> result) {
                panel.clear();
                for (SystemMessage sm : result) {
                    final DockPanel p = new DockPanel();
                    final SystemMessage msg = sm;
                    p.setSpacing(4);
                    p.addStyleName("SystemMessage");
                    p.addStyleName("SystemMessage-Level"+ sm.getLevel());
                    p.setWidth("100%");
                    Label l = new Label(new Date(sm.getStartTime()).toString() + " - " + sm.getTypeName());
                    l.addStyleName("SystemMessage-Header");
                    p.add(l, DockPanel.NORTH);
                    Label sysMsg = new Label(sm.getMessage());
                    p.add(sysMsg, DockPanel.CENTER);
                    p.setCellWidth(sysMsg, "100%");
                    
                    Image img = new Image("images/16x16/dialog-information.png");
                    p.add(img, DockPanel.WEST);
                    p.setCellHorizontalAlignment(img, HasHorizontalAlignment.ALIGN_LEFT);
                    p.setCellVerticalAlignment(img, HasVerticalAlignment.ALIGN_MIDDLE);
                    
                    if (sm.getLevel()==2) {
                        img.setUrl("images/16x16/dialog-warning.png");
                    }
                    if (sm.getLevel()>=3) {
                        img.setUrl("images/16x16/dialog-error.png");
                    }
                    
                    CommandItem cmd = new CommandItem("images/16x16/edit-delete.png", null, new Command() {
                        public void execute() {
                            statusServices.deleteSystemMessage(msg.getId(), new AsyncCallback<Void>() {
                                public void onFailure(Throwable caught) {
                                    Application.fireErrorEvent("Could not remove System Message", caught);
                                }

                                public void onSuccess(Void result) {
                                    panel.remove(p);
                                }
                            });
                        }
                    });
                    
                    p.add(cmd, DockPanel.EAST);
                    p.setCellHorizontalAlignment(cmd, HasHorizontalAlignment.ALIGN_LEFT);
                    p.setCellVerticalAlignment(cmd, HasVerticalAlignment.ALIGN_MIDDLE);
                    
                    panel.add(p);
                }
                callback.onSuccess(null);
            }
        });
    }

    public Widget getHeaderActionsWidget() {
        return actions;
    }
}
