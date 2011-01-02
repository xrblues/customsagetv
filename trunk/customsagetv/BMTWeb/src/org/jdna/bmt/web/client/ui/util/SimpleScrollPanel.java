package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleScrollPanel extends Composite implements ResizeHandler {
    private ScrollPanel scroller = new ScrollPanel();
    private HandlerRegistration resizeHandler;

    public SimpleScrollPanel(Widget content) {
        super();
        
        scroller.setWidth("100%");
        scroller.setHeight("100%");
        scroller.setAlwaysShowScrollBars(false);
        scroller.setWidget(content);
        initWidget(scroller);
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

    public void resize() {
        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                ResizeEvent evt = new ResizeEvent(Window.getClientWidth(), Window.getClientHeight()) {
                    // no body
                };
                onResize(evt);
            }
        });
    }
}
