package org.jdna.bmt.web.client.ui.app;

import org.jdna.bmt.web.client.AppConstants;
import org.jdna.bmt.web.client.Version;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AboutDialog extends Composite {
	private static AboutDialogUiBinder uiBinder = GWT
			.create(AboutDialogUiBinder.class);

	interface AboutDialogUiBinder extends UiBinder<Widget, AboutDialog> {
	}

	public AboutDialog() {
		initWidget(uiBinder.createAndBindUi(this));
		version.setInnerHTML(Version.VERSION);
		userGuide.setHref(AppConstants.USERGUIDE_URL);
		aboutGuide.setHref(AppConstants.ABOUTGUIDE_URL);
	}

	@UiField
	SpanElement version;
	
	@UiField
	AnchorElement userGuide;

	@UiField
	AnchorElement aboutGuide;
}
