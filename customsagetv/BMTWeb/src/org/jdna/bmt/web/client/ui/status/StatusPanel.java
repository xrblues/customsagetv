package org.jdna.bmt.web.client.ui.status;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.i18n.Labels;
import org.jdna.bmt.web.client.ui.layout.FlowGrid;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StatusPanel extends Composite {
    private Labels labels = Application.labels();
    
    private static class StatusBox extends Composite {
        VerticalPanel vp = new VerticalPanel();
        public StatusBox(HasStatus status) {
            vp.setSpacing(5);
            vp.setWidth("100%");
            Label l = new Label(status.getStatus());
            l.setTitle(status.getHelp());
            l.addStyleName("StatusBox-Title");
            vp.add(l);
            vp.add(status.getStatusWidget());
            
            initWidget(vp);
        }
    }
    
    FlowGrid grid = new FlowGrid(3);
    public StatusPanel() {
        grid.setWidth("100%");
        
        addStatus(new SimpleStatus(labels.statusPhoenix(), labels.statusPhoenixDesc(), "phoenix"));
        addStatus(new SimpleStatus(labels.metadataTools(), labels.metadataToolsDesc(), "bmt"));
        addStatus(new SimpleStatus(labels.sagetv(), labels.sagetvDesc(), "sagetv"));
        initWidget(grid);
    }
    
    public void addStatus(HasStatus status) {
        grid.add(new StatusBox(status));
        status.update();
    }
}
