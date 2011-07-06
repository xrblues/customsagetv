package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.ui.widgets.SimpleListAdapter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ViewCategoriesListAdapter extends SimpleListAdapter implements SimpleListAdapter.ViewBinder, HasViewCategories {
	private static BrowsingServiceAsync browser = GWT.create(BrowsingService.class);

	public ViewCategoriesListAdapter() {
		super();
		setBinder(this);
		browser.getViewCategories(new AsyncCallback<ArrayList<GWTView>>() {
			@Override
			public void onSuccess(ArrayList<GWTView> result) {
				setViewCategories(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Unable to get view categories", caught);
			}
		});
	}

	@Override
	public Widget createView(int pos) {
		return new Label();
	}

	@Override
	public void bindView(Widget w, Object data) {
		GWTView view = (GWTView) data;
		Label l = (Label) w;
		l.setText(view.getLabel());
	}

	@Override
	public void setViewCategories(ArrayList<GWTView> result) {
		result.add(0, new GWTView(null, "All Views"));
		result.add(1, new GWTView("hidden", "Hidden Views"));
		setList(result);
	}
}
