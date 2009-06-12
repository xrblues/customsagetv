package org.jdna.bmt.web.client.ui.status;

import java.util.List;

import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.util.Log;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SimpleStatus implements HasStatus {
    private final StatusServicesAsync statusServices = GWT.create(StatusServices.class);

    private String help;
    private String name;
    private String id;
    
    private Simple2ColFormLayoutPanel panel = new Simple2ColFormLayoutPanel();
    public SimpleStatus(String name, String help, String id) {
        this.name=name;
        this.help=help;
        this.id=id;
        panel.setWidth("100%");
    }
    
    public String getHelp() {
        return help;
    }

    public String getStatus() {
        return name;
    }

    public Widget getStatusWidget() {
        return panel;
    }
    
    public void update() {
        statusServices.getStatusInfo(id, new AsyncCallback<List<StatusValue>>() {
            public void onFailure(Throwable caught) {
                Log.error("Failed to get status for: " + name, caught);
            }

            public void onSuccess(List<StatusValue> result) {
                for (StatusValue v : result) {
                    if (v.getLabel().equals("--")) {
                        panel.add("", new Label());
                    } else {
                        Label lab = new Label(v.getValue());
                        if (!StringUtils.isEmpty(v.getReason())) {
                            lab.setTitle(v.getReason());
                        }
                        panel.add(v.getLabel(), lab);
                        System.out.println("STATUS: " + v.getLevel() + "; " + v.getLabel());
                        if (v.getLevel()== v.ERROR) {
                            System.out.println("Adding Error!!!!; " + v.getLabel());
                            panel.addRowStyle("error");
                        } else if (v.getLevel()== v.WARN) {
                            panel.addRowStyle("warn");
                        }
                    }
                }
            }
        });
    }
}
