package org.jdna.bmt.web.client.ui.input;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.prefs.PreferencesService;
import org.jdna.bmt.web.client.ui.prefs.PreferencesServiceAsync;
import org.jdna.bmt.web.client.ui.prefs.RegexValidation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RegexEditorPanel extends Composite implements HasValue<String> {
    private final PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);

    private static RegexEditorPanelUiBinder uiBinder = GWT.create(RegexEditorPanelUiBinder.class);

    interface RegexEditorPanelUiBinder extends UiBinder<Widget, RegexEditorPanel> {
    }

    @UiField TextBox regex;
    @UiField TextArea sampleData;
    @UiField TextArea results;
    
    public RegexEditorPanel(String regexValue) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public String getValue() {
        return regex.getValue();
    }

    public void setValue(String value) {
        regex.setValue(value);
    }

    public void setValue(String value, boolean fireEvents) {
        regex.setValue(value, fireEvents);
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return regex.addValueChangeHandler(handler);
    }
    
    @UiHandler("test")
    public void testRegex(ClickEvent click) {
        RegexValidation val = new RegexValidation(regex.getText(), sampleData.getText(), null);
        preferencesService.validateRegex(val, new AsyncCallback<RegexValidation>() {
            public void onSuccess(RegexValidation result) {
                results.setText(result.getResults());
            }
            
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to call regex service", caught);
            }
        });
    }
}
