package org.jdna.bmt.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface NotificationEventHandler extends EventHandler {
    public void onNotification(NotificationEvent event);
}
