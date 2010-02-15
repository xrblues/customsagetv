package org.jdna.bmt.web.client.ui.debug;

import java.util.Map;

import org.jdna.bmt.web.client.media.GWTMediaFile;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DebugServiceAsync {
    public void getMetadata(String source, GWTMediaFile file, AsyncCallback<Map<String, String>> callback);
}
