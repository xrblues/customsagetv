package org.jdna.bmt.web.client.ui.browser;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;




@RemoteServiceRelativePath("browsing")
public interface BrowsingService extends RemoteService {
    public MediaResource[] browseChildren(MediaFolder folder);
    public GWTFactoryInfo[] getFactories(GWTFactoryInfo.SourceType sourceType);
    public MediaFolder getFolderForSource(GWTFactoryInfo source, MediaFolder parentFolder);
}
