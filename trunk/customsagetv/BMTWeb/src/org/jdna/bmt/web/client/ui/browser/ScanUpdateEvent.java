package org.jdna.bmt.web.client.ui.browser;

import com.google.gwt.event.shared.GwtEvent;

public class ScanUpdateEvent extends GwtEvent<ScanUpdateHandler> {
    public static final GwtEvent.Type<ScanUpdateHandler> TYPE =  new GwtEvent.Type<ScanUpdateHandler>();
    private ProgressStatus status;
    
    public ScanUpdateEvent(ProgressStatus status) {
        this.status=status;
    }
    
    @Override
    protected void dispatch(ScanUpdateHandler handler) {
        handler.onScanUpdate(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ScanUpdateHandler> getAssociatedType() {
        return TYPE;
    }
    
    public ProgressStatus getProgressStatus() {
        return status;
    }
}
