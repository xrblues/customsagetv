package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.ui.util.SearchBoxPanel;
import org.jdna.bmt.web.client.ui.util.SearchBoxPanel.SearchHandler;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.ui.util.SideMenuItem;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel extends Composite implements SearchHandler {

	private static SearchPanelUiBinder uiBinder = GWT.create(SearchPanelUiBinder.class);

	interface SearchPanelUiBinder extends UiBinder<Widget, SearchPanel> {
	}

	@UiField SearchBoxPanel searchBox;
	@UiField VerticalPanel searchResultsPanel;
	
	private BrowsePanel controller;

	public SearchPanel(BrowsePanel controller) {
		this.controller = controller;
		initWidget(uiBinder.createAndBindUi(this));
		
		searchBox.setSearchHandler(this);
		searchBox.setHint("Search...");
		
		Element el = DOM.createIFrame();
		el.setAttribute("src", "help/pql.html");
		el.setAttribute("width", "500px");
		el.setAttribute("height", "100%");
		el.setAttribute("frameborder", "0");
		HTMLPanel panel = new HTMLPanel("");
		panel.setWidth("500px");
		panel.setHeight("500px");
		panel.getElement().appendChild(el);
		//Label help = new Label();
		//help.setText("You can enter searches using Phoenix Query Language (PQL).  Sample Queries include\nHouse - Search for House as the Title\nGenre contains 'Horror' or Genre contains 'Thriller'\n");
		searchBox.setHelpWidget(panel);
	}

	private void reset() {
		searchBox.setText("");
	}

	@Override
	public void onSearch(SearchBoxPanel widget, final String text) {
		if (StringUtils.isEmpty(text)) {
			Application.fireErrorEvent("Missing Search Text");
			return;
		}
		
		controller.getServices().searchMediaFiles(text, new AsyncCallback<ServiceReply<GWTMediaFolder>>() {
			@Override
			public void onSuccess(final ServiceReply<GWTMediaFolder> result) {
				if (result==null) {
					Application.fireErrorEvent("Nothing found for " + text);
					return;
				}
				
				if (result.getCode()==0) {
					SideMenuItem<GWTMediaFolder> smi = new SideMenuItem<GWTMediaFolder>(text, null, new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							controller.browseFolder(result.getData(), 0, result.getData().getPageSize());
						}
					});
					smi.setWidth("100%");
					searchResultsPanel.setVisible(true);
					searchResultsPanel.add(smi);
					controller.browseFolder(result.getData(), 0, result.getData().getPageSize());
					reset();
				} else {
					Application.fireErrorEvent("Error in search " + searchBox.getText() + ";  Error was " + result.getMessage());
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Search Failed for " + text, caught);
			}
		});
	}
}
