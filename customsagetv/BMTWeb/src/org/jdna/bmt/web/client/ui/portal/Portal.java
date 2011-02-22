package org.jdna.bmt.web.client.ui.portal;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;

public class Portal extends Composite {
	private Grid panel = new Grid(10, 2);
	public Portal() {
		super();
		initWidget(panel);
		panel.setCellSpacing(10);
		
		loadPortlets();
	}
	private void loadPortlets() {
		panel.setWidget(0, 0, new Portlet());
	}
}
