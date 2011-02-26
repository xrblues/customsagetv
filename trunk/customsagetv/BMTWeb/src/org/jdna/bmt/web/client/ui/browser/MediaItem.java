package org.jdna.bmt.web.client.ui.browser;

import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTAiringDetails;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.ui.debug.DebugDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.widgets.AbstractMouseAdapter;
import org.jdna.bmt.web.client.ui.widgets.IconAction;
import org.jdna.bmt.web.client.ui.widgets.PopupMenu;
import org.jdna.bmt.web.client.ui.widgets.PopupMenu.SimpleMenuAdapter;
import org.jdna.bmt.web.client.util.DateFormatUtil;
import org.jdna.bmt.web.client.util.MessageHandler;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaItem extends AbstractMouseAdapter implements MessageHandler {
	private static final int MENU_VIDEO_THUMB = 1;
	private static final int MENU_DELETE_FILE = 2;
	private static final int MENU_DELETE_WRONG_RECORDING = 3;
	private static final int MENU_DEBUG = 4;

	protected static final int MENU_RECORD_FAVORITE = 5;
	protected static final int MENU_RECORD_ONCE = 6;

	protected static final int MENU_SET_WATCHED = 7;

	private GWTMediaResource res;
	private VerticalPanel vpanel = new VerticalPanel();
	private VerticalPanel titles = new VerticalPanel();
	private HorizontalPanel actions = new HorizontalPanel();
	private BrowsePanel controller;
	private Image watchedIcon;

	public MediaItem(final GWTMediaResource res, BrowsePanel controller) {
		super();

		this.controller = controller;
		this.res = res;
		titles.setWidth("100%");
		vpanel.add(titles);
		vpanel.setWidth("150px");
		vpanel.setCellVerticalAlignment(titles, HasVerticalAlignment.ALIGN_TOP);
		vpanel.setCellHeight(titles, "10px");
		vpanel.setStyleName("MediaItem");
		setTitles(res);

		final Image img = new Image();
		img.addStyleName("MediaItem-Image");
		img.addErrorHandler(new ErrorHandler() {
			public void onError(ErrorEvent event) {
				String url = img.getUrl();
				GWT.log("Could not load image: " + url);
				if (res instanceof GWTMediaFolder) {
					img.setUrl("images/128x128/folder_video2.png");
				} else {
					img.setUrl("images/128x128/video2.png");
				}
				img.setTitle(url);
			}
		});

		if (res instanceof GWTMediaFolder) {
			if (res.getThumbnailUrl() != null) {
				img.setUrl(res.getThumbnailUrl());
			} else {
				img.addStyleName("MediaItemPoster-Folder");
				img.setUrl("images/128x128/folder_video2.png");
			}
		} else {
			img.setUrl(res.getThumbnailUrl());
			// set the actions
			actions.setSpacing(10);
			if (!(res instanceof GWTMediaFolder)) {
				watchedIcon = new Image("images/marker_watched.png");
				watchedIcon.setSize("16px", "16px");
				watchedIcon.setVisible(((GWTMediaFile) res).getIsWatched()
						.get());
				actions.insert(watchedIcon, 0);

				if (!res.isAiring()) {
					actions.insert(new IconAction(
							"images/16x16/media-playback-start.png") {
						@Override
						protected void onClick() {
							playFile((GWTMediaFile) res);
						}
					}, 0);
				}

				if (res.isAiring()
						&& ((GWTMediaFile) res).getAiringDetails() != null
						&& ((GWTMediaFile) res).getAiringDetails()
								.isManualRecord()) {
					Image img2 = new Image("images/marker_manual.png");
					img2.setSize("16px", "16px");
					actions.insert(img2, 0);
				}

			}
		}

		vpanel.add(img);
		vpanel.setCellHorizontalAlignment(img,
				HasHorizontalAlignment.ALIGN_CENTER);
		vpanel.setCellVerticalAlignment(img, HasVerticalAlignment.ALIGN_BOTTOM);

		vpanel.add(actions);
		vpanel.setCellHorizontalAlignment(actions,
				HasHorizontalAlignment.ALIGN_RIGHT);
		vpanel.setCellVerticalAlignment(actions,
				HasVerticalAlignment.ALIGN_BOTTOM);
		vpanel.setCellHeight(actions, "20px");

		if (res instanceof GWTMediaFolder) {
			vpanel.addStyleName("MediaFolder");
		}

		if (res.getMessage() != null) {
			vpanel.setTitle(res.getMessage());
		}

		initWidget(vpanel);
	}

	protected void playFile(GWTMediaFile res2) {
		Dialogs.showAsDialog("Play Video", new PlayOnClientDialogPanel(res2));
	}

	private void setTitles(GWTMediaResource res2) {
		titles.clear();
		Label title1 = new Label(res2.getTitle());
		titles.add(title1);
		titles.setCellHorizontalAlignment(title1,
				HasHorizontalAlignment.ALIGN_CENTER);
		title1.setStyleName("MediaItem-Title1");

		String epTitle = res2.getMinorTitle();
		if (!StringUtils.isEmpty(epTitle) && !epTitle.equals(res.getTitle())) {
			Label title2 = new Label(epTitle);
			titles.add(title2);
			titles.setCellHorizontalAlignment(title2,
					HasHorizontalAlignment.ALIGN_CENTER);
			title2.setStyleName("MediaItem-Title2");
		}

		if (res2 instanceof GWTMediaFile
				&& ((GWTMediaFile) res2).getAiringDetails() != null) {
			GWTAiringDetails det = ((GWTMediaFile) res2).getAiringDetails();
			StringBuilder sb = new StringBuilder();

			Label title = new Label(DateFormatUtil.formatAiredDate(det
					.getStartTime()));
			titles.add(title);
			titles.setCellHorizontalAlignment(title,
					HasHorizontalAlignment.ALIGN_CENTER);
			title.setStyleName("MediaItem-Title2");

			String s = "on channel " + det.getChannel() + " ("
					+ det.getNetwork() + ")";
			title = new Label(s);
			titles.add(title);
			titles.setCellHorizontalAlignment(title,
					HasHorizontalAlignment.ALIGN_CENTER);
			title.setStyleName("MediaItem-Title2");
		}

	}

	public void onClick() {
		if (res instanceof GWTMediaFolder) {
			controller.browseFolder((GWTMediaFolder) res, 0,
					((GWTMediaFolder) res).getPageSize());
		} else {
			if (res.isAiring()) {
				controller.setDisplay(new ViewAiringItemDetails(
						(GWTMediaFile) res, controller));
			} else {
				controller.setDisplay(new MediaEditorMetadataPanel(
						(GWTMediaFile) res, controller));
			}
		}
	}

	public void onLongPress() {
		if (res instanceof GWTMediaFile) {
			if (res.isAiring()) {
				PopupMenu pm = new PopupMenu("Select MediaFile Action",
						new PopupMenu.MenuSelectionHandler() {
							@Override
							public void onMenuItemSelected(Object data) {
								int id = (Integer) ((SimpleMenuAdapter.SimpleItem) data)
										.getId();
								switch (id) {
								case MENU_RECORD_FAVORITE:
									Application
											.fireNotification("Favorites Not Implemented");
									break;
								case MENU_RECORD_ONCE:
									controller.record((GWTMediaFile) res);
									break;
								case MENU_SET_WATCHED:
									controller.setWatched((GWTMediaFile) res, !((GWTMediaFile)res).getIsWatched().get());
									break;
								}
							}
						});

				GWTMediaFile f = (GWTMediaFile) res;
				SimpleMenuAdapter sma = (SimpleMenuAdapter) pm.getListAdapter();
				sma.addItem(MENU_RECORD_FAVORITE, "Add as Favorite");
				sma.addItem(MENU_RECORD_ONCE, "Record Once");
				sma.addItem(MENU_SET_WATCHED, "Toggle Watched");
				sma.fireDataChanged();
				pm.center();
				pm.show();
			} else {
				PopupMenu pm = new PopupMenu("Select MediaFile Action",
						new PopupMenu.MenuSelectionHandler() {
							@Override
							public void onMenuItemSelected(Object data) {
								int id = (Integer) ((SimpleMenuAdapter.SimpleItem) data)
										.getId();
								switch (id) {
								case MENU_DELETE_FILE:
									if (Window
											.confirm("Press OK to delete this file, permanently.  It will be removed from disk.")) {
										removeFromParent();
									}
									break;
								case MENU_DELETE_WRONG_RECORDING:
									if (Window
											.confirm("Press OK to delete this file, permanently, since it was recording that was recorded incorrectly.  It will be removed from disk.")) {
										removeFromParent();
									}
									break;
								case MENU_DEBUG:
									DebugDialog.show((GWTMediaFile) res);
									break;
								case MENU_SET_WATCHED:
									controller.setWatched((GWTMediaFile) res, !((GWTMediaFile)res).getIsWatched().get());
									break;
								case MENU_VIDEO_THUMB:
									controller
											.showViewThumbnails((GWTMediaFile) res);
									break;
								}
							}
						});

				GWTMediaFile f = (GWTMediaFile) res;
				SimpleMenuAdapter sma = (SimpleMenuAdapter) pm.getListAdapter();
				sma.addItem(MENU_VIDEO_THUMB, "View Video Thumbnails");
				sma.addItem(MENU_DELETE_FILE, "Delete Media File");
				if (f.getSageRecording().get()) {
					sma.addItem(MENU_DELETE_WRONG_RECORDING,
							"Delete, Wrong Recording");
				}
				sma.addItem(MENU_SET_WATCHED, "Toggle Watched");
				sma.addItem(MENU_DEBUG, "View Debug Metadata Details");
				sma.fireDataChanged();
				pm.center();
				pm.show();
			}
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		controller.getMessageBus().addHandler(BrowsePanel.MSG_FILE_WATCHED,
				this);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		controller.getMessageBus().removeHandler(BrowsePanel.MSG_FILE_WATCHED,
				this);
	}

	@Override
	public void onMessageReceived(String msg, Map<String, ?> args) {
		if (watchedIcon!=null && BrowsePanel.MSG_FILE_WATCHED.equals(msg)) {
			GWTMediaFile f = (GWTMediaFile) args.get("file");
			boolean watched = (Boolean) args.get("watched");
			if (f.getAiringId() != null
					&& f.getAiringId().equals(((GWTMediaFile)res).getAiringId())) {
				    ((GWTMediaFile)res).getIsWatched().set(watched);
					watchedIcon.setVisible(watched);
			}
		}
	}
}
