package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTMediaFolder;

import com.google.gwt.event.shared.GwtEvent;

public class BrowseReplyEvent extends GwtEvent<BrowseReplyHandler> {
    public static final GwtEvent.Type<BrowseReplyHandler> TYPE =  new GwtEvent.Type<BrowseReplyHandler>();
    private GWTMediaFolder folder = null;
    
    public BrowseReplyEvent(GWTMediaFolder folder) {
        this.folder=folder;
    }
    
    @Override
    protected void dispatch(BrowseReplyHandler handler) {
        handler.onBrowseReply(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<BrowseReplyHandler> getAssociatedType() {
        return TYPE;
    }

    public GWTMediaFolder getBrowseableFolder() {
        return folder;
    }
}
