package org.jdna.bmt.web.client.ui.scan;

import java.util.Comparator;
import java.util.List;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.app.AppPanel;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.SortedVerticalPanel;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditor extends Composite implements ResizeHandler {
    private final BrowserServiceAsync browserService  = GWT.create(BrowserService.class);

    private static class MediaFileComparator implements Comparator<MediaEditorMediaFileWidget> {
        public int compare(MediaEditorMediaFileWidget o1, MediaEditorMediaFileWidget o2) {
            if (o1==null || o1.getMediaFile().getTitle()==null) return -1;
            if (o2==null || o2.getMediaFile().getTitle()==null) return 1;
            System.out.println("Comparing: " + o1.getMediaFile().getTitle() + "; " + o2.getMediaFile().getTitle() + "; " + o1.getMediaFile().getTitle().compareTo(o2.getMediaFile().getTitle()));
            return o1.getMediaFile().getTitle().compareToIgnoreCase(o2.getMediaFile().getTitle());
        }
    }
    
    private HorizontalPanel           panel           = new HorizontalPanel();
    private DockPanel                 editor          = new DockPanel();

    private VerticalPanel             itemsPanel      = new VerticalPanel();
    private SortedVerticalPanel<MediaEditorMediaFileWidget>       scannedItems    = new SortedVerticalPanel<MediaEditorMediaFileWidget>(new MediaFileComparator());
    private VerticalPanel             itemStatusPanel = new VerticalPanel();

    private VerticalPanel             leftPanel       = new VerticalPanel();

    private ScrollPanel               scrollItems     = null;

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
        panel.setCellWidth(editor, "100%");
        initWidget(panel);

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

        AppPanel.adjustWindowSize();
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

        
        System.out.println("MediaEditor(): onWindowResize(): " + scrollWidth + ";" + scrollHeight);

        panel.setPixelSize(scrollWidth, scrollHeight);

        resizeItemScroller(windowWidth, windowHeight);
        resizeDetailScroller(windowWidth, windowHeight);
    }

    public void resizeDetailScroller(int windowWidth, int windowHeight) {
        if (details != null) {
            int editorWidth = windowWidth - editor.getAbsoluteLeft();
            if (editorWidth < 1) {
                editorWidth = 1;
            }

            int editorHeight = windowHeight - editor.getAbsoluteTop();
            if (editorHeight < 1) {
                editorHeight = 1;
            }

            System.out.println("MediaEditor(): Resize Details: " + editorWidth + "; " + editorHeight);
            
            editor.setPixelSize(editorWidth, editorHeight);
            details.setPixelSize(editorWidth, editorHeight);
            details.adjustSize(windowWidth, windowHeight);
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

        System.out.println("MediaEditor(): Resize List: " + scrollWidth + "; " + scrollHeight);

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
        
        AppPanel.adjustWindowSize();
    }

    protected void updateMediaEditorPanel(final MediaEditorMediaFileWidget item) {
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
                updateEditorPanel(mi, item);
            }
        });
    }

    protected void updateEditorPanel(GWTMediaFile mediaFile, final MediaEditorMediaFileWidget item) {
        editor.clear();
        details = new MediaEditorMetadataPanel(mediaFile);
        details.setWidth("100%");
        editor.add(details, DockPanel.CENTER);
        editor.setCellWidth(details, "100%");
        details.setUpdateListener(new AsyncCallback<GWTMediaFile>() {
            public void onFailure(Throwable caught) {
            }

            public void onSuccess(GWTMediaFile result) {
                item.onUpdate(result);
            }
        });
        
        AppPanel.adjustWindowSize();
    }

    public void onResize(ResizeEvent event) {
        System.out.println("MediaEditor Resize Event: " + event.getWidth() +";"+ event.getHeight());
        onWindowResized(event.getWidth(), event.getHeight());
    }
}