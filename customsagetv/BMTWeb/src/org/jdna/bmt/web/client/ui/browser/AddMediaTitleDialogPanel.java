package org.jdna.bmt.web.client.ui.browser;

import java.util.HashMap;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddMediaTitleDialogPanel extends DialogBox {
	private static BrowsingServiceAsync browser = GWT
	.create(BrowsingService.class);

	private static AddMediaTitleDialogPanelUiBinder uiBinder = GWT
			.create(AddMediaTitleDialogPanelUiBinder.class);
	
	@UiField RadioButton selectFile;
	@UiField RadioButton selectFolder;
	@UiField TextBox file;
	@UiField TextBox folder;
	@UiField TextBox provider;
	@UiField TextBox providerid;
	@UiField TextBox mediatype;
	@UiField Button save;
	@UiField Button cancel;
	@UiField Label filename;

	private GWTMediaFile mediaFile;

	interface AddMediaTitleDialogPanelUiBinder extends
			UiBinder<Widget, AddMediaTitleDialogPanel> {
	}

	public AddMediaTitleDialogPanel(GWTMediaFile mediaFile) {
		setText(Application.labels().addMediaTitlesDialog());
		setWidget(uiBinder.createAndBindUi(this));
		this.mediaFile=mediaFile;
		
		filename.setText(mediaFile.getPath());
		
		GWTMediaMetadata md = mediaFile.getMetadata();
		if (md!=null) {
			String path = mediaFile.getPath();
			if (path!=null) {
				String parts[] = path.split("[\\/]");
				if (parts.length>2) {
					String filespec = parts[parts.length-1];
					String matchon[] = filespec.split("[-\\.]");
					file.setText(matchon[0].trim());
					if (mediaFile.getSageRecording().get()) {
						file.setText(file.getText()+"-");
					}
					folder.setText(parts[parts.length-2].trim());
				}
			}
			provider.setText(md.getMediaProviderID().get());
			providerid.setText(md.getMediaProviderDataID().get());
			mediatype.setText(md.getMediaType().get());
		}
	}
	
	@UiHandler("save")
	public void save(ClickEvent evt) {
		hide();
		HashMap<String, String> fields = new HashMap<String, String>();

		String regex = null;
		if (selectFile.getValue()) {
			regex = file.getText();
			if (StringUtils.isEmpty(regex)) {
				Application.fireNotification("You must enter a file or folder pattern");
				return;
			}
			regex = "[\\\\/]" + regex;
		} else if (selectFolder.getValue()) {
			regex = folder.getText();
			if (StringUtils.isEmpty(regex)) {
				Application.fireNotification("You must enter a file or folder pattern");
				return;
			}
			regex = "[\\\\/]" + regex + "[\\\\/]";
		}
		
		if (StringUtils.isEmpty(regex)) {
			Application.fireNotification("You must enter a file or folder pattern");
			return;
		}
		
		if (StringUtils.isEmpty(mediatype.getText())) {
			Application.fireNotification("You must enter a mediatype");
			return;
		}
		
		if (StringUtils.isEmpty(provider.getText())) {
			Application.fireNotification("You must enter a provider type");
			return;
		}

		if (StringUtils.isEmpty(providerid.getText())) {
			Application.fireNotification("You must enter the provider's unique data id");
			return;
		}
		
		fields.put("regex", regex);
		fields.put("mediatype", mediatype.getText());
		fields.put("providerid", provider.getText());
		fields.put("dataid", providerid.getText());
		
		browser.addMediaTitle(fields, new AsyncCallback<ServiceReply<Boolean>>() {
			@Override
			public void onSuccess(ServiceReply<Boolean> result) {
				if (!result.getData()) {
					Application.fireErrorEvent(result.getMessage());
					return;
				}
				
				Application.fireNotification("New matcher has been added and saved.");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to save new matcher");
			}
		});
	}

	@UiHandler("cancel")
	public void cancel(ClickEvent evt) {
		hide();
	}
}
