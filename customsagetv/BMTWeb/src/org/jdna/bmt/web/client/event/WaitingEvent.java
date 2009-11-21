package org.jdna.bmt.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class WaitingEvent extends GwtEvent<WaitingHandler> {
    public static final GwtEvent.Type<WaitingHandler> TYPE =  new GwtEvent.Type<WaitingHandler>();
    private String id;
    private boolean waiting;
    
    public WaitingEvent(String id, boolean waiting) {
        this.id=id;
        this.waiting=waiting;
    }
    
    @Override
    protected void dispatch(WaitingHandler handler) {
        handler.onWaiting(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<WaitingHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the waiting
     */
    public boolean isWaiting() {
        return waiting;
    }
}
