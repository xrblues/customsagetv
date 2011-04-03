package org.jdna.bmt.web.client.ui.prefs;

import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.UIObject;

public class PrefItemHelpCaption extends UIObject {
	private static PrefItemHelpCaptionUiBinder uiBinder = GWT
			.create(PrefItemHelpCaptionUiBinder.class);

	interface PrefItemHelpCaptionUiBinder extends
			UiBinder<Element, PrefItemHelpCaption> {
	}

	@UiField Element property;
	@UiField Element type;
	@UiField Element description;
	@UiField Element separator;

	public PrefItemHelpCaption(PrefItem pi) {
		setElement(uiBinder.createAndBindUi(this));
		property.setInnerText(pi.getKey());
		type.setInnerText(pi.getType());
		description.setInnerText(pi.getDescription());
		if (!StringUtils.isEmpty(pi.getListSeparator())) {
			separator.setInnerText("NOTE: This field uses a list separtor of " + pi.getListSeparator());
		}
	}
}
