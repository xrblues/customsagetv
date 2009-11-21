package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.event.EventBus;
import org.jdna.bmt.web.client.event.WaitingEvent;
import org.jdna.bmt.web.client.ui.app.ErrorEvent;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class BrowsePanel extends Composite implements BrowseReplyHandler, HasFolder, ResizeHandler {
    private DockPanel           panel          = new DockPanel();
    private FlowPanel           mainItems      = new FlowPanel();
    private SourcesPanel        sources        = new SourcesPanel(this);
    private MediaFolder         currentFolder  = null;

    private DockPanel           browser        = new DockPanel();
    private ScrollPanel         browserScroller = new ScrollPanel();
    
    private HorizontalButtonBar     browserActions = new HorizontalButtonBar();

    private HandlerRegistration replyHandler   = null;
    private HandlerRegistration resizeHandler = null;
    
    public BrowsePanel() {
        super();
        panel.setSpacing(5);
        panel.setWidth("100%");
        sources.setWidth("220px");
        panel.add(sources, DockPanel.WEST);
        panel.setCellHorizontalAlignment(sources, HasHorizontalAlignment.ALIGN_LEFT);
        panel.setCellVerticalAlignment(sources, HasVerticalAlignment.ALIGN_TOP);
        panel.setCellWidth(sources, "300px");

        
        // add browse actions
        browserActions.add(new Button("Back", new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (currentFolder.getParent()!=null) {
                    BrowsingServicesManager.getInstance().browseFolder(currentFolder.getParent());
                }
            }
        }));
        //browserActions.addItem(new Button("Find Metadata"));
        //browserActions.addItem(new Button("Set All Watched"));
        browserActions.setVisible(false);
        
        browserScroller.setWidth("100%");
        browserScroller.setHeight("100%");
        browserScroller.setAlwaysShowScrollBars(false);
        browserScroller.setWidget(mainItems);
        
        browser.setWidth("100%");
        browser.setStyleName("BrowsePanel-Actions");
        browser.add(browserActions, DockPanel.NORTH);
        browser.add(browserScroller, DockPanel.CENTER);

        
        
        // new Action("Scan Folder", "Action-ScanFolder",
        // ScanFolderEvent(path));
        // new Action("Set Watched", "Action-SetWatched",
        // SetWatchedEvent(path));

        panel.add(browser, DockPanel.CENTER);
        panel.setCellHorizontalAlignment(browser, HasHorizontalAlignment.ALIGN_LEFT);
        panel.setCellVerticalAlignment(browser, HasVerticalAlignment.ALIGN_TOP);
        panel.setCellWidth(browser, "100%");

        panel.setStyleName("BrowsePanel");

        initWidget(panel);
    }

    @Override
    protected void onAttach() {
        System.out.println("Attaching Browse Reply Event Handler");
        replyHandler = Application.events().addHandler(BrowseReplyEvent.TYPE, this);
        resizeHandler = Window.addResizeHandler(this);
        super.onAttach();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        replyHandler.removeHandler();
        resizeHandler.removeHandler();
        System.out.println("Detaching Browse Reply Event Handler");
    }

    public void onBrowseReply(BrowseReplyEvent event) {
        updateViewForFolder(event.getBrowseableFolder());
    }

    private void updateViewForFolder(MediaFolder browseableFolder) {
        try {
            this.currentFolder = browseableFolder;
            if (browseableFolder == null) {
                Application.events().fireEvent(new ErrorEvent(Application.messages().failedToBrowseFolder("")));
                return;
            }

            mainItems.clear();
            MediaResource[] children = browseableFolder.getChildren();
            if (children == null) {
                Application.events().fireEvent(new ErrorEvent(Application.messages().failedToBrowseFolder(browseableFolder.getTitle())));
                return;
            }

            if (children.length>0) {
                browserActions.setVisible(true);
            } else {
                browserActions.setVisible(false);
            }
            
            //if (browseableFolder.getParent() != null) {
            //    mainItems.add(new MediaItem(new BackMediaFolder(browseableFolder.getParent())));
            //}

            for (MediaResource r : children) {
                MediaItem mi = new MediaItem(r);
                mi.setHeight("250px");
                mainItems.add(mi);
            }
            
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    ResizeEvent evt = new ResizeEvent(Window.getClientWidth(), Window.getClientHeight()) {
                    };
                    onResize(evt);
                }
            });
        } finally {
            // notify handlers that we are no longer waiting for data
            EventBus.getHandlerManager().fireEvent(new WaitingEvent(browseableFolder.getResourceRef(), false));
        }
    }

    public MediaFolder getFolder() {
        return currentFolder;
    }

    public void onResize(ResizeEvent event) {
        onWindowResized(event.getWidth(), event.getHeight());
    }
    
    public void onWindowResized(int windowWidth, int windowHeight) {
        int scrollWidth = windowWidth - browserScroller.getAbsoluteLeft();
        if (scrollWidth < 1) {
            scrollWidth = 1;
        }

        int scrollHeight = windowHeight - browserScroller.getAbsoluteTop();
        if (scrollHeight < 1) {
            scrollHeight = 1;
        }
        browserScroller.setPixelSize(scrollWidth, scrollHeight);
    }
}
