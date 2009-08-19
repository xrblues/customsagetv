package org.jdna.bmt.web.client.ui.browser;



import java.util.List;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("browser")
public interface BrowserService extends RemoteService {
    public String scan(ScanOptions options);
    public GWTMediaMetadata loadMetadata(GWTMediaFile file);
    public GWTMediaMetadata getMetadata(GWTMediaSearchResult result);
    public List<GWTMediaSearchResult> searchForMetadata(GWTMediaFile item, String provider);
    public ProgressStatus getStatus(String id);
    public ServiceReply<GWTMediaFile> saveMetadata(GWTMediaFile file);
}
