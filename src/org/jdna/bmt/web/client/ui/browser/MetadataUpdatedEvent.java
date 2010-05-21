package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTMediaFile;

import com.google.gwt.event.shared.GwtEvent;

public class MetadataUpdatedEvent extends GwtEvent<MetadataUpdatedHandler> {
    public static final GwtEvent.Type<MetadataUpdatedHandler> TYPE =  new GwtEvent.Type<MetadataUpdatedHandler>();
    private GWTMediaFile file = null;
    
    public MetadataUpdatedEvent(GWTMediaFile file) {
        this.file=file;
    }
    
    @Override
    protected void dispatch(MetadataUpdatedHandler handler) {
        handler.onMetadataUpdated(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<MetadataUpdatedHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the folder
     */
    public GWTMediaFile getFile() {
        return file;
    }
}
