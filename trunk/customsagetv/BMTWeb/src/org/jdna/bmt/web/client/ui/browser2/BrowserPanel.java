package org.jdna.bmt.web.client.ui.browser2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class BrowserPanel extends Composite {

	private static BrowserPanelUiBinder uiBinder = GWT
			.create(BrowserPanelUiBinder.class);

	interface BrowserPanelUiBinder extends UiBinder<Widget, BrowserPanel> {
	}

	public BrowserPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
