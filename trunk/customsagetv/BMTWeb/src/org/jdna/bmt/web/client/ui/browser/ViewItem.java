package org.jdna.bmt.web.client.ui.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ViewItem extends Composite implements HasText {

	private static ViewItemUiBinder uiBinder = GWT
			.create(ViewItemUiBinder.class);

	interface ViewItemUiBinder extends UiBinder<Widget, ViewItem> {
	}

	@UiField Element label;
	
	public ViewItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public String getText() {
		return label.getInnerText();
	}

	@Override
	public void setText(String text) {
		label.setInnerText(text);
	}
}
