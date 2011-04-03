package org.jdna.bmt.web.client.ui.prefs;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class MenuEditorPanel extends Composite {
	private final PreferencesServiceAsync service = GWT
	.create(PreferencesService.class);
	
	private static MenuEditorPanelUiBinder uiBinder = GWT
			.create(MenuEditorPanelUiBinder.class);

	interface MenuEditorPanelUiBinder extends UiBinder<Widget, MenuEditorPanel> {
	}

	public MenuEditorPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		service.getMenus(new AsyncCallback<ArrayList<String>>() {
			@Override
			public void onSuccess(ArrayList<String> result) {
				if (result==null||result.size()==0) {
					Application.fireNotification("No menus to load");
					return;
				}
				menusList.clear();
				menusList.addItem("-- Select Menu --", "-");
				for (String s: result) {
					menusList.addItem(s);
				}
				menusList.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						final String value = menusList.getValue(menusList.getSelectedIndex());
						if ("-".equals(value)) {
							return;
						}
						
						service.loadMenu(value, new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								Application.fireErrorEvent("Failed to load menu " + value);
							}

							@Override
							public void onSuccess(String result) {
								menuXML.setText(result);
							}
						});
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Application.fireNotification("Failed to load menus");
			}
		});
	}

	@UiField TextArea menuXML;
	@UiField ListBox menusList;

	@UiHandler("new_menu")
	void onNewMenu(ClickEvent e) {
		menuXML.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<!DOCTYPE menus SYSTEM \"menus.dtd\">\n" + 
				"\n" + 
				"<menus>\n" + 
				"  <menu name=\"YOUR_MENU\" label=\"Your Menu Label\" type=\"TV\" background=\"GlobalMenuBackground.jpg\" visible=\"true\">\n" + 
				"  </menu>\n" + 
				"</menus>");
	}

	@UiHandler("save_menu")
	void onSaveMenu(ClickEvent e) {
		service.saveMenu(menuXML.getText(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to save menu, most likely XML Issue", caught);
			}

			@Override
			public void onSuccess(String result) {
				menuXML.setText(result);
				Application.fireNotification("Menu Saved");
			}
		});
	}
}
