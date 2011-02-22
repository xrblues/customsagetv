package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.media.GWTViewCategories;
import org.jdna.bmt.web.client.ui.widgets.ListItem;
import org.jdna.bmt.web.client.ui.widgets.ListView;

public class ViewsListView extends ListView implements ListView.OnItemClickListener, ListView.OnItemLongClickListener, HasViews {
	private BrowsePanel controller;
	private ViewsListViewHeader header;
	public ViewsListView(BrowsePanel controller) {
		this.controller=controller;
        controller.getViews(null, this);
        setClickListener(this);
        setLongClickListener(this);
        header = new ViewsListViewHeader(this, "Views");
        addHeader(header);
	}

	@Override
	public void onLongItemClick(ListView view, ListItem item, int pos) {
	}

	@Override
	public void onItemClick(ListView view, ListItem item, int pos) {
		controller.getView((GWTView) view.getListAdapter().getItem(pos));
	}

	@Override
	public void setViews(GWTViewCategories result) {
		header.setText(result.getLabel());
		setListAdapter(new ViewsListAdapter(result));
	}

	public void updateViews(GWTView item) {
		controller.getViews(item.getId(), this);
	}
}
