package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.event.EventBus;
import org.jdna.bmt.web.client.event.WaitingEvent;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.media.GWTViewCategories;
import org.jdna.bmt.web.client.ui.BatchOperation;
import org.jdna.bmt.web.client.ui.BatchOperations;
import org.jdna.bmt.web.client.ui.input.NVP;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.OKDialogHandler;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.util.Log;
import org.jdna.bmt.web.client.util.MessageBus;
import org.jdna.bmt.web.client.util.MessageHandler;
import org.jdna.bmt.web.client.util.StringUtils;

import sagex.phoenix.metadata.MediaArtifactType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BrowsePanel extends Composite implements BrowserView, ValueChangeHandler<String>, MessageHandler {
	public static final String MSG_PROGRESS_UPDATED = "progressupdated";
	public static final String MSG_NEW_SCAN_STARTED = "newscanstarted";
	public static final String MSG_METADATA_CHANGED = "metadatachanged";
	public static final String MSG_RECORDING_ADDED = "recording_added";
	public static final String MSG_FILE_WATCHED = "filewatched";
	public static final String MSG_POSTER_UPDATED = "posterupdated";
	
	public static final String MSG_HIDE_VIEWS = "hide-views";
	public static final String MSG_SHOW_VIEWS = "show-views";

	private static BrowsingServiceAsync browser = GWT
			.create(BrowsingService.class);

	private HorizontalPanel hpanel = new HorizontalPanel();
	private Panel mainItems = new FlowPanel();
	private VerticalPanel sideSource = new VerticalPanel();
	// private SourcesPanel sources = new SourcesPanel(this);
	private ScansPanel scans = new ScansPanel(this);
	private SearchPanel search = new SearchPanel(this);

	private GWTMediaFolder currentFolder = null;

	private VerticalPanel vbrowser = new VerticalPanel();
	private SimplePanel browserScroller = new SimplePanel();
	private BrowserHeaderPanel headerPanel = new BrowserHeaderPanel();

	private HorizontalButtonBar browserActions = new HorizontalButtonBar();
	private Button backButton = null;
	private Button updateMetadataButton = null;
	private Button refreshFanartButton = null;
	private Button loadMoreButton = null;

	private int lastScrollPosition;

	private MessageBus messageBus = new MessageBus();

	private ListBox batchOperations;
	private Button batchUpdate;
	private HandlerRegistration historyHandler;
	private GWTView currentView;

	public BrowsePanel(List<String> paths) {
		super();
		hpanel.setSpacing(5);
		hpanel.setWidth("100%");
		sideSource.setWidth("220px");
		sideSource.add(search);
		search.setWidth("100%");

		ViewsListView sources = new ViewsListView(this);

		sideSource.add(sources);
		sources.setWidth("100%");

		scans.setVisible(false);
		sideSource.add(scans);
		scans.setWidth("100%");

		hpanel.add(sideSource);
		hpanel.setCellHorizontalAlignment(sideSource,
				HasHorizontalAlignment.ALIGN_LEFT);
		hpanel.setCellVerticalAlignment(sideSource,
				HasVerticalAlignment.ALIGN_TOP);
		hpanel.setCellWidth(sideSource, "300px");

		// add browse actions
		backButton = new Button("Back", new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (currentFolder.getParent() != null) {
					browseFolder(currentFolder.getParent(), 0,
							currentFolder.getPageSize());
				}
			}
		});
		browserActions.add(backButton);

		updateMetadataButton = new Button("Update Metadata ...",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						final PersistenceOptionsUI options = new PersistenceOptionsUI();
						options.getScanPath().set(currentFolder);
						DataDialog.showDialog(new ScanOptionsPanel(options,
								new OKDialogHandler<PersistenceOptionsUI>() {
									public void onSave(PersistenceOptionsUI data) {
										scan(currentFolder, options);
									}
								}));
					}
				});
		browserActions.add(updateMetadataButton);

		refreshFanartButton = new Button("Refresh Fanart", new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (Window
						.confirm("Press OK to update ALL your fanart for items in this folder, and it's children.")) {
					final PersistenceOptionsUI options = new PersistenceOptionsUI();
					options.getScanPath().set(currentFolder);
					options.getRefresh().set(true);
					options.getUpdateMetadata().set(false);
					scan(currentFolder, options);
				}
			}
		});
		browserActions.add(refreshFanartButton);

		loadMoreButton = new Button("Load More Items", new ClickHandler() {
			public void onClick(ClickEvent event) {
				browseFolder(currentFolder, currentFolder.getLoaded(),
						currentFolder.getPageSize());
			}
		});
		loadMoreButton.addStyleName("LoadMoreButton");
		browserActions.add(loadMoreButton);

		batchOperations = new ListBox();
		batchOperations.addItem("-- Batch Operations --", "--");

		for (BatchOperation op : BatchOperations.getInstance()
				.getBatchOperations()) {
			batchOperations.addItem(op.getLabel(), op.getLabel());
		}

		batchOperations.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String value = batchOperations.getValue(batchOperations
						.getSelectedIndex());
				if ("--".equals(value))
					return;
				for (BatchOperation bo : BatchOperations.getInstance()
						.getBatchOperations()) {
					if (value.equals(bo.getLabel())) {
						Application.runBatchOperation(currentFolder, bo);
						batchOperations.setSelectedIndex(0);
						break;
					}
				}
			}
		});

		browserActions.add(batchOperations);
		setActionsVisible(false);

		batchUpdate = new Button("Batch Update", new ClickHandler() {
			public void onClick(ClickEvent event) {
				editraw(currentFolder);
			}
		});
		
		browserActions.add(batchUpdate);

		browserScroller.setWidget(mainItems);

		vbrowser.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		vbrowser.setWidth("100%");
		vbrowser.setStyleName("BrowsePanel-Actions");
		vbrowser.add(browserActions);
		vbrowser.setCellHeight(browserActions, "10px");
		vbrowser.add(headerPanel);
		vbrowser.add(browserScroller);

		hpanel.add(vbrowser);
		hpanel.setCellHorizontalAlignment(vbrowser,
				HasHorizontalAlignment.ALIGN_LEFT);
		hpanel.setCellVerticalAlignment(vbrowser,
				HasVerticalAlignment.ALIGN_TOP);
		hpanel.setCellWidth(vbrowser, "100%");

		hpanel.setStyleName("BrowsePanel");

		initWidget(hpanel);
		setParams(paths);
		
		messageBus.addHandler(MSG_SHOW_VIEWS, new MessageHandler() {
			@Override
			public void onMessageReceived(String msg, Map<String, ?> args) {
				sideSource.setVisible(true);
			}
		});
		
		messageBus.addHandler(MSG_HIDE_VIEWS, new MessageHandler() {
			@Override
			public void onMessageReceived(String msg, Map<String, ?> args) {
				sideSource.setVisible(false);
			}
		});
	}

	public void onBrowseReply(GWTMediaFolder browseableFolder, int start,
			int size) {
		try {
			if (browserScroller.getWidget() != mainItems) {
				browserScroller.setWidget(mainItems);
			}
			mainItems.clear();

			// clear the view if we starting from scratch
			if (start == 0) {
				mainItems.clear();
			}

			this.currentFolder = browseableFolder;
			if (browseableFolder == null) {
				Application.fireErrorEvent(Application.messages()
						.failedToBrowseFolder(""));
				return;
			}

			ArrayList<GWTMediaResource> children = browseableFolder
					.getChildren();
			if (children == null) {
				Application.fireNotification(Application.messages()
						.nothingToShowFor(browseableFolder.getTitle()));
				return;
			}

			if (children.size() > 0 && browseableFolder.isAllowActions()) {
				setActionsVisible(true);
			} else {
				setActionsVisible(false);
			}

			for (GWTMediaResource r : children) {
				MediaItem mi = new MediaItem(r, this);
				mi.setHeight("250px");
				mainItems.add(mi);
			}

		} finally {
			// notify handlers that we are no longer waiting for data
			EventBus.getHandlerManager().fireEvent(
					new WaitingEvent(browseableFolder.getResourceRef(), false));
		}

		// hide the load more items, if we don't need it.
		if (currentFolder.getLoaded() == currentFolder.getSize()
				|| currentFolder.getLoaded() < currentFolder.getPageSize()) {
			loadMoreButton.setVisible(false);
		} else {
			loadMoreButton.setVisible(true);
		}

		// reset the window
		Window.scrollTo(0, 0);
		
		headerPanel.update(currentFolder);
	}

	public GWTMediaFolder getFolder() {
		return currentFolder;
	}

	public void setDisplay(Widget w) {
		// save the scroll position
		lastScrollPosition = Window.getScrollTop();

		setActionsVisible(false);
		Panel p = new FlowPanel();
		p.add(w);
		browserScroller.setWidget(p);
	}

	public void setActionsVisible(boolean b) {
		browserActions.setVisible(b);
		headerPanel.setVisible(b);
	}

	public void back() {
		// just restore the mainitems
		setActionsVisible(true);

		// restore the side panel
		sideSource.setVisible(true);
		
		browserScroller.setWidget(mainItems);

		Window.scrollTo(0, lastScrollPosition);
	}

	public void browseFolder(final GWTMediaFolder folder, final int start,
			final int pageSize) {
		// if the folder has children, then use them
		if (folder.isLoaded(start, pageSize)) {
			onBrowseReply(folder, start, pageSize);
			return;
		}

		final PopupPanel dialog = Dialogs.showWaitingPopup("Loading...");
		browser.browseChildren(folder, start, pageSize,
				new AsyncCallback<GWTMediaResource[]>() {
					public void onFailure(Throwable caught) {
						dialog.hide();
						Application.fireErrorEvent(Application.messages()
								.failedToBrowseFolder(folder.getTitle()),
								caught);
					}

					public void onSuccess(GWTMediaResource[] result) {
						folder.addChildren(result);
						onBrowseReply(folder, start, pageSize);
						dialog.hide();
						if (result == null || result.length == 0) {
							Application.fireErrorEvent(Application.messages()
									.nothingToShowFor(folder.getTitle()));
							return;
						}
					}
				});
	}

	public BrowsingServiceAsync getServices() {
		return browser;
	}

	public void getView(final GWTView view) {
		currentView = view;
		History.newItem("viewitem", false);
		
		final PopupPanel panel = Dialogs
				.showWaitingPopup("Loading view items....");
		Log.debug("Browse View: " + view.getLabel());
		browser.getView(view, new AsyncCallback<GWTMediaFolder>() {
			public void onFailure(Throwable caught) {
				panel.hide();
				Application.fireErrorEvent(Application.messages()
						.failedToBrowseSource(view.getLabel()), caught);
			}

			public void onSuccess(GWTMediaFolder result) {
				panel.hide();
				if (result == null) {
					onFailure(new Exception(
							"Server Replied with no information"));
				} else {
					browseFolder(result, 0, result.getPageSize());
				}
			}
		});
	}

	public void scan(final GWTMediaFolder folder,
			final PersistenceOptionsUI options) {
		browser.scan(folder, options, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent(
						Application.messages().failedToScan(folder.getTitle()),
						caught);
			}

			public void onSuccess(String progressId) {
				// notify listeners about the scan
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("folder", folder);
				args.put("options", options);
				args.put("progressid", progressId);
				messageBus.postMessage(MSG_NEW_SCAN_STARTED, args);

				// show the scans panel
				scans.setVisible(true);

				// Create a process that will monitor the progress and send
				// event updates
				monitorProgress(progressId);
			}
		});
	}

	public void monitorProgress(final String progressId) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				browser.getStatus(progressId,
						new AsyncCallback<ProgressStatus>() {
							public void onFailure(Throwable caught) {
								Application.fireErrorEvent("Scan Failed: "
										+ caught.getMessage());
								cancel();

								ProgressStatus result = new ProgressStatus();
								result.setProgressId(progressId);
								result.setIsDone(true);
								result.setStatus(caught.getMessage());

								// notify listeners about the update
								progressUpdated(result);
							}

							public void onSuccess(ProgressStatus result) {
								if (result == null) {
									// if there is no result, then set this to
									// done
									result = new ProgressStatus();
									result.setProgressId(progressId);
									result.setIsDone(true);
								}

								if (result.isCancelled() || result.isDone()) {
									cancel();
								}

								// notify listeners about the update
								progressUpdated(result);
							}
						});
			}
		};
		timer.scheduleRepeating(400);
	}

	/**
	 * on reply, sends a message to all listeners about the progress status
	 * 
	 * @param progressId
	 */
	public void requestScanProgress(String progressId) {
		browser.getStatus(progressId, new AsyncCallback<ProgressStatus>() {
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to get Scan progress",
						caught);
			}

			public void onSuccess(ProgressStatus result) {
				Map<String, Object> args = new HashMap<String, Object>();
				args.put(MSG_PROGRESS_UPDATED, result);
				messageBus.postMessage(MSG_PROGRESS_UPDATED, args);
			}
		});
	}

	public void requestItemsForProgress(String progressId, final boolean b) {
		browser.getProgressItems(progressId, b,
				new AsyncCallback<GWTMediaResource[]>() {
					public void onFailure(Throwable caught) {
					}

					public void onSuccess(GWTMediaResource[] result) {
						GWTMediaFolder folder = new GWTMediaFolder(null,
								(b) ? "Success Items" : "Failed Items",
								result.length);
						folder.addChildren(result);
						// disable actions on these folders
						folder.setAllowActions(false);
						onBrowseReply(folder, 0, result.length);
					}
				});
	}

	public void cancelScan(String progressId) {
		browser.cancelScan(progressId, new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Void result) {
			}
		});
	}

	public void requestScansInProgress(final HasInProgressStatuses status) {
		browser.getScansInProgress(new AsyncCallback<ProgressStatus[]>() {
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to get Scans in progress",
						caught);
			}

			public void onSuccess(ProgressStatus[] result) {
				if (result != null && result.length > 0) {
					scans.setVisible(true);
					status.setInProgress(result);

					for (ProgressStatus p : result) {
						progressUpdated(p);

						if (!(p.isCancelled() || p.isDone())) {
							// monitor the progress
							monitorProgress(p.getProgressId());
						}
					}
				}
			}
		});
	}

	public void removeScan(String progressId) {
		browser.removeScan(progressId, new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Void result) {
			}
		});
	}

	public void requestUpdatedMetadata(final GWTMediaFile file,
			final HasMediaFile hasMediaFile) {
		browser.loadMetadata(file, new AsyncCallback<GWTMediaMetadata>() {
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to get metadata for item: "
						+ file.getTitle(), caught);
			}

			public void onSuccess(GWTMediaMetadata result) {
				file.attachMetadata(result);
				hasMediaFile.setMediaFile(file);
			}
		});
	}

	public void saveMetadata(final GWTMediaFile file, PersistenceOptionsUI options, final HasMediaFile hasMediaFile) {
		saveMetadata(file, options, hasMediaFile, false);
	}
	
	public void saveMetadata(final GWTMediaFile file, PersistenceOptionsUI options, final HasMediaFile hasMediaFile, final boolean background) {
		// Sage Bug: ExternalID cannot be null
		if (file.getMetadata() != null) {
			if (!file.isAiring()) {
				org.jdna.bmt.web.client.util.Property<String> prop = file
						.getMetadata().getExternalID();
				if (prop != null && StringUtils.isEmpty(prop.get())) {
					Application.fireErrorEvent("ExternalId Cannot be blank!");
					return;
				}
			}
		}

		final PopupPanel save = Dialogs.showWaitingPopup("Saving...");
		browser.saveMetadata(file, options,
				new AsyncCallback<ServiceReply<GWTMediaFile>>() {
					public void onFailure(Throwable caught) {
						save.hide();
						Application.fireErrorEvent(
								"Failed to save metadata for item: "
										+ file.getTitle(), caught);
					}

					public void onSuccess(ServiceReply<GWTMediaFile> result) {
						save.hide();
						if (result == null || result.getCode() > 0) {
							if (result == null) {
								Application.fireErrorEvent(
										"Failed to save metadata for item: "
												+ file.getTitle(), null);
							} else {
								Application.fireErrorEvent(result.getMessage(),
										null);
							}
						} else {
							if (!background) {
								hasMediaFile.setMediaFile(result.getData());
							}
						}
					}
				});
	}

	public void loadFanart(GWTMediaFile file, MediaArtifactType type,
			AsyncCallback<ArrayList<GWTMediaArt>> callback) {
		browser.getFanart(file, type, callback);
	}

	public void downloadFanart(GWTMediaFile file, MediaArtifactType type,
			GWTMediaArt ma, AsyncCallback<ServiceReply<GWTMediaArt>> callback) {
		browser.downloadFanart(file, type, ma, callback);
	}

	/**
	 * MessageBus used to communicate with panel components
	 * 
	 * @return
	 */
	public MessageBus getMessageBus() {
		return messageBus;
	}

	/**
	 * sends a message with the updated/changed metadata
	 * 
	 * @param file
	 */
	public void metadataUpdated(GWTMediaFile file) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("mediafile", file);
		messageBus.postMessage(MSG_METADATA_CHANGED, args);
	}

	/**
	 * sends a message with the updated/changed progress
	 * 
	 * @param file
	 */
	public void progressUpdated(ProgressStatus status) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(MSG_PROGRESS_UPDATED, status);
		messageBus.postMessage(MSG_PROGRESS_UPDATED, args);
	}

	public void getViewCategories(final HasViewCategories hasViewCats) {
		browser.getViewCategories(new AsyncCallback<ArrayList<GWTView>>() {
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to get view categories",
						caught);
			}

			@Override
			public void onSuccess(ArrayList<GWTView> result) {
				hasViewCats.setViewCategories(result);
			}
		});
	}

	public void getViews(final String tag, final HasViews hasViews) {
		if (browser == null) {
			throw new RuntimeException("** Browser is null");
		}

		browser.getViews(tag, new AsyncCallback<GWTViewCategories>() {
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to get views for category "
						+ tag, caught);
			}

			@Override
			public void onSuccess(GWTViewCategories result) {
				hasViews.setViews(result);
			}
		});
	}

	public void showViewThumbnails(GWTMediaFile res) {
		setDisplay(new VideoThumbnailsPanel(res, this));
	}

	public void setParams(List<String> paths) {
		if (paths.size() > 0) {
			// load the view
			if ("view".equals(paths.get(0))) {
				GWT.log("Loading View: " + paths.get(1));
				// viewing a view
				GWTView view = new GWTView(paths.get(1), "view to load");
				getView(view);
			}
		}
	}

	public void record(final GWTMediaFile file) {
		getServices().record(file, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to record", caught);
			}

			@Override
			public void onSuccess(String result) {
				Map<String,Object> args = new HashMap<String, Object>();
				args.put("file", file);
				messageBus.postMessage(MSG_RECORDING_ADDED, args);
				Application.fireNotification(result);
			}
		});
	}

	public void setWatched(final GWTMediaFile file, final boolean watched) {
		getServices().setWatched(file, watched, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Unable to set watched", caught);
			}

			@Override
			public void onSuccess(Void result) {
				Map<String,Object> args = new HashMap<String, Object>();
				args.put("file", file);
				args.put("watched", watched);
				messageBus.postMessage(MSG_FILE_WATCHED, args);
			}
		});
	}

	public void view(GWTMediaResource res) {
		setDisplay(new ViewAiringItemDetails(
				(GWTMediaFile) res, this));
	}

	public void edit(GWTMediaResource res) {
		setDisplay(new MediaEditorMetadataPanel(
				(GWTMediaFile) res, this));
	}

	public void editraw(GWTMediaResource res) {
		setDisplay(new SimpleBatchMetadataEditor(res, BrowsePanel.this));		
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		historyHandler = History.addValueChangeHandler(this);
		Application.getMessagebus().addHandler(Application.MSG_REQUEST_CURRENT_VIEW_INFO, this);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		historyHandler.removeHandler();
		Application.getMessagebus().removeHandler(Application.MSG_REQUEST_CURRENT_VIEW_INFO, this);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if (event.getValue()!=null) {
				back();
		}
	}

	public void addmatcher(GWTMediaFile mediaFile) {
		Dialogs.show(new AddMediaTitleDialogPanel(mediaFile));
	}

	public void next(GWTMediaResource res) {
		GWTMediaResource next = currentFolder.next(res);
		if (next!=null && next instanceof GWTMediaFile) {
			GWTMediaFile file = (GWTMediaFile) next;
			if (file.isAiring()) {
				view(next);
			} else {
				edit(next);
			}
		} else {
			Application.fireNotification("No next item");
		}
	}

	public void loadFanartFiles(GWTMediaFile file, AsyncCallback<ArrayList<String>> asyncCallback) {
		browser.getFanartFiles(file, asyncCallback);
	}

	@Override
	public void onMessageReceived(String msg, Map<String, ?> args) {
		if (Application.MSG_REQUEST_CURRENT_VIEW_INFO.equals(msg)) {
			if (currentView!=null) {
				Application.getMessagebus().postMessage(Application.MSG_RESPONSE_CURRENT_VIEW_INFO, new NVP<String>(Application.PARAM_MSG_RESPONSE_CURRENT_VIEW_PATH, currentFolder.getPath()), new NVP<String>(Application.PARAM_MSG_RESPONSE_CURRENT_VIEW_VIEWNAME, currentView.getId()));
			}
		}
	}
}
