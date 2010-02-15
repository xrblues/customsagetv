package org.jdna.bmt.web.client.ui.status;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SystemMessageStatus implements HasStatus {
    private final StatusServicesAsync statusServices = GWT.create(StatusServices.class);
    VerticalPanel panel = new VerticalPanel();

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
                System.out.println("**** have " + result.size() + " messages");
                for (SystemMessage sm : result) {
                    DockPanel p = new DockPanel();
                    p.setSpacing(4);
                    p.addStyleName("SystemMessage");
                    p.addStyleName("SystemMessage-Level"+ sm.getLevel());
                    p.setWidth("100%");
                    Label l = new Label(sm.getTypeName());
                    l.addStyleName("SystemMessage-Header");
                    p.add(l, DockPanel.NORTH);
                    p.add(new Label(sm.getMessage()), DockPanel.CENTER);
                    l = new Label(new Date(sm.getStartTime()).toString());
                    l.setWordWrap(false);
                    p.add(l, DockPanel.EAST);
                    p.setCellHorizontalAlignment(l, HasHorizontalAlignment.ALIGN_RIGHT);
                    p.setCellVerticalAlignment(l, HasVerticalAlignment.ALIGN_MIDDLE);
                    
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
                    
                    panel.add(p);
                }
                callback.onSuccess(null);
            }
        });
    }
}
