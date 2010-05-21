package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTFactoryInfo;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaResource;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;




@RemoteServiceRelativePath("browsing")
public interface BrowsingService extends RemoteService {
    public GWTMediaResource[] browseChildren(GWTMediaFolder folder);
    public GWTFactoryInfo[] getFactories(GWTFactoryInfo.SourceType sourceType);
    public GWTMediaFolder getFolderForSource(GWTFactoryInfo source, GWTMediaFolder parentFolder);
}
