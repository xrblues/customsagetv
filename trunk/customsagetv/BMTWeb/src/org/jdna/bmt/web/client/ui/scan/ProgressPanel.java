package org.jdna.bmt.web.client.ui.scan;

import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

public class ProgressPanel extends Composite {
    private final BrowserServiceAsync browserService = GWT.create(BrowserService.class);

    private Label label = new Label();
    private Hyperlink cancel =new Hyperlink("Cancel", "#cancelScan");
    private WaitingPanel waiting = new WaitingPanel();
    public ProgressPanel() {
        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        
        label.setText("Scanning...");
        hp.add(label);
        
        cancel.setStyleName("ProgressPanel-Cancel");
        cancel.setVisible(false);
        hp.add(cancel);
        
        
        waiting.setWidth("40px");
        hp.add(waiting);
        initWidget(hp);
    }
    
    public void updateProgress(ProgressStatus status) {
        if (status==null) {
            label.setText("No Status");
        } else {
            if (status.isDone() || status.isCancelled()) {
                label.setText(status.getStatus());
                waiting.setVisible(false);
                cancel.setVisible(false);
            } else {
                if (!cancel.isVisible()) {
                    final String statusId = status.getStatusId();
                    cancel.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            browserService.cancelScan(statusId, new AsyncCallback<Void>() {
                                public void onFailure(Throwable caught) {
                                    Log.error("Cannot Cancel", caught);
                                }

                                public void onSuccess(Void result) {
                                    Dialogs.showMessage("Scan Cancelled");
                                }
                            });
                        }
                    });
                    cancel.setVisible(true);
                }
                label.setText("Scanning... ("+ status.getWorked() + " of " + status.getTotalWork() +")" + ((int)(status.getComplete()*100)) + "%");
            }
        }
    }
}
