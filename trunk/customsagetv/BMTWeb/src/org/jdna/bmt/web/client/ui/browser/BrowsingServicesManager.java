package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTFactoryInfo;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.media.SageQueryFolder;
import org.jdna.bmt.web.client.ui.app.ErrorEvent;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BrowsingServicesManager {
    private static final BrowsingServicesManager instance = new BrowsingServicesManager();
    private BrowsingServiceAsync browser = GWT.create(BrowsingService.class);
    
    public BrowsingServicesManager() {
    }
    
    public void browseSageFiles(String mask, final String title) {
        browseFolder(new SageQueryFolder(mask, title));
    }

    public static BrowsingServicesManager getInstance() {
        return instance;
    }

    public void browseFolder(final GWTMediaFolder folder) {
        // TODO: Use a SystemEvent, like, WaitingEven.fireBeginWait(), and then have this even call a cancel wait when a reply is received
        // 
        // if the folder has children, then use them
        if (folder.getChildren()!=null) {
            Log.debug("Using Cached Items");
            Application.events().fireEvent(new BrowseReplyEvent(folder));
            return;
        }
        
        browser.browseChildren(folder, new AsyncCallback<GWTMediaResource[]>() {
            public void onFailure(Throwable caught) {
                System.out.println("**** ERROR **** " + caught.getMessage());
                Application.events().fireEvent(new ErrorEvent(Application.messages().failedToBrowseFolder(folder.getTitle()), caught));
            }

            public void onSuccess(GWTMediaResource[] result) {
                folder.setChildren(result);
                Application.events().fireEvent(new BrowseReplyEvent(folder));
            }
        });
    }

    public void getFactoryInfo(final GWTFactoryInfo.SourceType sourceType) {
        browser.getFactories(sourceType, new AsyncCallback<GWTFactoryInfo[]>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent(Application.messages().failedToGetFactoryInfo(sourceType.name()), caught);
            }

            public void onSuccess(GWTFactoryInfo[] result) {
                Application.events().fireEvent(new FactoriesReplyEvent(sourceType, result));
            }
        });
    }
    
    public BrowsingServiceAsync getServices() {
        return browser;
    }

    public void getFolderForSource(final GWTFactoryInfo factory, final GWTMediaFolder folder) {
        Log.debug("Browse Source: " + factory);
        browser.getFolderForSource(factory, folder, new AsyncCallback<GWTMediaFolder>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent(Application.messages().failedToBrowseSource(factory.getLabel()), caught);
            }
            
            public void onSuccess(GWTMediaFolder result) {
                browseFolder(result);
            }
        });
    }
}
