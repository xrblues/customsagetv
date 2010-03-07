package org.jdna.bmt.web.client.ui.status;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.i18n.Labels;
import org.jdna.bmt.web.client.ui.layout.FlowGrid;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StatusPanel extends Composite implements ResizeHandler {
    private Labels labels = Application.labels();
    private FlowGrid grid = new FlowGrid(2);
    private ScrollPanel scroller = new ScrollPanel();
    private HandlerRegistration resizeHandler;
    
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
        scroller.setWidth("100%");
        scroller.setHeight("100%");
        scroller.setAlwaysShowScrollBars(false);
        scroller.setWidget(grid);
        
        grid.setWidth("100%");
        
        addStatus(new SimpleStatus(labels.statusPhoenix(), labels.statusPhoenixDesc(), "phoenix"));
        addStatus(new SimpleStatus(labels.metadataTools(), labels.metadataToolsDesc(), "bmt"));
        addStatus(new SimpleStatus(labels.sagetv(), labels.sagetvDesc(), "sagetv"));
        addStatus(new SimpleStatus(labels.jars(), labels.jarsDesc(), "jars"));
        addStatus(new SystemMessageStatus());
        initWidget(scroller);
        resize();
    }
    
    @Override
    protected void onAttach() {
        resizeHandler = Window.addResizeHandler(this);
        super.onAttach();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        resizeHandler.removeHandler();
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
    
    public void onResize(ResizeEvent event) {
        onWindowResized(event.getWidth(), event.getHeight());
    }
    
    public void onWindowResized(int windowWidth, int windowHeight) {
        int scrollWidth = windowWidth - scroller.getAbsoluteLeft();
        if (scrollWidth < 1) {
            scrollWidth = 1;
        }

        int scrollHeight = windowHeight - scroller.getAbsoluteTop();
        if (scrollHeight < 1) {
            scrollHeight = 1;
        }
        scroller.setPixelSize(scrollWidth, scrollHeight);
    }

    private void resize() {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                ResizeEvent evt = new ResizeEvent(Window.getClientWidth(), Window.getClientHeight()) {
                    // no body
                };
                onResize(evt);
            }
        });
    }

}
