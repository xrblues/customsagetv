package org.jdna.bmt.web.client.ui.widgets;

import com.google.gwt.user.client.ui.DialogBox;

public class ListViewDialog extends DialogBox implements ListView.OnItemClickListener {
	private ListView.OnItemClickListener clickListener;
	
	public ListViewDialog(String title, ListView view, ListView.OnItemClickListener listener) {
		super(false, true);
		
		view.setClickListener(this);
		setWidget(view);
		setText(title);
		this.clickListener=listener;
	}

	@Override
	public void onItemClick(ListView view, ListItem item, int pos) {
		hide();
		clickListener.onItemClick(view, item, pos);
	}
}
