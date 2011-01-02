package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.event.EventBus;
import org.jdna.bmt.web.client.event.WaitingEvent;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.ui.BatchOperation;
import org.jdna.bmt.web.client.ui.BatchOperations;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.OKDialogHandler;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BrowsePanel extends Composite implements BrowseReplyHandler, BrowserView {
    private HorizontalPanel     hpanel          = new HorizontalPanel();
    private Panel               mainItems      = new FlowPanel();
    private VerticalPanel       sideSource = new VerticalPanel();
    private SourcesPanel        sources        = new SourcesPanel(this);
    private ScansPanel          scans = new ScansPanel(this);
    private SearchPanel         search = new SearchPanel();
    
    private GWTMediaFolder         currentFolder  = null;

    private VerticalPanel       vbrowser        = new VerticalPanel();
    private SimplePanel         browserScroller = new SimplePanel();
    
    private HorizontalButtonBar     browserActions = new HorizontalButtonBar();
    private Button backButton = null;
    private Button updateMetadataButton = null;
    private Button refreshFanartButton = null;
	private Button loadMoreButton = null;

    private HandlerRegistration replyHandler   = null;
    private int lastScrollPosition;
    
    private ListBox batchOperations;
    
    public BrowsePanel() {
        super();
        hpanel.setSpacing(5);
        hpanel.setWidth("100%");
        sideSource.setWidth("220px");
        sideSource.add(search);
        search.setWidth("100%");
        sideSource.add(sources);
        sources.setWidth("100%");
        
        scans.setVisible(false);
        sideSource.add(scans);
        scans.setWidth("100%");
        
        hpanel.add(sideSource);
        hpanel.setCellHorizontalAlignment(sideSource, HasHorizontalAlignment.ALIGN_LEFT);
        hpanel.setCellVerticalAlignment(sideSource, HasVerticalAlignment.ALIGN_TOP);
        hpanel.setCellWidth(sideSource, "300px");

        
        // add browse actions
        backButton = new Button("Back", new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (currentFolder.getParent()!=null) {
                    BrowsingServicesManager.getInstance().browseFolder(currentFolder.getParent(), 0, currentFolder.getPageSize());
                }
            }
        });
        browserActions.add(backButton);
        
        updateMetadataButton = new Button("Update Metadata ...", new ClickHandler() {
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

        refreshFanartButton = new Button("Refresh Fanart", new ClickHandler() {
            public void onClick(ClickEvent event) {
            	if (Window.confirm("Press OK to update ALL your fanart for items in this folder, and it's children.")) {
	                final PersistenceOptionsUI options = new PersistenceOptionsUI();
	                options.getScanPath().set(currentFolder);
	                options.getRefresh().set(true);
	                options.getUpdateMetadata().set(false);
                    BrowsingServicesManager.getInstance().scan(currentFolder, options);
            	}
            }
        });
        browserActions.add(refreshFanartButton);
        
        loadMoreButton = new Button("Load More Items", new ClickHandler() {
            public void onClick(ClickEvent event) {
            	BrowsingServicesManager.getInstance().browseFolder(currentFolder, currentFolder.getLoaded(), currentFolder.getPageSize());
            }
        });
        loadMoreButton.addStyleName("LoadMoreButton");
        browserActions.add(loadMoreButton);
        
        batchOperations = new ListBox();
        batchOperations.addItem("-- Batch Operations --", "--");
        
        for (BatchOperation op: BatchOperations.getInstance().getBatchOperations()) {
        	batchOperations.addItem(op.getLabel(), op.getLabel());
        }
        
        batchOperations.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String value = batchOperations.getValue(batchOperations.getSelectedIndex());
				if ("--".equals(value)) return;
				for (BatchOperation bo: BatchOperations.getInstance().getBatchOperations()) {
					if (value.equals(bo.getLabel())) {
						Application.runBatchOperation(currentFolder, bo);
						break;
					}
				}
			}
		});
        
        browserActions.add(batchOperations);
        browserActions.setVisible(false);
        browserScroller.setWidget(mainItems);
        
        vbrowser.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        vbrowser.setWidth("100%");
        vbrowser.setStyleName("BrowsePanel-Actions");
        vbrowser.add(browserActions);
        vbrowser.setCellHeight(browserActions, "10px");
        vbrowser.add(browserScroller);
        
        hpanel.add(vbrowser);
        hpanel.setCellHorizontalAlignment(vbrowser, HasHorizontalAlignment.ALIGN_LEFT);
        hpanel.setCellVerticalAlignment(vbrowser, HasVerticalAlignment.ALIGN_TOP);
        hpanel.setCellWidth(vbrowser, "100%");

        hpanel.setStyleName("BrowsePanel");

        initWidget(hpanel);
    }
    
    @Override
    protected void onAttach() {
        replyHandler = Application.events().addHandler(BrowseReplyEvent.TYPE, this);
        super.onAttach();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        replyHandler.removeHandler();
    }

    public void onBrowseReply(BrowseReplyEvent event) {
    	GWTMediaFolder browseableFolder = event.getBrowseableFolder();
        try {
        	if (browserScroller.getWidget() != mainItems) {
        		browserScroller.setWidget(mainItems);
        	}
    		mainItems.clear();

            
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

    public void setDisplay(Widget w) {
        // save the scroll position
        lastScrollPosition = Window.getScrollTop();
        
        setActionsVisible(false);
        Panel  p = new FlowPanel();
        p.add(w);
        browserScroller.setWidget(p);
    }

    public void setActionsVisible(boolean b) {
        browserActions.setVisible(b);
    }

    public void back() {
        // just restore the mainitems
        setActionsVisible(true);
        browserScroller.setWidget(mainItems);
        
        Window.scrollTo(0, lastScrollPosition);
    }
}
