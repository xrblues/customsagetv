package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.util.StringUtils;

import sagex.phoenix.metadata.MediaArtifactType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DownloadFanartPanel extends Composite {

	private static DownloadFanartPanelUiBinder uiBinder = GWT
			.create(DownloadFanartPanelUiBinder.class);

	interface DownloadFanartPanelUiBinder extends
			UiBinder<Widget, DownloadFanartPanel> {
	}

	@UiField Label label;
	@UiField TextBox url;
	@UiField Button download;
	private FanartManagerPanel controller;
	private MediaArtifactType type;
	
	public DownloadFanartPanel(FanartManagerPanel controller, MediaArtifactType type) {
		initWidget(uiBinder.createAndBindUi(this));
		this.controller=controller;
		this.type=type;
		label.setText(type.name() + ": ");
	}

	@UiHandler("download")
	public void download(ClickEvent evt) {
		if (StringUtils.isEmpty(url.getText())) {
			Application.fireNotification("Download Url cannot be blank.");
			return;
		}
		controller.downloadFanart(url.getText(), type);
		url.setText("");
	}
}
