package org.jdna.process;

import sagex.phoenix.event.EventHandler;

/**
 * Handler for handling when a media file fails to update/scan
 * 
 * @author seans
 *
 */
public interface MetadataFailedEventHandler extends EventHandler {
    public void onMetadataFailed(MetadataFailedEvent event);
}
