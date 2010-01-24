package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTMediaFolder;

import com.google.gwt.event.shared.GwtEvent;

public class ScanRequestEvent extends GwtEvent<ScanRequestHandler> {
    public static final GwtEvent.Type<ScanRequestHandler> TYPE =  new GwtEvent.Type<ScanRequestHandler>();
    private GWTMediaFolder folder = null;
    private ScanOptions options = null;
    private String trackingId=null;
    
    public ScanRequestEvent(GWTMediaFolder folder, ScanOptions options, String trackingId) {
        this.folder=folder;
        this.options=options;
        this.trackingId=trackingId;
    }
    
    @Override
    protected void dispatch(ScanRequestHandler handler) {
        handler.onScanRequest(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ScanRequestHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the folder
     */
    public GWTMediaFolder getFolder() {
        return folder;
    }

    /**
     * @return the options
     */
    public ScanOptions getOptions() {
        return options;
    }

    /**
     * @return the trackingId
     */
    public String getTrackingId() {
        return trackingId;
    }
}
