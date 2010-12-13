package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.BatchOperation;
import org.jdna.bmt.web.client.ui.BatchOperations;
import org.jdna.bmt.web.client.ui.util.CommandItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BatchOperationsDialog extends DialogBox {
	private static BatchOperationsDialogUiBinder uiBinder = GWT
			.create(BatchOperationsDialogUiBinder.class);

	interface BatchOperationsDialogUiBinder extends
			UiBinder<Widget, BatchOperationsDialog> {
	}
	
	interface Style extends CssResource {
	    String listItem();
	}

	@UiField Style style;


	@UiField
	protected VerticalPanel batchOperations;
	
	public BatchOperationsDialog() {
		setText(Application.labels().batchOperations());
		setWidget(uiBinder.createAndBindUi(this));
		
        for (BatchOperation op: BatchOperations.getInstance().getBatchOperations()) {
        	//Label l = new Label(op.getLabel());
        	//l.setWordWrap(false);
        	//l.addStyleName(style.listItem());
        	CommandItem ci = new CommandItem(null, op.getLabel(), null);
        	ci.addStyleName(style.listItem());
        	batchOperations.add(ci);
        }
	}
	
	@UiHandler("closeButton")
	public void close(ClickEvent evt) {
		hide();
	}
}
