package org.jdna.bmt.web.client.ui.browser2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RightSidePanel extends Composite {
	private static RightSidePanelUiBinder uiBinder = GWT.create(RightSidePanelUiBinder.class);

	interface RightSidePanelUiBinder extends UiBinder<Widget, RightSidePanel> {
	}

	public RightSidePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		ViewManager.INSTANCE.getViews("list", new IJSONReply() {
			@Override
			public void onReply(JSONValue value) {
				updateViewSelection(value.isArray());
			}
		});
		ViewManager.INSTANCE.getViews("all", new IJSONReply() {
			@Override
			public void onReply(JSONValue value) {
				updateViewPanel(value.isArray());
			}
		});
	}

	protected void updateViewPanel(JSONArray array) {
	}

	protected void updateViewSelection(JSONArray array) {
		viewSelector.clear();
		int s = array.size();
		for (int i=0;i<s;i++) {
			viewSelector.addItem(array.get(i).isObject().get("label").toString(), array.get(i).isObject().get("label").toString());
		}
	}

	@UiField
	TextBox searchBox;
	
	@UiField
	ListBox viewSelector;
	
	@UiField
	VerticalPanel views;
}
