package org.jdna.bmt.web.client.ui.prefs;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.input.FileChooserTextBox;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.input.LargeStringTextBox;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.UpdatablePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

public class Log4jEditorPanel extends Composite implements UpdatablePanel {
    private final PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);

    private Simple2ColFormLayoutPanel     grid;
    private Log4jPrefs                    prefs              = null;

    public Log4jEditorPanel() {
        grid = new Simple2ColFormLayoutPanel();
        initWidget(grid);
    }

    public void onLoad() {
        preferencesService.getLog4jPreferences(new AsyncCallback<Log4jPrefs>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to load log4j perferences", caught);
            }

            public void onSuccess(Log4jPrefs result) {
                if (result == null) {
                    Application.fireErrorEvent("Log4j Returned No Preferences!");
                    return;
                }
                updatePanel(result);
            }

        });
    }

    private void updatePanel(Log4jPrefs result) {
        prefs = result;
        grid.clear();
        grid.add("Log File", new FileChooserTextBox(InputBuilder.textbox().bind(prefs.getFile()).widget(), "Log4j Log File", false));
        grid.add("Log Format", new LargeStringTextBox(InputBuilder.textbox().bind(prefs.getPattern()).widget(), "Log4j Log Format String"));
        grid.add("Default Log Level", InputBuilder.combo("error,warn,info,debug").bind(prefs.getLevel()).widget());
        grid.add("BMT Log Level", InputBuilder.combo("error,warn,info,debug").bind(prefs.getBmtLevel()).widget());
        grid.add("Phoenix Log Level", InputBuilder.combo("error,warn,info,debug").bind(prefs.getPhoenixLevel()).widget());
    }

    public String getHeader() {
        return "Log4j Preferences";
    }

    public String getHelp() {
        return "Configure Log4j Preferences";
    }

    public boolean isReadonly() {
        return false;
    }

    public void save(final AsyncCallback<UpdatablePanel> callback) {
        preferencesService.saveLog4jPreferences(prefs, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            public void onSuccess(String result) {
                callback.onSuccess(Log4jEditorPanel.this);
            }
        });
    }
}
