package org.jdna.bmt.web.client.ui.browser;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BrowsingServiceAsync {
    public void browseChildren(MediaFolder folder, AsyncCallback<MediaResource[]> callback);
    public void getFactories(GWTFactoryInfo.SourceType sourceType, AsyncCallback<GWTFactoryInfo[]> sources);
    public void getFolderForSource(GWTFactoryInfo source, MediaFolder parentFolder, AsyncCallback<MediaFolder> folder);
}
