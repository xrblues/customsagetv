package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.util.NamedProperty;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MetadataFieldEditor extends Composite {
	private static MetadataFieldEditorUiBinder uiBinder = GWT.create(MetadataFieldEditorUiBinder.class);
	@UiField Label name;
	@UiField TextBox value;
	@UiField CheckBox clearfield;
	
	private NamedProperty<String> prop;

	interface MetadataFieldEditorUiBinder extends
			UiBinder<Widget, MetadataFieldEditor> {
	}
	
	public MetadataFieldEditor(NamedProperty<String> prop) {
		initWidget(uiBinder.createAndBindUi(this));
		this.prop=prop;
		this.name.setText(prop.getName());
		this.clearfield.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (clearfield.getValue()) {
					value.setText("");
				}
				value.setEnabled(!clearfield.getValue());
			}
		});
		this.value.setText(prop.get());
	}
	
	public NamedProperty<String> getProperty() {
		return prop;
	}

	public String getValue() {
		return value.getText();
	}
	
	public boolean isCleared() {
		return clearfield.getValue();
	}
}
