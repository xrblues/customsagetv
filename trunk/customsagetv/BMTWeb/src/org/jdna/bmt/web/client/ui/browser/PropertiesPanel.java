package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.debug.DebugService;
import org.jdna.bmt.web.client.ui.debug.DebugServiceAsync;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.AsyncServiceReply;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class PropertiesPanel extends Composite {
	private DebugServiceAsync debug = GWT.create(DebugService.class);
	private Simple2ColFormLayoutPanel propPanel = new Simple2ColFormLayoutPanel();
	private GWTMediaFile file;
	
	public PropertiesPanel(GWTMediaFile file) {
		this.file = file;
		updateValues();
		initWidget(propPanel);
	}

    private void updateValues() {
    	debug.getMetadata(".properties", file, new AsyncServiceReply<Map<String,String>>() {
    		
			@Override
			public void onOK(Map<String, String> result) {
                updatePanels(result);
			}

			@Override
			public void onError(ServiceReply<Map<String, String>> result) {
				propPanel.clear();
				propPanel.add("", new Label(result.getMessage()));
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
