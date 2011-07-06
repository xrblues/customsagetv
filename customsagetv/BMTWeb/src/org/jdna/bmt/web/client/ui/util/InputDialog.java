package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class InputDialog extends DialogBox {
	private VerticalPanel panel = new VerticalPanel();
	
	public InputDialog(String title, String label, String defText) {
		super(false, true);
		setGlassEnabled(true);
		setText(title);
		Label l = new Label(label);
		final TextBox tb = new TextBox();
		if (defText!=null) {
			tb.setText(defText);
		}
		panel.setSpacing(5);
		panel.add(l);
		panel.add(tb);
		
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		Button b1 = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		b1.setWidth("2cm");
		Button b2 = new Button("OK", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onOK(tb.getText());
			}
		});
		b2.setWidth("2cm");
		buttons.add(b2);
		buttons.add(b1);
		panel.add(buttons);
		panel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_CENTER);
		setWidget(panel);
	}

	public abstract void onOK(String text);
}
