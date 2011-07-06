package org.jdna.bmt.web.client.ui.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.AsyncServiceReply;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
    private Label lastModified = new Label();
    public DebugPanel(final GWTMediaFile file) {
        this.file=file;
        /*
        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        hp.add(lastModified);
        Button b = new Button("Update Timestamp");
        hp.add(b);
        b.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                debug.updateTimestamp(file, new AsyncCallback<Long>() {
                    public void onFailure(Throwable caught) {
                    }

                    public void onSuccess(Long result) {
                        updateLastModified(result);
                    }
                });
            }
        });
        updateLastModified(file.lastModified());
        */
        listbox.addItem("Wiz.bin", "wiz.bin");
        listbox.addItem("Custom Metadata Fields", "custom");
        listbox.addItem("GWTMediaFile", "GWTMediaFile");
        listbox.addItem("BMT Metadata", "metadata");
        listbox.addItem(".properties", ".properties");
        listbox.addItem("sage7metadata", "sage7metadata");
        listbox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                updateValues(listbox.getValue(listbox.getSelectedIndex()));
            }
        });
        listbox.setWidth("100%");
        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        //vp.add(hp);
        vp.add(listbox);
        
        propPanel.setWidth("100%");
        vp.add(propPanel);
        
        ScrollPanel scroll = new ScrollPanel(propPanel);
        scroll.setHeight("400px");
        scroll.setWidth("100%");
        vp.add(scroll);
        
        main.add(vp, DockPanel.CENTER);
        main.setWidth("100%");
        initWidget(main);
        listbox.setSelectedIndex(0);
        updateValues(listbox.getValue(listbox.getSelectedIndex()));
    }
    
    private void updateLastModified(long lastModified2) {
        lastModified.setText("File Timestamp: " + lastModified2);    
    }

    private void updateValues(final String value) {
    	debug.getMetadata(value, file, new AsyncServiceReply<Map<String,String>>() {
			@Override
			public void onOK(Map<String, String> result) {
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
