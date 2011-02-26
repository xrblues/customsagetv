package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTMediaFolder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class BrowserHeaderPanel extends Composite {
	private static BrowserHeaderPanelUiBinder uiBinder = GWT
			.create(BrowserHeaderPanelUiBinder.class);

	interface BrowserHeaderPanelUiBinder extends
			UiBinder<Widget, BrowserHeaderPanel> {
	}

	@UiField SpanElement folder; 
	
	public BrowserHeaderPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void update(GWTMediaFolder item) {
		folder.setInnerText(item.getTitle() + " - "+ item.getSize() + " Items");
	}
}
