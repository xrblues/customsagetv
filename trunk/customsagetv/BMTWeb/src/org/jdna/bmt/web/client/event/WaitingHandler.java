package org.jdna.bmt.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface WaitingHandler extends EventHandler {
    public void onWaiting(WaitingEvent event);
}
