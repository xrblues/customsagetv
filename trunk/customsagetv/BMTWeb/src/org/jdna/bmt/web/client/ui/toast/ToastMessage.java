package org.jdna.bmt.web.client.ui.toast;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ToastMessage extends Composite implements HasText {

	private static ToastMessageUiBinder uiBinder = GWT
			.create(ToastMessageUiBinder.class);

	interface ToastMessageUiBinder extends UiBinder<Widget, ToastMessage> {
	}

	public ToastMessage() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	
	@UiField
	SpanElement message; 

	@UiField
	Image deleteIcon;
	
	@UiField 
	Image infoIcon;

	private Toaster toaster;
	
	public ToastMessage(Toaster toaster, String msg) {
		initWidget(uiBinder.createAndBindUi(this));
		this.toaster=toaster;
		setText(msg);
		infoIcon.setUrl("images/16x16/dialog-information.png");
	}

	@UiHandler("deleteIcon")
	void onClick(ClickEvent e) {
		toaster.remove(this);
	}

	public void setText(String text) {
		message.setInnerHTML(text);
	}

	public String getText() {
		return message.getInnerText();
	}

	public void setError() {
		infoIcon.setUrl("images/16x16/dialog-error.png");
	}

	public void setWarning() {
		infoIcon.setUrl("images/16x16/dialog-warning.png");
	}
}
