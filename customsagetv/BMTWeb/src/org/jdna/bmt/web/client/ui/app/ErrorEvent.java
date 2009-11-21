package org.jdna.bmt.web.client.ui.app;

import com.google.gwt.event.shared.GwtEvent;

public class ErrorEvent extends GwtEvent<ErrorEventHandler> {
    public static final GwtEvent.Type<ErrorEventHandler> TYPE =  new GwtEvent.Type<ErrorEventHandler>();

    private String message;
    private Throwable exception;
    
    public ErrorEvent(String message) {
        this(message,null);
    }
    
    public ErrorEvent(String message, Throwable ex) {
        this.message=message;
        this.exception=ex;
    }
    
    @Override
    protected void dispatch(ErrorEventHandler handler) {
        handler.onError(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ErrorEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getException() {
        return exception;
    }
}
