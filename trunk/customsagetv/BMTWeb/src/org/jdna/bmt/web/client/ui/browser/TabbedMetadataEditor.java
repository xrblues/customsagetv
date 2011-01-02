package org.jdna.bmt.web.client.ui.browser;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TabbedMetadataEditor extends Composite implements MetadataUpdatedHandler, ChangeHandler {
	private VerticalPanel panel = new VerticalPanel();
	private TabBar tabs = new TabBar();
	
	public TabbedMetadataEditor() {
		tabs.addTab(new Button("Details"));
		tabs.addTab("Cast");
		tabs.addTab("Fanart");
		tabs.addTab("Properties");
		panel.setWidth("100%");
		panel.add(tabs);
		initWidget(panel);
	}
	
	@Override
	public void onChange(ChangeEvent event) {
		
	}

	@Override
	public void onMetadataUpdated(MetadataUpdatedEvent event) {
	}
}
