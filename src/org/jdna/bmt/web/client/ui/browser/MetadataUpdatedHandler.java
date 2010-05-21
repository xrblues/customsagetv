package org.jdna.bmt.web.client.ui.browser;

import com.google.gwt.event.shared.EventHandler;

public interface MetadataUpdatedHandler extends EventHandler {
    public void onMetadataUpdated(MetadataUpdatedEvent event);
}
