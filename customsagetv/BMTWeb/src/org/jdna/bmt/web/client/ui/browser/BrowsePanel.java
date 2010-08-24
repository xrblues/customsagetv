package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.event.EventBus;
import org.jdna.bmt.web.client.event.WaitingEvent;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.OKDialogHandler;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BrowsePanel extends Composite implements BrowseReplyHandler, BrowserView, ResizeHandler {
    private DockPanel           panel          = new DockPanel();
    private Panel               mainItems      = new FlowPanel();
    private VerticalPanel       sideSource = new VerticalPanel();
    private SourcesPanel        sources        = new SourcesPanel(this);
    private ScansPanel          scans = new ScansPanel(this);
    private SearchPanel         search = new SearchPanel();
    
    private GWTMediaFolder         currentFolder  = null;

    private DockPanel           browser        = new DockPanel();
    private ScrollPanel         browserScroller = new ScrollPanel();
    
    private HorizontalButtonBar     browserActions = new HorizontalButtonBar();
    private Button backButton = null;
    private Button updateMetadataButton = null;
	private Button loadMoreButton = null;

    private HandlerRegistration replyHandler   = null;
    private HandlerRegistration resizeHandler = null;
    private int lastScrollPosition;
    
    private ListBox batchOperations;
    
    public BrowsePanel() {
        super();
        panel.setSpacing(5);
        panel.setWidth("100%");
        sideSource.setWidth("220px");
        sideSource.add(search);
        search.setWidth("100%");
        sideSource.add(sources);
        sources.setWidth("100%");
        
        scans.setVisible(false);
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
                    BrowsingServicesManager.getInstance().browseFolder(currentFolder.getParent(), 0, currentFolder.getPageSize());
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
                        BrowsingServicesManager.getInstance().scan(currentFolder, options);
                    }
                }));
            }
        });
        browserActions.add(updateMetadataButton);

        loadMoreButton = new Button("Load More Items", new ClickHandler() {
            public void onClick(ClickEvent event) {
            	BrowsingServicesManager.getInstance().browseFolder(currentFolder, currentFolder.getLoaded(), currentFolder.getPageSize());
            }
        });
        browserActions.add(loadMoreButton);
        
        batchOperations = new ListBox();
        batchOperations.addItem("-- Batch Operations --", "--");
        batchOperations.addItem("Set Watched", BatchOperation.WATCHED.name());
        batchOperations.addItem("Set UnWatched", BatchOperation.UNWATCHED.name());
        batchOperations.addItem("Set Archived", BatchOperation.ARCHIVE.name());
        batchOperations.addItem("Set UnArchived", BatchOperation.UNARCHIVE.name());
        batchOperations.addItem("Import As Recording", BatchOperation.IMPORTASRECORDING.name());
        batchOperations.addItem("Move to Video Libary (For Recordings)", BatchOperation.UNIMPORTASRECORDING.name());
        batchOperations.addItem("Remove Fanart Files", BatchOperation.CLEANFANART.name());
        batchOperations.addItem("Remove .properties Files", BatchOperation.CLEANPROPERTIES.name());
        batchOperations.addItem("Clear Custom Metadata Fields", BatchOperation.CLEARCUSTOMMETADATA.name());
        batchOperations.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String value = batchOperations.getValue(batchOperations.getSelectedIndex());
				if ("--".equals(value)) return;
				
				final PopupPanel dialog = Dialogs.showWaitingPopup("Applying batch operation...");
				BrowsingServicesManager.getInstance().getServices().applyBatchOperation(currentFolder, BatchOperation.valueOf(value), new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						dialog.hide();
						Application.fireNotification(result);
						batchOperations.setSelectedIndex(0);
						BrowsingServicesManager.getInstance().browseFolder(currentFolder, 0, currentFolder.getPageSize());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						dialog.hide();
						Application.fireErrorEvent("Operation Failed", caught);
						batchOperations.setSelectedIndex(0);
					}
				});
			}
		});
        browserActions.add(batchOperations);
        
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
    	GWTMediaFolder browseableFolder = event.getBrowseableFolder();
        try {
            // if the mainItems widget is not visible, then set it.
            if (mainItems != browserScroller.getWidget()) {
                mainItems.clear();
                browserScroller.setWidget(mainItems);
            }
            
            // clear the view if we starting from scratch
            if (event.getStart()==0) {
            	mainItems.clear();
            }
            
            this.currentFolder = browseableFolder;
            if (browseableFolder == null) {
                Application.fireErrorEvent(Application.messages().failedToBrowseFolder(""));
                return;
            }

            ArrayList<GWTMediaResource> children = browseableFolder.getChildren();
            if (children == null) {
                Application.fireNotification(Application.messages().nothingToShowFor(browseableFolder.getTitle()));
                return;
            }

            if (children.size()>0 && browseableFolder.isAllowActions()) {
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
        
        // hide the load more items, if we don't need it.
        if (currentFolder.getLoaded() == currentFolder.getSize() || currentFolder.getLoaded() < currentFolder.getPageSize()) {
        	loadMoreButton.setVisible(false);
        } else {
        	loadMoreButton.setVisible(true);
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
