package org.jdna.bmt.web.client.ui.browser;

import java.util.List;

import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;
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
import com.google.gwt.user.client.ui.DecoratedTabBar;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MediaEditor extends Composite {
    private final BrowserServiceAsync browserService = GWT.create(BrowserService.class);

    private MediaResult[] items = null;

    private HorizontalPanel panel = new HorizontalPanel();
    private DockPanel editor = new DockPanel();
    
    private VerticalPanel successPanel = new VerticalPanel();
    private VerticalPanel successItems = new VerticalPanel();
    private VerticalPanel actionPanel = new VerticalPanel();
    
    private VerticalPanel stackPanel = new VerticalPanel();
    private VerticalPanel failedItems = new VerticalPanel();
    private VerticalPanel failedPanel = new VerticalPanel();

    private ScrollPanel scrollSuccessItems = null;
    private ScrollPanel scrollDetails = null;

    private TabBar itemTabs = new DecoratedTabBar();
    
    private MediaEditorPanel details = null;

    

    private String progressScanId;
    private Timer timer = null;
    
    private int successCount = 0;
    
    private ProgressPanel statusIndicator = new ProgressPanel();
    
    public MediaEditor(String progressId) {
        this.progressScanId = progressId;

        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.add(stackPanel);
        
        stackPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        stackPanel.setWidth("300px");
        //stackPanel.setHeight("100%");

        actionPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        actionPanel.add(statusIndicator);
        
        itemTabs.addTab("Success");
        itemTabs.addTab("Failed");
        itemTabs.selectTab(0);
        
        actionPanel.add(itemTabs);
        
        stackPanel.add(actionPanel);
        stackPanel.setCellHeight(actionPanel, "20px");
        actionPanel.setWidth("100%");
        statusIndicator.setWidth("100%");
        
        stackPanel.add(itemTabs);
        stackPanel.setCellHeight(itemTabs, "20px");
        
        stackPanel.add(successPanel);
        
        successPanel.setHeight("100%");
        successPanel.setWidth("100%");
        
        scrollSuccessItems = new ScrollPanel(successItems);
        scrollSuccessItems.setWidth("300px");
        scrollSuccessItems.setHeight("100%");
        successPanel.add(scrollSuccessItems);
        successItems.setWidth("300px");

        stackPanel.setCellHeight(successPanel, "100%");
        
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
                        Log.error("Failed to udpate status");
                        timer.cancel();
                        actionPanel.remove(statusIndicator);
                    }

                    public void onSuccess(ProgressStatus result) {
                        if (result!=null) {
                            if (result.isCancelled()) timer.cancel();
                            if (result.isDone() && result.getItems().size()==0) {
                                timer.cancel();
                                actionPanel.remove(statusIndicator);
                            }
                            statusIndicator.updateProgress(result);
                            addResults(result.getItems());
                        } else {
                            timer.cancel();
                            Log.error("Failed to get progress results");
                        }
                    }
                });
            }
        };
        timer.scheduleRepeating(400);
        
        onWindowResized(Window.getClientWidth(), Window.getClientHeight());
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
        if (scrollDetails!=null) {
            int scrollWidth = windowWidth - scrollDetails.getAbsoluteLeft()-9;
            if (scrollWidth < 1) {
              scrollWidth = 1;
            }
    
            System.out.println("Window Height: " + windowHeight);
            System.out.println("Abs Top: " + scrollDetails.getAbsoluteTop());
            System.out.println("Height: " + (windowHeight - scrollDetails.getAbsoluteTop()));
            int scrollHeight = windowHeight - scrollDetails.getAbsoluteTop();
            if (scrollHeight < 1) {
              scrollHeight = 1;
            }
            
            if (details!=null) {
                details.setWidth(scrollWidth + "px");
            }
            
            //scrollDetails.setPixelSize(scrollWidth, scrollHeight);
            scrollDetails.setHeight(scrollHeight+"px");
        }
    }
    
    public void resizeItemScroller(int windowWidth, int windowHeight) {
        int scrollHeight = windowHeight - scrollSuccessItems.getAbsoluteTop();
        if (scrollHeight < 1) {
          scrollHeight = 1;
        }
        //scrollSuccessItems.setPixelSize(stackPanel.getOffsetWidth(), scrollHeight);
        scrollSuccessItems.setHeight(scrollHeight+"px");
    }
    
    protected void addResults(List<MediaResult> items) {
        if (items!=null) {
            successCount += items.size();
            itemTabs.setTabText(0, "Success (" + successCount + ")");
            for (int i=0;i<items.size();i++) {
                final MediaEditorWidget item = new MediaEditorWidget(items.get(i));
                item.setWidth("100%");
                item.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        updateMediaEditorPanel(item);
                    }
                });
                successItems.add(item);
            }
        }
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                onWindowResized(Window.getClientWidth(), Window.getClientHeight());
            }
        });
    }
    
    protected void updateMediaEditorPanel(MediaEditorWidget item) {
        MediaResult mi = item.getMediaItem();
        final PopupPanel waiting = Dialogs.showWaitingPopup("Getting details for " + mi.getMediaTitle());
        browserService.getMediaItem(mi, new AsyncCallback<MediaItem>() {
            public void onFailure(Throwable caught) {
                Dialogs.hidePopup(waiting, 1000);
                editor.clear();
                Log.error("Failed to load metadata.", caught);
            }
            public void onSuccess(MediaItem result) {
                Dialogs.hidePopup(waiting, 1000);
                updateEditorPanel(result);
            }
        });
    }

    protected void updateEditorPanel(MediaItem result) {
        editor.clear();
        details = new MediaEditorPanel(result);
        scrollDetails = new ScrollPanel(details);
        scrollDetails.setWidth("100%");
        scrollDetails.setHeight("100%");
        editor.add(scrollDetails, DockPanel.CENTER);
        editor.setCellWidth(scrollDetails, "100%");
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                onWindowResized(Window.getClientWidth(), Window.getClientHeight());
            }
        });
    }
}