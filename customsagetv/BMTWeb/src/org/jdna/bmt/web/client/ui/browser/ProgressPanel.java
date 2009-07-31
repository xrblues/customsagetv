package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.util.WaitingPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class ProgressPanel extends Composite {
    private Label label = new Label();
    private WaitingPanel waiting = new WaitingPanel();
    public ProgressPanel() {
        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        label.setText("Scanning...");
        hp.add(label);
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
            } else {
                label.setText("Scanning... ("+ status.getWorked() + " of " + status.getTotalWork() +")" + ((int)(status.getComplete()*100)) + "%");
            }
        }
    }
}
