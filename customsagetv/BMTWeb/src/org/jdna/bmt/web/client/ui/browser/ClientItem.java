package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.util.AbstractClickableItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ClientItem extends AbstractClickableItem {
	private static ClientItemUiBinder uiBinder = GWT
			.create(ClientItemUiBinder.class);

	interface ClientItemUiBinder extends UiBinder<Widget, ClientItem> {
	}

	@UiField Label name;
	@UiField Label id;
	private PlayOnClientDialogPanel panel;

	public ClientItem(PlayOnClientDialogPanel panel, String sname, String sid) {
		initWidget(uiBinder.createAndBindUi(this));
		name.setText(sname);
		id.setText(sid);
		this.panel = panel;
	}

	@Override
	public void onClick(ClickEvent event) {
		panel.playFileForClient(this);
	}

	public String getId() {
		return id.getText();
	}

	public String getName() {
		return name.getText();
	}
}
