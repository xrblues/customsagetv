package org.jdna.bmt.web.client.ui.widgets;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopupMenu extends PopupPanel {
	public static interface MenuSelectionHandler {
		public void onMenuItemSelected(Object data);
	}
	
	public static class SimpleMenuAdapter extends SimpleListAdapter {
		public static class SimpleItem {
			private Object id;
			private String label;

			public Object getId() {
				return id;
			}

			public void setId(Object id) {
				this.id = id;
			}

			public String getLabel() {
				return label;
			}

			public void setLabel(String label) {
				this.label = label;
			}

			public SimpleItem(Object id, String label) {
				this.id=id;
				this.label=label;
			}
		}
		
		public SimpleMenuAdapter() {
			super(new ArrayList<PopupMenu.SimpleMenuAdapter.SimpleItem>());
		}

		@Override
		public Widget createView(int pos) {
			return new Label(((SimpleItem)getItem(pos)).getLabel());
		}
		
		public void addItem(Object id, String label) {
			addItem(new SimpleItem(id, label));
		}
	}
	
	private ListView menuItems;
	private MenuSelectionHandler handler;

	public PopupMenu(String title, MenuSelectionHandler itemHandler) {
		this(title, new SimpleMenuAdapter(), itemHandler);
	}

	public PopupMenu(String title, ListAdapter items, MenuSelectionHandler itemHandler) {
		super(true, true);
		this.handler=itemHandler;
		menuItems = new ListView(items);

		Label l = new Label(title);
		l.setStyleName("PopupMenu-Title");
		VerticalPanel vp = new VerticalPanel();
		vp.add(l);
		vp.setCellVerticalAlignment(l, HasVerticalAlignment.ALIGN_MIDDLE);
		vp.setCellHorizontalAlignment(l, HasHorizontalAlignment.ALIGN_CENTER);
		vp.setCellHeight(l, "10px");
		
		vp.add(menuItems);
		
		Label f = new Label();
		vp.add(f);
		vp.setCellVerticalAlignment(f, HasVerticalAlignment.ALIGN_MIDDLE);
		vp.setCellHorizontalAlignment(f, HasHorizontalAlignment.ALIGN_CENTER);
		vp.setCellHeight(f, "10px");
		
		
		setWidget(vp);
		
		setStylePrimaryName("PopupMenu");
		menuItems.setClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(ListView view, ListItem item, int pos) {
				hide();
				handler.onMenuItemSelected(view.getListAdapter().getItem(pos));
			}
		});
		
		setGlassEnabled(true);
	}
	
	public void setListAdapter(ListAdapter la) {
		menuItems.setListAdapter(la);
	}
	
	public ListAdapter getListAdapter() {
		return menuItems.getListAdapter();
	}
}
