package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class VerticalSeparator extends Composite {

	private static VerticalSeparatorUiBinder uiBinder = GWT
			.create(VerticalSeparatorUiBinder.class);

	interface VerticalSeparatorUiBinder extends
			UiBinder<Widget, VerticalSeparator> {
	}

	@UiField DivElement div;
	
	public VerticalSeparator() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
