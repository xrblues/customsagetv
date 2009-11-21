package org.jdna.bmt.web.client.ui.browser;

import com.google.gwt.event.shared.GwtEvent;

public class BrowseRequestEvent extends GwtEvent<BrowseRequestHandler> {
    public static final GwtEvent.Type<BrowseRequestHandler> TYPE =  new GwtEvent.Type<BrowseRequestHandler>();
    private MediaFolder folder = null;
    
    public BrowseRequestEvent(MediaFolder folder) {
        this.folder=folder;
    }
    
    @Override
    protected void dispatch(BrowseRequestHandler handler) {
        handler.onBrowseRequest(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<BrowseRequestHandler> getAssociatedType() {
        return TYPE;
    }
}
