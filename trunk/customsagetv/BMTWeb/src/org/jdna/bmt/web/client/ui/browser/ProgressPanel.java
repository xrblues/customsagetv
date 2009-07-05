package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.util.WaitingPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProgressPanel extends Composite {
    private Label label = new Label();
    public ProgressPanel() {
        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        label.setText("Scanning...");
        hp.add(label);
        Widget w = new WaitingPanel();
        w.setWidth("40px");
        hp.add(w);
        initWidget(hp);
    }
    
    public void updateProgress(ProgressStatus status) {
        if (status==null) {
            label.setText("No Status");
        } else {
            if (status.isDone()) {
                label.setText("Scan Complete.  Loading...");
            } else if (status.isCancelled()) {
                label.setText("Cancelling...");
            } else {
                label.setText("Scanning... ("+ status.getWorked() + " of " + status.getTotalWork() +")" + ((int)(status.getComplete()*100)) + "%");
            }
        }
    }
}
