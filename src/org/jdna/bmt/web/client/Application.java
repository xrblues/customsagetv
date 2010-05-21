package org.jdna.bmt.web.client;

import org.jdna.bmt.web.client.event.EventBus;
import org.jdna.bmt.web.client.event.NotificationEvent;
import org.jdna.bmt.web.client.event.NotificationEvent.MessageType;
import org.jdna.bmt.web.client.i18n.Labels;
import org.jdna.bmt.web.client.i18n.Msgs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

public class Application {
    private static final Labels i18nLabels = GWT.create(Labels.class);
    private static final Msgs i18nMessages = GWT.create(Msgs.class);
    private static final HandlerManager eventBus = EventBus.getHandlerManager();
    
    public static Labels labels() {
        return i18nLabels;
    }
    
    public static Msgs messages() {
        return i18nMessages;
    }

    public static HandlerManager events() {
        return eventBus;
    }

    public static void fireErrorEvent(String msg) {
        fireErrorEvent(msg, null);
    }

    public static void fireErrorEvent(String msg, Throwable t) {
        events().fireEvent(new NotificationEvent(MessageType.ERROR, msg, t));    
    }

    public static void fireNotification(String msg) {
        events().fireEvent(new NotificationEvent(MessageType.INFO, msg));    
    }
}
