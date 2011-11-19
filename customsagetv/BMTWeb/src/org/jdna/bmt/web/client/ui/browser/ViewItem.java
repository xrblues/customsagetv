package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.widgets.MouseEventSink;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ViewItem extends Composite implements HasText {
	private static BrowsingServiceAsync browser = GWT.create(BrowsingService.class);
	private static ViewItemUiBinder uiBinder = GWT.create(ViewItemUiBinder.class);
	
	private static final String VISIBLE_ICON = "images/16x16/list-remove.png";
	private static final String HIDDEN_ICON = "images/16x16/list-add.png";

	interface ViewItemUiBinder extends UiBinder<Widget, ViewItem> {
	}

	@UiField Label label;
	@UiField Image hideViewAction;
	@UiField Image createViewMenuAction;
	
	private GWTView view;
	
	public ViewItem(GWTView view) {
		initWidget(uiBinder.createAndBindUi(this));
		this.view=view;
		MouseEventSink.consumeEvents(hideViewAction);
		MouseEventSink.consumeEvents(createViewMenuAction);
	}

	public void update() {
		setText(view.getLabel());
		label.setTitle(view.getId());
		hideViewAction.setUrl((view.isVisible()?VISIBLE_ICON:HIDDEN_ICON));
		hideViewAction.setTitle((view.isVisible()?"Hide View":"Show View"));
	}

	@Override
	public String getText() {
		return label.getText();
	}

	@Override
	public void setText(String text) {
		label.setText(text);
	}
	
	@UiHandler("createViewMenuAction")
	public void addViewMenu(ClickEvent evt) {
		AddMenuFromViewDialog dlg = new AddMenuFromViewDialog(view);
		Dialogs.show(dlg);
	}
	
	@UiHandler("hideViewAction")
	public void toggleHideView(ClickEvent evt) {
		evt.preventDefault();
		evt.stopPropagation();
		browser.toggleViewVisibility(view, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				view.setVisible(!view.isVisible());
				update();
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
}
