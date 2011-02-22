package org.jdna.bmt.web.client.ui.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class IconAction extends AbstractMouseAdapter {

	private static IconActionUiBinder uiBinder = GWT
			.create(IconActionUiBinder.class);

	interface IconActionUiBinder extends UiBinder<Widget, IconAction> {
	}

	@UiField ImageElement icon;

	public IconAction(String iconSrc) {
		initWidget(uiBinder.createAndBindUi(this));
		icon.setSrc(iconSrc);
	}
}
