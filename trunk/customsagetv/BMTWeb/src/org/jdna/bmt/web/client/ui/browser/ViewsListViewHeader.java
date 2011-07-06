package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.ui.widgets.PopupMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ViewsListViewHeader extends Composite implements HasText {

	private static ViewListViewHeaderUiBinder uiBinder = GWT
			.create(ViewListViewHeaderUiBinder.class);

	interface ViewListViewHeaderUiBinder extends
			UiBinder<Widget, ViewsListViewHeader> {
	}

	private ViewsListView parentListView;

	public ViewsListViewHeader(ViewsListView vlv, String text) {
		initWidget(uiBinder.createAndBindUi(this));
		setText(text);
		this.parentListView = vlv;
	}

	@UiField Label headerText;
	@UiField Image selectViewImage;
	
	@UiHandler("selectViewImage")
	void onClick(ClickEvent e) {
		PopupMenu pm = new PopupMenu("Select View Category", new ViewCategoriesListAdapter(), new PopupMenu.MenuSelectionHandler() {
			@Override
			public void onMenuItemSelected(Object data) {
				GWTView v = (GWTView) data;
				parentListView.updateViews(v);
			}
		});
		pm.setPopupPosition(getAbsoluteLeft() + getOffsetWidth() + 10, getAbsoluteTop());
		pm.show();
	}

	public void setText(String text) {
		headerText.setText(text);
	}

	public String getText() {
		return headerText.getText();
	}
}
