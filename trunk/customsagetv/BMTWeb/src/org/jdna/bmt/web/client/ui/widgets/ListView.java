package org.jdna.bmt.web.client.ui.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListView extends Composite {
	private static ListViewUiBinder uiBinder = GWT.create(ListViewUiBinder.class);

	interface ListViewUiBinder extends UiBinder<Widget, ListView> {
	}
	
	public interface OnItemLongClickListener {
		public void onLongItemClick(ListView view, ListItem item, int pos);
	}

	public interface OnItemClickListener {
		public void onItemClick(ListView view, ListItem item, int pos);
	}

	private ListAdapter adapter;

	@UiField SimplePanel items;
	@UiField VerticalPanel header;
	@UiField VerticalPanel footer;
	
	private OnItemClickListener clickListener;
	public OnItemClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(OnItemClickListener clickListener) {
		this.clickListener = clickListener;
	}

	public OnItemLongClickListener getLongClickListener() {
		return longClickListener;
	}

	public void setLongClickListener(OnItemLongClickListener longClickListener) {
		this.longClickListener = longClickListener;
	}

	private OnItemLongClickListener longClickListener;
	
	private VerticalPanel itemList = new VerticalPanel();
	
	public ListView() {
		this(null);
	}

	public ListView(ListAdapter adapter) {
		initWidget(uiBinder.createAndBindUi(this));
		itemList.setWidth("100%");
		items.setWidget(itemList);
		if (adapter!=null) {
			setListAdapter(adapter);
		}
	}
	
	public void setListAdapter(ListAdapter adapter) {
		this.adapter=adapter;
		adapter.setListView(this);
		updateView();
	}

	public void updateView() {
		itemList.clear();
		if (adapter!=null && adapter.getCount()>0) {
			for (int i=0;i<adapter.getCount();i++) {
				Widget w = adapter.getView(i);
				itemList.add(new ListItem(this, i, w));
			}
		} else {
			System.out.println("ListView: No Adapter or Adapterw as empty??");
		}
	}
	
	public void addHeader(Widget header) {
		this.header.add(header);
	}
	
	public void addFooter(Widget footer) {
		this.footer.add(footer);
	}

	public void fireLongClick(ListItem listItem) {
		if (longClickListener != null) {
			longClickListener.onLongItemClick(this, listItem, listItem.getPostion());
		}
	}

	public void fireClick(ListItem listItem) {
		if (clickListener!=null) {
			clickListener.onItemClick(this, listItem, listItem.getPostion());
		}
	}

	public ListAdapter getListAdapter() {
		return adapter;
	}
}
