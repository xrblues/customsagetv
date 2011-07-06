package org.jdna.bmt.web.client.ui.databrowser;

import java.util.ArrayList;
import java.util.HashMap;

import org.jdna.bmt.web.client.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DataBrowser extends Composite {

	private static DataBrowserUiBinder uiBinder = GWT
			.create(DataBrowserUiBinder.class);

	private static DataBrowserServiceAsync browser = GWT
	.create(DataBrowserService.class);

	interface DataBrowserUiBinder extends UiBinder<Widget, DataBrowser> {
	}

	@UiField
	protected ListBox userstores;
	
	@UiField
	protected SimplePanel panel;
	
	public DataBrowser() {
		initWidget(uiBinder.createAndBindUi(this));
		browser.getStores(new AsyncCallback<ArrayList<String>>() {
			@Override
			public void onSuccess(ArrayList<String> result) {
				updateUserStores(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to get data stores", caught);
			}
		});
		
		userstores.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				loadUserStores(userstores.getValue(userstores.getSelectedIndex()));
			}
		});
	}

	protected void loadUserStores(String value) {
		browser.getUserRecords(value, new AsyncCallback<StoreContents>() {
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Unabled to get user record data", caught);
			}

			@Override
			public void onSuccess(StoreContents records) {
				updateRecordData(records);
			}
		});
	}

	protected void updateRecordData(StoreContents contents) {
		FlexTable t = new FlexTable();
		t.setCellPadding(2);
		t.setCellSpacing(3);
		ArrayList<String> cols = new ArrayList(contents.getColumns());
		int collen=cols.size();
		for (int i=0; i<collen;i++) {
			t.setText(0, i+1, cols.get(i));
		}
		
		ArrayList<HashMap<String, String>> records = contents.getRecords();
		for (int i=0;i<records.size();i++) {
			t.setText(i+1, 0, String.valueOf(i));
			HashMap<String,String> r = records.get(i);
			for (int c=0;c<collen;c++) {
				t.setText(i+1, c+1, r.get(cols.get(c)));
			}
		}
		
		panel.clear();
		panel.setWidget(t);
	}

	protected void updateUserStores(ArrayList<String> result) {
		userstores.clear();
		userstores.addItem("-- Select User Store --", "__SELECT_STORE__");
		for (String r: result) {
			userstores.addItem(r);
		}
	}

}
