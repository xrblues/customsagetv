package org.jdna.bmt.web.client.ui.status;

import org.jdna.bmt.web.client.ui.layout.FlowGrid;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StatusPanel extends Composite {
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
            
            //DecoratorPanel dp = new DecoratorPanel();
            //dp.setWidget(vp);
            
            initWidget(vp);
        }
    }
    
    FlowGrid grid = new FlowGrid(3);
    public StatusPanel() {
        grid.setWidth("100%");
        
        addStatus(new SimpleStatus("Phoenix", "Phoenix Status Information", "phoenix"));
        addStatus(new SimpleStatus("Metadata Tools", "Metadata Tools Status Information", "bmt"));
        addStatus(new SimpleStatus("SageTV", "SageTV Status Information", "sagetv"));
        initWidget(grid);
    }
    
    public void addStatus(HasStatus status) {
        grid.add(new StatusBox(status));
        status.update();
    }
}
