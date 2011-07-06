package org.jdna.bmt.web.client.ui.util;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessageDialog extends DialogBox {
	private VerticalPanel panel = new VerticalPanel();
	
	public MessageDialog(String title, String text) {
		this(title, Arrays.asList(new String[] {text}));
	}
	
	public MessageDialog(String title, List<String> text) {
		super(false, true);
		setGlassEnabled(true);
		setText(title);
		initPanel(text);
		setWidget(panel);
	}
	
	protected void initPanel(List<String> text) {
		panel.setSpacing(10);
		for (String s: text) {
			panel.add(new HTML(s));
		}
		Button b = new Button("OK");
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		panel.add(b);
		panel.setCellHorizontalAlignment(b, HasHorizontalAlignment.ALIGN_CENTER);
	}
	
	public static void showMessage(String title, String message) {
		MessageDialog md = new MessageDialog(title, message);
		md.center();
		md.show();
	}
}
