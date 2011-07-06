package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.util.NamedProperty;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleBatchMetadataEditor extends Composite {
	private static BrowsingServiceAsync browser = GWT.create(BrowsingService.class);

	private static SimpleBatchMetadataEditorUiBinder uiBinder = GWT
			.create(SimpleBatchMetadataEditorUiBinder.class);
	
	@UiField VerticalPanel fieldListPanel;
	@UiField Button save;
	@UiField Label title;
	@UiField Button savenext;
	@UiField Button next;
	@UiField Button previous;

	interface SimpleBatchMetadataEditorUiBinder extends
			UiBinder<Widget, SimpleBatchMetadataEditor> {
	}

	private BrowsePanel controller;
	private GWTMediaResource baseResource;

	public SimpleBatchMetadataEditor(GWTMediaResource res, BrowsePanel controller) {
		initWidget(uiBinder.createAndBindUi(this));
		
		History.newItem("batchedit", false);
		
		controller.getMessageBus().postMessage(BrowsePanel.MSG_HIDE_VIEWS);
		this.controller=controller;
		updateUI(res);
	}
	
	protected void updateUI(GWTMediaResource res) {
		this.baseResource=res;
		this.title.setText(res.getPath());
		browser.getEditableMetadataFields(res, new AsyncCallback<ArrayList<NamedProperty<String>>>() {
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Unable to get list of editable fields", caught);
			}

			@Override
			public void onSuccess(ArrayList<NamedProperty<String>> result) {
				renderFields(result);
			}
		});
	}

	@UiHandler("back")
	public void back(ClickEvent evt) {
		controller.getMessageBus().postMessage(BrowsePanel.MSG_SHOW_VIEWS);
		controller.back();
	}

	@UiHandler("savenext")
	public void saveAndNext(ClickEvent evt) {
		save(evt);
		next(evt);
	}

	@UiHandler("next")
	public void next(ClickEvent evt) {
    	GWTMediaResource r = controller.getFolder().next(baseResource);
    	if (r instanceof GWTMediaFile) {
    		updateUI(r);
    	} else {
    		Application.fireNotification("No more files");
    	}
	}

	@UiHandler("previous")
	public void previous(ClickEvent evt) {
    	GWTMediaResource r = controller.getFolder().previous(baseResource);
    	if (r instanceof GWTMediaFile) {
    		updateUI(r);
    	} else {
    		Application.fireNotification("No previous file");
    	}
	}

	@UiHandler("save")
	public void save(ClickEvent evt) {
		if (baseResource instanceof GWTMediaFile || Window.confirm("You are about to batch update one or more media items.  Press 'OK' to update.")) {
			ArrayList<NamedProperty<String>> props = new ArrayList<NamedProperty<String>>();
			for (Widget w: fieldListPanel) {
				if (w instanceof MetadataFieldEditor) {
					MetadataFieldEditor fe = ((MetadataFieldEditor)w);
					NamedProperty<String> p = fe.getProperty();
					if (hasChanged(fe, p) || fe.isCleared()) {
						p.set(fe.getValue());
						p.setVisible(fe.isCleared());
						props.add(p);
					}
				}
			}
			
			if (props.size()==0) {
				Application.fireNotification("Nothing changed");
				return;
			}
			
			final PopupPanel save = Dialogs.showWaitingPopup("Saving...");
			browser.batchUpdateMetadata(baseResource, props, new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					save.hide();
					Application.fireErrorEvent("Failed to batch update one or more items.", caught);
				}

				@Override
				public void onSuccess(Boolean result) {
					save.hide();
					if (result==null||!result.booleanValue()) {
						Application.fireErrorEvent("Failed to batch update one or more items.");
					} else {
						Application.fireNotification("Batch update complete");
					}
				}
			});
		}
	}
	
	private boolean hasChanged(MetadataFieldEditor fe, NamedProperty<String> p) {
		return !StringUtils.isEmpty(fe.getValue()) && !fe.getValue().equals(p.get());
	}

	private void renderFields(ArrayList<NamedProperty<String>> result) {
		fieldListPanel.clear();
		for (NamedProperty<String> np: result) {
			fieldListPanel.add(new MetadataFieldEditor(np));
		}
	}
}
