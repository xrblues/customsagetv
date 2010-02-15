package org.jdna.bmt.web.client.ui.debug;

import java.util.Map;

import org.jdna.bmt.web.client.media.GWTMediaFile;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;




@RemoteServiceRelativePath("debug")
public interface DebugService extends RemoteService {
    public Map<String,String> getMetadata(String source, GWTMediaFile file);
}
