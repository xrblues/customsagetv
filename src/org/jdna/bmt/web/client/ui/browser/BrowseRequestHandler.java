package org.jdna.bmt.web.client.ui.browser;

import com.google.gwt.event.shared.EventHandler;

public interface BrowseRequestHandler extends EventHandler {
    public void onBrowseRequest(BrowseRequestEvent event);
}
