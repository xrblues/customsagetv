package org.jdna.bmt.web.client.ui.scan;



import java.util.List;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("browser")
public interface BrowserService extends RemoteService {
    public String scan(ScanOptions options);
    public GWTMediaMetadata loadMetadata(GWTMediaFile file);
    public GWTMediaMetadata getMetadata(GWTMediaSearchResult result, GWTPersistenceOptions options);
    public List<GWTMediaSearchResult> searchForMetadata(GWTMediaFile item, SearchQueryOptions options);
    public ProgressStatus getStatus(String id);
    public void cancelScan(String id);
    public ServiceReply<GWTMediaFile> saveMetadata(GWTMediaFile file, SaveOptions options);
    public List<GWTProviderInfo> getProviders();
}
