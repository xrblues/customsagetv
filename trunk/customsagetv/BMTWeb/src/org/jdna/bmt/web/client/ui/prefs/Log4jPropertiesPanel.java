package org.jdna.bmt.web.client.ui.prefs;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Log4jPropertiesPanel extends Composite {
    private final PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);
    private static Log4jPropertiesPanelUiBinder uiBinder = GWT.create(Log4jPropertiesPanelUiBinder.class);

    interface Log4jPropertiesPanelUiBinder extends UiBinder<Widget, Log4jPropertiesPanel> {
    }

    @UiField ListBox logs;
    @UiField Simple2ColFormLayoutPanel panel;
    @UiField TextBox key;
    @UiField TextBox value;
    
    private ArrayList<PrefItem> items;
    
    public Log4jPropertiesPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        logs.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                changed();
            }
        });
    }

    protected void changed() {
        preferencesService.getLog4jProperties(logs.getValue(logs.getSelectedIndex()), new AsyncCallback<ArrayList<PrefItem>>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Unabled to get logger properties", caught);
            }

            public void onSuccess(ArrayList<PrefItem> result) {
                updateItems(result);
            }

        });
    }

    private void updateItems(ArrayList<PrefItem> result) {
        panel.clear();
        items=result;
        for (PrefItem p : result) {
            if (p.getKey().endsWith(".File")) {
                HorizontalPanel lab = new HorizontalPanel();
                lab.setSpacing(0);
                Label l = new Label(p.getKey());
                l.addStyleName("form-label");
                lab.add(l);
                Anchor a = new Anchor();
                a.addStyleName("simple-action");
                a.setText("(view log file)");
                a.setTarget("_logfile");
                a.setHref("view?file=" + URL.encode(p.getValue()));
                lab.add(a);
                panel.add(lab, InputBuilder.textbox().bind(p).widget());
            } else {
                panel.add(p.getKey(), InputBuilder.textbox().bind(p).widget());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        panel.clear();
        preferencesService.getLog4jLoggers(new AsyncCallback<String[]>() {
            public void onSuccess(String[] result) {
                logs.clear();
                for (String s: result) {
                    logs.addItem(s);
                }
                logs.setItemSelected(0, true);
                changed();
            }
            
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("No Loggers Configured", caught);
            }
        });
    }
    
    @UiHandler("add")
    public void addProperty(ClickEvent evt) {
        if (key.getText().length()==0 || value.getText().length()==0) return;
        
        PrefItem p = new PrefItem();
        p.setKey(key.getText());
        p.setValue(value.getText());
        items.add(p);
        panel.add(p.getKey(), InputBuilder.textbox().bind(p).widget());
        key.setText("");
        value.setText("");
    }
    
    @UiHandler("save")
    public void saveProperties(ClickEvent evt) {
        preferencesService.saveLog4jProperties(logs.getValue(logs.getSelectedIndex()), items, new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Unable to save properties", caught);
            }

            public void onSuccess(Void result) {
                Application.fireNotification("Properties Saved.");
            }
        });
    }
}
