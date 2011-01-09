package org.jdna.bmt.web.client.ui.status;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.i18n.Labels;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class StatusPanel extends Composite {
    private Labels labels = Application.labels();
    
    private VerticalPanel grid = new VerticalPanel();
    
    private static class StatusBox extends Composite {
        VerticalPanel vp = new VerticalPanel();
        public StatusBox(HasStatus status) {
            vp.setSpacing(5);
            vp.setWidth("750px");
            
            HorizontalPanel p = new HorizontalPanel();
            p.setWidth("100%");
            Label l = new Label(status.getStatus());
            l.setTitle(status.getHelp());
            l.addStyleName("StatusBox-Title");
            p.add(l);
            p.setCellHorizontalAlignment(l, HasHorizontalAlignment.ALIGN_LEFT);
            p.setCellVerticalAlignment(l, HasVerticalAlignment.ALIGN_MIDDLE);
            
            Widget actionsWidget = status.getHeaderActionsWidget();
            if (actionsWidget!=null) {
                p.add(actionsWidget);
                p.setCellHorizontalAlignment(actionsWidget, HasHorizontalAlignment.ALIGN_RIGHT);
                p.setCellVerticalAlignment(actionsWidget, HasVerticalAlignment.ALIGN_MIDDLE);
            }
            
            vp.add(p);
            vp.add(status.getStatusWidget());
            vp.addStyleName("StatusBox");
            
            initWidget(vp);
        }
    }
    
    public StatusPanel() {
        grid.setWidth("100%");
        grid.setSpacing(10);
        
        initWidget(grid);
        addStatus(new SimpleStatus(labels.statusPhoenix(), labels.statusPhoenixDesc(), "phoenix"));
        addStatus(new SimpleStatus(labels.sagetv(), labels.sagetvDesc(), "sagetv"));
        addStatus(new SystemMessageStatus());
    }
    
    public void addStatus(final HasStatus status) {
    	Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                final StatusBox sb = new StatusBox(status); 
                grid.add(sb);
                grid.setCellHorizontalAlignment(sb, HasHorizontalAlignment.ALIGN_CENTER);
                status.update(new AsyncCallback<Void>() {
                    public void onFailure(Throwable caught) {
                    }

                    public void onSuccess(Void result) {
                    }
                });
            }
        });
    }
}
