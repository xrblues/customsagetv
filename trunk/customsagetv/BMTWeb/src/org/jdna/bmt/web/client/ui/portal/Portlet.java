package org.jdna.bmt.web.client.ui.portal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Portlet extends Composite {
	private static PortletUiBinder uiBinder = GWT.create(PortletUiBinder.class);

	interface PortletUiBinder extends UiBinder<Widget, Portlet> {
	}

	// store portlet configuration in a user records
	// pass porleturl the size=screensize, and map of args
	
	public Portlet() {
		initWidget(uiBinder.createAndBindUi(this));
		iframe.setFrameBorder(0);
		iframe.setMarginHeight(0);
		iframe.setMarginWidth(0);
		iframe.setScrolling("no");
		iframe.setSrc("http://m.google.ca/");
	}

	@UiField
	IFrameElement iframe;

	public Portlet(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
