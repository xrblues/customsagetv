package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.ui.util.SideMenuItem;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel extends Composite {

	private static SearchPanelUiBinder uiBinder = GWT.create(SearchPanelUiBinder.class);

	interface SearchPanelUiBinder extends UiBinder<Widget, SearchPanel> {
	}

	@UiField HorizontalPanel searchContainer;
	@UiField TextBox searchBox;
	@UiField VerticalPanel searchResultsPanel;
	@UiField Image searchButton;

	public SearchPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		reset();
		searchResultsPanel.setVisible(false);
		searchBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (searchBox.getValue().contains("...")) {
					searchBox.setValue("");
				}
			}
		});
		searchBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
                if (event.getCharCode()==13) {
                    doSearch();
                }
			}
		});
		
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doSearch();
			}
		});
		
		searchContainer.setCellVerticalAlignment(searchButton, HasVerticalAlignment.ALIGN_MIDDLE);
	}

	private void reset() {
		if (StringUtils.isEmpty(searchBox.getText())) {
			searchBox.setText("Search...");
		} else {
			searchBox.setText("");
		}
	}

	protected void doSearch() {
		if (StringUtils.isEmpty(searchBox.getText())) {
			Application.fireErrorEvent("Missing Search Text");
			return;
		}
		
		BrowsingServicesManager.getInstance().getServices().searchMediaFiles(searchBox.getText(), new AsyncCallback<GWTMediaFolder>() {
			@Override
			public void onSuccess(final GWTMediaFolder result) {
				if (result!=null) {
					SideMenuItem<GWTMediaFolder> smi = new SideMenuItem<GWTMediaFolder>(searchBox.getText(), null, new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							BrowsingServicesManager.getInstance().browseFolder(result, 0, result.getPageSize());
						}
					});
					smi.setWidth("100%");
					searchResultsPanel.setVisible(true);
					searchResultsPanel.add(smi);
					BrowsingServicesManager.getInstance().browseFolder(result, 0, result.getPageSize());
				} else {
					Application.fireErrorEvent("Nothing found for " + searchBox.getText());
				}
				reset();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Search Failed for " + searchBox.getText(), caught);
				reset();
			}
		});
	}
}
