package org.jdna.bmt.web.client.ui.status;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.i18n.Labels;
import org.jdna.bmt.web.client.ui.layout.FlowGrid;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StatusPanel extends Composite {
    private Labels labels = Application.labels();
    private FlowGrid grid = new FlowGrid(2);
    
    private static class StatusBox extends Composite {
        VerticalPanel vp = new VerticalPanel();
        ScrollPanel scroll = new ScrollPanel();
        public StatusBox(HasStatus status) {
            vp.setSpacing(5);
            vp.setWidth("100%");
            Label l = new Label(status.getStatus());
            l.setTitle(status.getHelp());
            l.addStyleName("StatusBox-Title");
            vp.add(l);
            scroll.setWidget(status.getStatusWidget());
            vp.add(scroll);
            vp.addStyleName("StatusBox");
            
            initWidget(vp);
        }
        
        public void resize() {
            System.out.println("**** " + this.getOffsetHeight());
            if (this.getOffsetHeight()>150) {
                scroll.setHeight("150px");
            }
        }
    }
    
    public StatusPanel() {
        grid.setWidth("100%");
        
        addStatus(new SimpleStatus(labels.statusPhoenix(), labels.statusPhoenixDesc(), "phoenix"));
        addStatus(new SimpleStatus(labels.metadataTools(), labels.metadataToolsDesc(), "bmt"));
        addStatus(new SimpleStatus(labels.sagetv(), labels.sagetvDesc(), "sagetv"));
        addStatus(new SimpleStatus(labels.jars(), labels.jarsDesc(), "jars"));
        addStatus(new SystemMessageStatus());
        initWidget(grid);
    }
    
    public void addStatus(HasStatus status) {
        final StatusBox sb = new StatusBox(status); 
        grid.add(sb);
        status.update(new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                sb.resize();
            }

            public void onSuccess(Void result) {
                sb.resize();
            }
        });
    }
}
