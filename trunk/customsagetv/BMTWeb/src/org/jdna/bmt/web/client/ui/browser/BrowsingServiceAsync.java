package org.jdna.bmt.web.client.ui.browser;


import org.jdna.bmt.web.client.media.GWTFactoryInfo;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaResource;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BrowsingServiceAsync {
    public void browseChildren(GWTMediaFolder folder, AsyncCallback<GWTMediaResource[]> callback);
    public void getFactories(GWTFactoryInfo.SourceType sourceType, AsyncCallback<GWTFactoryInfo[]> sources);
    public void getFolderForSource(GWTFactoryInfo source, GWTMediaFolder parentFolder, AsyncCallback<GWTMediaFolder> folder);
}
