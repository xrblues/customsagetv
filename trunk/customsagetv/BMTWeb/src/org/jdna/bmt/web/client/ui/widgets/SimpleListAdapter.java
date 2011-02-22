package org.jdna.bmt.web.client.ui.widgets;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

public abstract class SimpleListAdapter implements ListAdapter {
	public interface ViewBinder {
		public void bindView(Widget w, Object data);
	}
	
	private List items;
	private ViewBinder binder;
	private ListView listView;
	
	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	public ViewBinder getBinder() {
		return binder;
	}

	public void setBinder(ViewBinder binder) {
		this.binder = binder;
	}
	
	public SimpleListAdapter() {
	}

	public void setList(List items) {
		this.items=items;
		fireDataChanged();
	}
	
	public void fireDataChanged() {
		if (listView!=null) {
			listView.updateView();
		}
	}

	public SimpleListAdapter(List items) {
		this.items=items;
	}
	
	@Override
	public int getCount() {
		return (items!=null)?items.size():0;
	}

	@Override
	public Object getItem(int pos) {
		return items.get(pos);
	}

	@Override
	public Widget getView(int pos) {
		Widget w = createView(pos);
		if (binder!=null) {
			binder.bindView(w, getItem(pos));
		}
		return w;
	}
	
	public void addItem(Object data) {
		items.add(data);
	}
	
	public abstract Widget createView(int pos);

	@Override
	public boolean isEmpty() {
		return getCount()==0;
	}
}
