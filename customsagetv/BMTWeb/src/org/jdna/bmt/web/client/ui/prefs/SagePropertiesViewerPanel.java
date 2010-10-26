package org.jdna.bmt.web.client.ui.prefs;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.util.UpdatablePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class SagePropertiesViewerPanel extends Composite implements UpdatablePanel {
    private final PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);
    private static SagePropertiesViewerPanelUiBinder uiBinder = GWT.create(SagePropertiesViewerPanelUiBinder.class);

    interface SagePropertiesViewerPanelUiBinder extends UiBinder<Widget, SagePropertiesViewerPanel> {
    }

    @UiField HTML grid;
    
    public SagePropertiesViewerPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    

    public String getHeader() {
        return "Sage.properties (Server)";
    }

    public String getHelp() {
        return "provides a read only view of the Sage.properties";
    }

    public boolean isReadonly() {
        return true;
    }

    public void save(AsyncCallback<UpdatablePanel> callback) {
    }



    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onLoad() {
        super.onLoad();
        
        preferencesService.getSagePropertiesAsList(new AsyncCallback<ArrayList<String>>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to load Sage.properties", caught);
            }

            public void onSuccess(ArrayList<String> result) {
                loadItems(result);
            }
        });
    }



    protected void loadItems(ArrayList<String> result) {
        grid.setHTML("");
        if (result==null) {
            Application.fireErrorEvent("No Sage Properties");
            return;
        }
        StringBuilder sb= new StringBuilder();
        for (String s: result) {
            sb.append(s+"<br/>");
        }
        grid.setHTML(sb.toString());
    }
}
