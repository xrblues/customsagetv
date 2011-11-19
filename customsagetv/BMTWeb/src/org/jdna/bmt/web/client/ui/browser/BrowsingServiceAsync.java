package org.jdna.bmt.web.client.ui.browser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.media.GWTViewCategories;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.util.NamedProperty;

import sagex.phoenix.metadata.MediaArtifactType;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BrowsingServiceAsync {
    void browseChildren(GWTMediaFolder folder, int start, int size, AsyncCallback<GWTMediaResource[]> callback);
    void scan(GWTMediaFolder folder, PersistenceOptionsUI options, AsyncCallback<String> callback);
    void loadMetadata(GWTMediaFile mediaFile, AsyncCallback<GWTMediaMetadata> callback);
    void getMetadata(GWTMediaSearchResult result, GWTPersistenceOptions options,AsyncCallback<GWTMediaMetadata> callback);
    void searchForMetadata(GWTMediaFile item, SearchQueryOptions options, AsyncCallback<List<GWTMediaSearchResult>> jsonReply);
    void getStatus(String id, AsyncCallback<ProgressStatus> status);
    void saveMetadata(GWTMediaFile file, PersistenceOptionsUI options, AsyncCallback<ServiceReply<GWTMediaFile>> result);
    void getProviders(AsyncCallback<List<GWTProviderInfo>> result);
    void cancelScan(String scanId, AsyncCallback<Void> result);
    void getScansInProgress(AsyncCallback<ProgressStatus[]> callback);
    void removeScan(String progressId, AsyncCallback<Void> asyncCallback);
    void getProgressItems(String progressId, boolean b, AsyncCallback<GWTMediaResource[]> asyncCallback);
    void getFanart(GWTMediaFile file, MediaArtifactType artifact, AsyncCallback<ArrayList<GWTMediaArt>> callback);
    void downloadFanart(GWTMediaFile file, MediaArtifactType artifact, GWTMediaArt ma, AsyncCallback<ServiceReply<GWTMediaArt>> callback);
    void deleteFanart(GWTMediaArt art, AsyncCallback<Boolean> callback);
    void makeDefaultFanart(GWTMediaFile file, MediaArtifactType type, GWTMediaArt art, AsyncCallback<Void> callback);
	void searchMediaFiles(String search, AsyncCallback<ServiceReply<GWTMediaFolder>> callback);
	void discoverQueryOptions(GWTMediaFile file, AsyncCallback<SearchQueryOptions> callback);
	void getViews(String tag, AsyncCallback<GWTViewCategories> callback);    
    void getView(GWTView view, AsyncCallback<GWTMediaFolder> folder);
	void getViewCategories(AsyncCallback<ArrayList<GWTView>> callback);
	void record(GWTMediaFile file, AsyncCallback<String> callback);
	void setWatched(GWTMediaFile file, boolean watched,	AsyncCallback<Void> callback);
	void cancelRecord(GWTMediaFile file, AsyncCallback<Void> callback);
	void getEditableMetadataFields(GWTMediaResource res, AsyncCallback<ArrayList<NamedProperty<String>>> callback);
	void batchUpdateMetadata(GWTMediaResource res,	ArrayList<NamedProperty<String>> props,	AsyncCallback<Boolean> callback);
	void addMediaTitle(HashMap<String, String> fields,	AsyncCallback<ServiceReply<Boolean>> callback);
	void loadFiles(String dir, String mask, AsyncCallback<ArrayList<String>> files);
	void getFanartFiles(GWTMediaFile file, AsyncCallback<ArrayList<String>> files);
	void toggleViewVisibility(GWTView view, AsyncCallback<Void> callback);
}
