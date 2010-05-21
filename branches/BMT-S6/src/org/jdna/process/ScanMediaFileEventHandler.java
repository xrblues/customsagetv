package org.jdna.process;

import sagex.phoenix.event.EventHandler;

/**
 * Event called when a media file needs to be scanned
 * 
 * @author seans
 *
 */
public interface ScanMediaFileEventHandler extends EventHandler {
    public void onScanRequest(ScanMediaFileEvent event);
}
