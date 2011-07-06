package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.ui.util.AsyncServiceReply;
import org.jdna.bmt.web.client.ui.util.MessageDialog;
import org.jdna.bmt.web.client.ui.xmleditor.AddMenu;
import org.jdna.bmt.web.client.ui.xmleditor.NamedItem;
import org.jdna.bmt.web.client.ui.xmleditor.PhoenixConfiguration;
import org.jdna.bmt.web.client.ui.xmleditor.PhoenixConfigurationAsync;
import org.jdna.bmt.web.client.util.MessageHandler;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddMenuFromViewDialog extends DialogBox implements MessageHandler {
	private PhoenixConfigurationAsync configServer = GWT.create(PhoenixConfiguration.class);

	private static AddMenuFromViewDialogUiBinder uiBinder = GWT
			.create(AddMenuFromViewDialogUiBinder.class);
	@UiField ListBox menus;
	@UiField ListBox menuItems;
	@UiField TextBox folderPath;
	@UiField TextBox viewName;
	@UiField Button btnOK;
	@UiField Button btnCancel;
	@UiField TextBox menuId;
	@UiField TextBox menuLabel;
	@UiField TextArea menuDescription;
	@UiField RadioButton insertBefore;
	@UiField ListBox flowType;

	interface AddMenuFromViewDialogUiBinder extends
			UiBinder<Widget, AddMenuFromViewDialog> {
	}

	public AddMenuFromViewDialog() {
		setWidget(uiBinder.createAndBindUi(this));
		setText("Add View Menu");
		Application.getMessagebus().postMessage(Application.MSG_REQUEST_CURRENT_VIEW_INFO);
	}
	
	public AddMenuFromViewDialog(GWTView view) {
		setWidget(uiBinder.createAndBindUi(this));
		viewName.setText(view.getId());
		setText("Add View Menu");
	}

	@UiHandler("menus")
	void onListBoxAttachOrDetach(AttachEvent event) {
		if (event.isAttached()) {
			// load flowtypes as well
			flowType.clear();
			flowType.addItem("Default","");
			flowType.addItem("Banner","BANNER");
			flowType.addItem("Grid", "GRID");
			flowType.addItem("Cover", "COVER");
			flowType.addItem("Movie", "MOVIE");
			
			
			configServer.getMenus(new AsyncServiceReply<ArrayList<NamedItem>>() {
				@Override
				public void onOK(ArrayList<NamedItem> result) {
					menus.clear();
					menus.addItem("== Select Menu ==", "");
					for (NamedItem ni : result) {
						menus.addItem(ni.getValue() + " ("+ni.getName()+")", ni.getName());
						if ("phoenix.menu.lz".equals(ni.getName())) {
							menus.setSelectedIndex(menus.getItemCount()-1);
							menus.fireEvent(new ChangeEvent() {});
						}
					}
				}
			});
		}
	}
	
	
	@UiHandler("menus")
	void onMenusChange(ChangeEvent event) {
		String val = menus.getValue(menus.getSelectedIndex());
		if (StringUtils.isEmpty(val)) {
			menuItems.clear();
		} else {
			configServer.getMenuItems(val, new AsyncServiceReply<ArrayList<NamedItem>>() {
				@Override
				public void onOK(ArrayList<NamedItem> result) {
					menuItems.clear();
					menuItems.addItem("== Select Menu Item ==", "");
					for (NamedItem ni : result) {
						menuItems.addItem(ni.getValue() + " ("+ni.getName()+")", ni.getName());
					}
				}
			});
		}
	}

	@Override
	public void onMessageReceived(String msg, Map<String, ?> args) {
		if (Application.MSG_RESPONSE_CURRENT_VIEW_INFO.equals(msg)) {
			folderPath.setText((String) args.get(Application.PARAM_MSG_RESPONSE_CURRENT_VIEW_PATH));
		}
	}
	@UiHandler("btnOK")
	void onBtnOKClick(ClickEvent event) {
		String title="Missing Require Field";
		
		if (StringUtils.isEmpty(menuId.getText())) {
			MessageDialog.showMessage(title, "You need to enter a unique menu item id for your new menu item");
			return;
		}
		
		if (StringUtils.isEmpty(menuLabel.getText())) {
			MessageDialog.showMessage(title, "You need to enter a label for your new menu item");
			return;
		}

		if (StringUtils.isEmpty(viewName.getText())) {
			MessageDialog.showMessage(title, "You need to enter the view id");
			return;
		}
		
		if (menus.getSelectedIndex()<=1) {
			MessageDialog.showMessage(title, "You need to select a menu");
			return;
		}
		
		AddMenu menu = new AddMenu();
		menu.description=menuDescription.getText();
		menu.flowType=null;
		menu.isBefore=insertBefore.getValue();
		menu.isBookmark=false;
		menu.isInline=false;
		menu.menuId=menuId.getText();
		menu.menuLabel=menuLabel.getText();
		menu.parentMenuId=menus.getValue(menus.getSelectedIndex());
		if (menuItems.getSelectedIndex()>0) {
			menu.parentMenuItemId=menuItems.getValue(menuItems.getSelectedIndex());
		}
		menu.viewId = viewName.getText();
		menu.viewPath = folderPath.getText();
		if (flowType.getSelectedIndex()>=0) {
			menu.flowType = flowType.getValue(flowType.getSelectedIndex());
		}
		configServer.addViewMenu(menu, new AsyncServiceReply<Void>() {
			@Override
			public void onOK(Void result) {
				Application.fireNotification("Menu was added.");
				hide();
			}
		});
	}
	
	@UiHandler("btnCancel")
	void onBtnCancelClick(ClickEvent event) {
		hide();
	}
}
