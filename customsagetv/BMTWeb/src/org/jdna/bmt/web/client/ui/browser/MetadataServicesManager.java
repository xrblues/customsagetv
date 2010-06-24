package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import sagex.phoenix.metadata.MediaArtifactType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

public class MetadataServicesManager {
    private static final MetadataServicesManager instance = new MetadataServicesManager();
    private static final MetadataServiceAsync service = GWT.create(MetadataService.class);
    
    public MetadataServicesManager() {
    }
    
    public static MetadataServicesManager getInstance() {
        return instance;
    }

    public void scan(final GWTMediaFolder folder, final PersistenceOptionsUI options) {
        service.scan(folder, options, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent(Application.messages().failedToScan(folder.getTitle()), caught);
            }

            public void onSuccess(String progressId) {
                System.out.println("Scan Started; Progress Id: " + progressId);
                
                // notify listeners about the scan
                Application.events().fireEvent(new ScanRequestEvent(folder, options, progressId));
                
                // Create a process that will monitor the progress and send event updates
                monitorProgress(progressId);
            }
        });
    }

    public void monitorProgress(final String progressId) {
        System.out.println("Monitoring progress for: " + progressId);
        Timer timer = new Timer() {
            @Override
            public void run() {
                System.out.println("Scanning For Progress for: " + progressId);
                service.getStatus(progressId, new AsyncCallback<ProgressStatus>() {
                    public void onFailure(Throwable caught) {
                        Application.fireErrorEvent("Scan Failed: " + caught.getMessage());
                        cancel();
                        
                        ProgressStatus result = new ProgressStatus();
                        result.setProgressId(progressId);
                        result.setIsDone(true);
                        result.setStatus(caught.getMessage());

                        // notify listeners about the update
                        Application.events().fireEvent(new ScanUpdateEvent(result));
                    }

                    public void onSuccess(ProgressStatus result) {
                        if (result == null) {
                            // if there is no result, then set this to done
                            result = new ProgressStatus();
                            result.setProgressId(progressId);
                            result.setIsDone(true);
                        }
                        
                        if (result.isCancelled() || result.isDone()) {
                            System.out.println("Scan was completed.");
                            cancel();
                        }
                        
                        // notify listeners about the update
                        Application.events().fireEvent(new ScanUpdateEvent(result));
                    }
                });
            }
        };
        timer.scheduleRepeating(400);
    }
    
    public void requestScanProgress(String progressId) {
        service.getStatus(progressId, new AsyncCallback<ProgressStatus>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to get Scan progress", caught);
            }

            public void onSuccess(ProgressStatus result) {
                Application.events().fireEvent(new ScanUpdateEvent(result));
            }
        });
    }

    public void requestItemsForProgress(String progressId, final boolean b) {
        System.out.println("Getting items for progress: " + progressId);
        service.getProgressItems(progressId, b, new AsyncCallback<GWTMediaResource[]>() {
            public void onFailure(Throwable caught) {
            }

            public void onSuccess(GWTMediaResource[] result) {
                GWTMediaFolder folder = new GWTMediaFolder(null, (b)?"Success Items":"Failed Items");
                folder.setChildren(result);
                // disable actions on these folders
                folder.setAllowActions(false);
                Application.events().fireEvent(new BrowseReplyEvent(folder));
            }
        });
    }
    
    public void cancelScan(String progressId) {
        service.cancelScan(progressId, new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
            }

            public void onSuccess(Void result) {
            }
        });
    }
    
    public void requestScansInProgress() {
        service.getScansInProgress(new AsyncCallback<ProgressStatus[]>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to get Scans in progress", caught);
            }

            public void onSuccess(ProgressStatus[] result) {
                if (result!=null) {
                    Application.events().fireEvent(new ScansInProgressEvent(result));

                    for (ProgressStatus p : result) {
                        Application.events().fireEvent(new ScanUpdateEvent(p));
                        
                        if (! (p.isCancelled() || p.isDone())) {
                            // monitor the progress
                            monitorProgress(p.getProgressId());
                        }
                    }
                }
            }
        });
    }

    public MetadataServiceAsync getServices() {
        return service;
    }

    public void removeScan(String progressId) {
        service.removeScan(progressId, new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
            }

            public void onSuccess(Void result) {
            }
        });
    }
    
    public void requestUpdatedMetadata(final GWTMediaFile file) {
        service.loadMetadata(file, new AsyncCallback<GWTMediaMetadata>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to get metadata for item: " + file.getTitle(), caught);
            }

            public void onSuccess(GWTMediaMetadata result) {
                file.attachMetadata(result);
                metadataUpdated(file);
            }
        });
    }
    
    public void metadataUpdated(GWTMediaFile file) {
        Application.events().fireEvent(new MetadataUpdatedEvent(file));
    }
    
    public void saveMetadata(final GWTMediaFile file, PersistenceOptionsUI options) {
        final PopupPanel save = Dialogs.showWaitingPopup("Saving...");
        service.saveMetadata(file, options, new AsyncCallback<ServiceReply<GWTMediaFile>>() {
            public void onFailure(Throwable caught) {
                save.hide();
                Application.fireErrorEvent("Failed to save metadata for item: " + file.getTitle(), caught);
            }

            public void onSuccess(ServiceReply<GWTMediaFile> result) {
                save.hide();
                if (result==null || result.getCode()>0) {
                    if (result==null) {
                        Application.fireErrorEvent("Failed to save metadata for item: " + file.getTitle(), null);
                    } else {
                        Application.fireErrorEvent(result.getMessage(), null);
                    }
                } else {
                    metadataUpdated(result.getData());
                }
            }
        });
    }
    
    public void loadFanart(GWTMediaFile file, MediaArtifactType type, AsyncCallback<ArrayList<GWTMediaArt>> callback) {
        service.getFanart(file, type, callback);
    }

    public void downloadFanart(GWTMediaFile file, MediaArtifactType type, GWTMediaArt ma, AsyncCallback<GWTMediaArt> callback) {
        service.downloadFanart(file, type, ma, callback);
    }
    
    public static MetadataServiceAsync service() {
        return service;
    }
}
