package org.jdna.bmt.web.client.ui.browser;

import java.util.List;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditor extends Composite {
    private final BrowserServiceAsync browserService  = GWT.create(BrowserService.class);

    private HorizontalPanel           panel           = new HorizontalPanel();
    private DockPanel                 editor          = new DockPanel();

    private VerticalPanel             itemsPanel      = new VerticalPanel();
    private VerticalPanel             scannedItems    = new VerticalPanel();
    private VerticalPanel             itemStatusPanel = new VerticalPanel();

    private VerticalPanel             leftPanel       = new VerticalPanel();

    private ScrollPanel               scrollItems     = null;
    private ScrollPanel               scrollDetails   = null;

    private MediaEditorMetadataPanel          details         = null;

    private String                    progressScanId;
    private Timer                     timer           = null;

    private int                       successCount    = 0;

    private ProgressPanel             statusIndicator = new ProgressPanel();

    public MediaEditor(String progressId) {
        this.progressScanId = progressId;

        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.add(leftPanel);

        leftPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        leftPanel.setWidth("300px");
        leftPanel.setHeight("100%");

        itemStatusPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        itemStatusPanel.add(statusIndicator);

        leftPanel.add(itemStatusPanel);
        leftPanel.setCellHeight(itemStatusPanel, "20px");
        itemStatusPanel.setWidth("100%");
        statusIndicator.setWidth("100%");

        leftPanel.add(itemsPanel);
        itemsPanel.setHeight("100%");
        itemsPanel.setWidth("100%");

        scrollItems = new ScrollPanel(scannedItems);
        scrollItems.setWidth("100%");
        scrollItems.setHeight("100%");
        itemsPanel.add(scrollItems);
        scannedItems.setWidth("100%");

        leftPanel.setCellHeight(itemsPanel, "100%");

        editor.setWidth("100%");
        editor.setHeight("100%");

        panel.add(editor);
        panel.setCellWidth(editor, "90%");
        initWidget(panel);

        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                System.out.println("Updating Scroll Size");
                onWindowResized(Window.getClientWidth(), Window.getClientHeight());
            }
        });

        timer = new Timer() {
            @Override
            public void run() {
                System.out.println("Scanning For Progress for: " + progressScanId);
                browserService.getStatus(progressScanId, new AsyncCallback<ProgressStatus>() {
                    public void onFailure(Throwable caught) {
                        cancelTimer(caught);
                    }

                    public void onSuccess(ProgressStatus result) {
                        if (result != null) {
                            if ((result.isDone() || result.isCancelled()) && (result.getItems()==null || result.getItems().size()==0)) {
                                result.setStatus(result.getTotalWork() + " Items");
                                cancelTimer(result);
                            } else {
                                statusIndicator.updateProgress(result);
                                addResults(result.getItems());
                            }
                        } else {
                            cancelTimer("No Results");
                        }
                    }

                });
            }
        };
        timer.scheduleRepeating(400);

        onWindowResized(Window.getClientWidth(), Window.getClientHeight());
    }

    private void cancelTimer(String string) {
        ProgressStatus status = new ProgressStatus();
        status.setStatus(string);
        status.setIsDone(true);
        status.setIsCancelled(true);
        cancelTimer(status);
    }
    
    private void cancelTimer(Throwable t) {
        Log.error("Failed!", t);
        ProgressStatus status = new ProgressStatus();
        status.setStatus("Failed: " + t.getMessage());
        status.setIsDone(true);
        status.setIsCancelled(true);
        cancelTimer(status);
    }
    
    private void cancelTimer(ProgressStatus status) {
        timer.cancel();
        statusIndicator.updateProgress(status);
    }

    public void onWindowResized(int windowWidth, int windowHeight) {
        int scrollWidth = windowWidth - panel.getAbsoluteLeft();
        if (scrollWidth < 1) {
            scrollWidth = 1;
        }

        int scrollHeight = windowHeight - panel.getAbsoluteTop();
        if (scrollHeight < 1) {
            scrollHeight = 1;
        }

        panel.setPixelSize(scrollWidth, scrollHeight);

        resizeItemScroller(windowWidth, windowHeight);
        resizeDetailScroller(windowWidth, windowHeight);
    }

    public void resizeDetailScroller(int windowWidth, int windowHeight) {
        if (scrollDetails != null) {
            int editorWidth = windowWidth - scrollDetails.getAbsoluteLeft();
            if (editorWidth < 1) {
                editorWidth = 1;
            }

            int editorHeight = windowHeight - scrollDetails.getAbsoluteTop();
            if (editorHeight < 1) {
                editorHeight = 1;
            }

            editor.setPixelSize(editorWidth, editorHeight);
            scrollDetails.setPixelSize(editorWidth, editorHeight);
        }
    }

    public void resizeItemScroller(int windowWidth, int windowHeight) {
        int scrollWidth = windowWidth - scrollItems.getAbsoluteLeft() - 9;
        if (scrollWidth < 1) {
            scrollWidth = 1;
        }

        int scrollHeight = windowHeight - scrollItems.getAbsoluteTop();
        if (scrollHeight < 1) {
            scrollHeight = 1;
        }

        if (scrollWidth > 300) scrollWidth = 300;

        leftPanel.setWidth(scrollWidth + "px");
        scrollItems.setHeight(scrollHeight + "px");
    }

    protected void addResults(List<GWTMediaFile> items) {
        if (items != null) {
            successCount += items.size();
            for (int i = 0; i < items.size(); i++) {
                final MediaEditorMediaFileWidget item = new MediaEditorMediaFileWidget(items.get(i));
                item.setWidth("100%");
                item.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        updateMediaEditorPanel(item);
                    }
                });
                scannedItems.add(item);
            }
        }
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                onWindowResized(Window.getClientWidth(), Window.getClientHeight());
            }
        });
    }

    protected void updateMediaEditorPanel(MediaEditorMediaFileWidget item) {
        final GWTMediaFile mi = item.getMediaFile();
        final PopupPanel waiting = Dialogs.showWaitingPopup("Getting details for " + mi.getTitle());
        browserService.loadMetadata(mi, new AsyncCallback<GWTMediaMetadata>() {
            public void onFailure(Throwable caught) {
                Dialogs.hidePopup(waiting, 1000);
                editor.clear();
                Log.error("Failed to load metadata.", caught);
            }

            public void onSuccess(GWTMediaMetadata result) {
                Dialogs.hidePopup(waiting, 1000);
                mi.attachMetadata(result);
                updateEditorPanel(mi);
            }
        });
    }

    protected void updateEditorPanel(GWTMediaFile mediaFile) {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                onWindowResized(Window.getClientWidth(), Window.getClientHeight());
            }
        });
        
        editor.clear();
        details = new MediaEditorMetadataPanel(mediaFile);
        scrollDetails = new ScrollPanel();
        scrollDetails.setWidth("100%");
        scrollDetails.setHeight("100%");
        editor.add(scrollDetails, DockPanel.CENTER);
        editor.setCellWidth(scrollDetails, "100%");
        scrollDetails.add(details);
        onWindowResized(Window.getClientWidth(), Window.getClientHeight());
    }
}