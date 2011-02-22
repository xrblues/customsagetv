package org.jdna.bmt.web.client.ui.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListItem extends AbstractMouseAdapter {
	private static ListItemUiBinder uiBinder = GWT
			.create(ListItemUiBinder.class);

	interface ListItemUiBinder extends UiBinder<Widget, ListItem> {
	}

	private ListView listView;
	private int pos;
	
	@UiField VerticalPanel item;

	public ListItem(ListView parent, int pos, Widget child) {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		item.add(child);
		item.setCellVerticalAlignment(child, HasVerticalAlignment.ALIGN_MIDDLE);
		item.setCellWidth(child, "100%");
		this.listView=parent;
		this.pos=pos;
		
	}

	@Override
	protected void onLongPress() {
		super.onLongPress();
		listView.fireLongClick(this);
	}

	@Override
	protected void onClick() {
		super.onClick();
		listView.fireClick(this);
	}
	
	public Widget getWidget() {
		return item.getWidget(0);
	}

	public int getPostion() {
		return pos;
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		super.onMouseOver(event);
	}
}
