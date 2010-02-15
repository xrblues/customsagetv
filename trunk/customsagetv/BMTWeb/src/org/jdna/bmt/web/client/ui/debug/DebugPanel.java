package org.jdna.bmt.web.client.ui.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DebugPanel extends Composite {
    private DockPanel main = new DockPanel();
    private Simple2ColFormLayoutPanel propPanel = new Simple2ColFormLayoutPanel();
    private ListBox listbox = new ListBox();
    private DebugServiceAsync debug = GWT.create(DebugService.class);
    private GWTMediaFile file;
    
    public DebugPanel(GWTMediaFile file) {
        this.file=file;
        listbox.addItem("Wiz.bin", "wiz.bin");
        listbox.addItem(".properties", ".properties");
        listbox.addItem("Custom Metadata Fields", "custom");
        //listbox.addItem("Fanart Metadata", "fanart");
        listbox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                updateValues(listbox.getValue(listbox.getSelectedIndex()));
            }
        });
        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        vp.add(listbox);
        
        propPanel.setWidth("100%");
        vp.add(propPanel);
        
        ScrollPanel scroll = new ScrollPanel(propPanel);
        scroll.setHeight("400px");
        scroll.setWidth("100%");
        vp.add(scroll);
        
        main.add(vp, DockPanel.CENTER);
        initWidget(main);
        listbox.setSelectedIndex(0);
        updateValues(listbox.getValue(listbox.getSelectedIndex()));
    }
    
    private void updateValues(final String value) {
        debug.getMetadata(value, file, new AsyncCallback<Map<String,String>>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to get debug info for: " + value);
            }
            
            public void onSuccess(Map<String, String> result) {
                updatePanels(result);
            }
        });
    }

    protected void updatePanels(Map<String, String> result) {
        propPanel.clear();
        Set<String> keyset =result.keySet();
        List<String> keys = new ArrayList<String>(keyset);
        Collections.sort(keys);
        for (String k : keys) {
            propPanel.add(new Label(k), new Label(result.get(k)));
        }
    }
}
