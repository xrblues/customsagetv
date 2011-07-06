package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.media.GWTViewCategories;
import org.jdna.bmt.web.client.ui.widgets.SimpleListAdapter;

import com.google.gwt.user.client.ui.Widget;

public class ViewsListAdapter extends SimpleListAdapter implements SimpleListAdapter.ViewBinder {
	public ViewsListAdapter(GWTViewCategories result) {
		super(result.getViews());
		setBinder(this);
	}

	@Override
	public Widget createView(int pos) {
		return new ViewItem((GWTView)getItem(pos));
	}

	@Override
	public void bindView(Widget w, Object data) {
		ViewItem item = (ViewItem)w;
		item.update();
	}
}
