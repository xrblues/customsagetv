package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.event.EventBus;
import org.jdna.bmt.web.client.event.WaitingEvent;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.ui.app.ErrorEvent;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.OKDialogHandler;

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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BrowsePanel extends Composite implements BrowseReplyHandler, BrowserView, ResizeHandler {
    private DockPanel           panel          = new DockPanel();
    private Panel               mainItems      = new FlowPanel();
    private VerticalPanel       sideSource = new VerticalPanel();
    private SourcesPanel        sources        = new SourcesPanel(this);
    private ScansPanel          scans = new ScansPanel(this);
    
    private GWTMediaFolder         currentFolder  = null;

    private DockPanel           browser        = new DockPanel();
    private ScrollPanel         browserScroller = new ScrollPanel();
    
    private HorizontalButtonBar     browserActions = new HorizontalButtonBar();
    private Button backButton = null;
    private Button updateMetadataButton = null;

    private HandlerRegistration replyHandler   = null;
    private HandlerRegistration resizeHandler = null;
    private int lastScrollPosition;
    
    public BrowsePanel() {
        super();
        panel.setSpacing(5);
        panel.setWidth("100%");
        sideSource.setWidth("220px");
        sideSource.add(sources);
        sources.setWidth("100%");
        sideSource.add(scans);
        scans.setWidth("100%");
        panel.add(sideSource, DockPanel.WEST);
        panel.setCellHorizontalAlignment(sideSource, HasHorizontalAlignment.ALIGN_LEFT);
        panel.setCellVerticalAlignment(sideSource, HasVerticalAlignment.ALIGN_TOP);
        panel.setCellWidth(sideSource, "300px");

        
        // add browse actions
        backButton = new Button("Back", new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (currentFolder.getParent()!=null) {
                    BrowsingServicesManager.getInstance().browseFolder(currentFolder.getParent());
                }
            }
        });
        browserActions.add(backButton);
        
        updateMetadataButton = new Button("Update Metadata", new ClickHandler() {
            public void onClick(ClickEvent event) {
                final PersistenceOptionsUI options = new PersistenceOptionsUI();
                options.getScanPath().set(currentFolder);
                DataDialog.showDialog(new ScanOptionsPanel(options, new OKDialogHandler<PersistenceOptionsUI>() {
                    public void onSave(PersistenceOptionsUI data) {
                        MetadataServicesManager.getInstance().scan(currentFolder, options);
                    }
                }));
            }
        });
        browserActions.add(updateMetadataButton);
        
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

    private void updateViewForFolder(GWTMediaFolder browseableFolder) {
        try {
            // if the mainItems widget is not visible, then set it.
            if (mainItems != browserScroller.getWidget()) {
                mainItems.clear();
                browserScroller.setWidget(mainItems);
            }
            this.currentFolder = browseableFolder;
            if (browseableFolder == null) {
                Application.events().fireEvent(new ErrorEvent(Application.messages().failedToBrowseFolder("")));
                return;
            }

            mainItems.clear();
            GWTMediaResource[] children = browseableFolder.getChildren();
            if (children == null) {
                Application.events().fireEvent(new ErrorEvent(Application.messages().failedToBrowseFolder(browseableFolder.getTitle())));
                return;
            }

            if (children.length>0 && browseableFolder.isAllowActions()) {
                browserActions.setVisible(true);
            } else {
                browserActions.setVisible(false);
            }
            
            for (GWTMediaResource r : children) {
                MediaItem mi = new MediaItem(r, this);
                mi.setHeight("250px");
                mainItems.add(mi);
            }
            
            resize();
        } finally {
            // notify handlers that we are no longer waiting for data
            EventBus.getHandlerManager().fireEvent(new WaitingEvent(browseableFolder.getResourceRef(), false));
        }
    }

    public GWTMediaFolder getFolder() {
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

    public void setDisplay(Widget w) {
        // save the scroll position
        lastScrollPosition = browserScroller.getScrollPosition();
        
        setActionsVisible(false);
        Panel  p = new FlowPanel();
        p.add(w);
        browserScroller.setWidget(p);
        
        resize();
    }

    public void setActionsVisible(boolean b) {
        browserActions.setVisible(b);
    }

    public void back() {
        // just restore the mainitems
        setActionsVisible(true);
        browserScroller.setWidget(mainItems);
        browserScroller.setScrollPosition(lastScrollPosition);
        resize();
    }
    
    private void resize() {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                ResizeEvent evt = new ResizeEvent(Window.getClientWidth(), Window.getClientHeight()) {
                    // no body
                };
                onResize(evt);
            }
        });
    }
}
