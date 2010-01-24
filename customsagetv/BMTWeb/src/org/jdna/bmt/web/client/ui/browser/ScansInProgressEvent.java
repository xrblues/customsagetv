package org.jdna.bmt.web.client.ui.browser;

import com.google.gwt.event.shared.GwtEvent;

public class ScansInProgressEvent extends GwtEvent<ScansInProgressHandler> {
    public static final GwtEvent.Type<ScansInProgressHandler> TYPE =  new GwtEvent.Type<ScansInProgressHandler>();
    private ProgressStatus[] statuses;
    
    public ScansInProgressEvent(ProgressStatus[] status) {
        this.statuses=status;
    }
    
    @Override
    protected void dispatch(ScansInProgressHandler handler) {
        handler.onScansInProgress(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ScansInProgressHandler> getAssociatedType() {
        return TYPE;
    }
    
    public ProgressStatus[] getProgressStatuses() {
        return statuses;
    }
}
