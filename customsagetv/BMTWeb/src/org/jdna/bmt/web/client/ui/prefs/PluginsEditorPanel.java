package org.jdna.bmt.web.client.ui.prefs;

import java.util.ArrayList;

import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.SearchBoxPanel;
import org.jdna.bmt.web.client.ui.util.UpdatablePanel;
import org.jdna.bmt.web.client.ui.util.VerticalSeparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PluginsEditorPanel extends Composite implements UpdatablePanel {
	private final PreferencesServiceAsync preferencesService = GWT
			.create(PreferencesService.class);

	private VerticalPanel panel = new VerticalPanel();
	private VerticalPanel plugins = new VerticalPanel();
	private HorizontalButtonBar buttonBar = new HorizontalButtonBar();

	private SearchBoxPanel searchBox;

	public PluginsEditorPanel() {
		panel.setWidth("100%");
		panel.add(buttonBar);
		panel.add(plugins);
		initWidget(panel);
	}

	public void onLoad() {
		PluginQuery q = new PluginQuery();
		q.Type = PluginQuery.TYPE_STANDARD;
		queryPlugins(q);
		
		final ListBox lb = new ListBox();
		lb.addItem("-- Select Plugin Type --","--");
		lb.addItem("Standard", PluginQuery.TYPE_STANDARD);
		lb.addItem("UI Add-on", PluginQuery.TYPE_STVi);
		lb.addItem("UI Theme", PluginQuery.TYPE_THEME);
		lb.addItem("UI Replacement", PluginQuery.TYPE_STV);
		lb.addItem("Plugin Dependency", PluginQuery.TYPE_LIBRARY);
		lb.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (!"--".equals(lb.getValue(lb.getSelectedIndex()))) {
					PluginQuery q = new PluginQuery();
					q.Type = lb.getValue(lb.getSelectedIndex());
					queryPlugins(q);
				}
			}
		});
		
		buttonBar.add(lb);
		
		buttonBar.add(new VerticalSeparator());
		
		searchBox = new SearchBoxPanel(new SearchBoxPanel.SearchHandler() {
			@Override
			public void onSearch(SearchBoxPanel widget, String text) {
				PluginQuery q = new PluginQuery();
				q.QueryType = PluginQuery.QUERY_SEARCH_ALL;
				q.Query = text;
				queryPlugins(q);
			}
		});
		buttonBar.add(searchBox);
	}

	private void queryPlugins(PluginQuery q) {
		final PopupPanel waiting = Dialogs.showWaitingPopup("Loading Plugins...");
		waiting.show();
		
		preferencesService.getPlugins(q, new AsyncCallback<ArrayList<PluginDetail>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Plugins Failed", caught);
				waiting.hide();
			}

			@Override
			public void onSuccess(ArrayList<PluginDetail> result) {
				updatePlugins(result);
				waiting.hide();
			}
		});
	}

	protected void updatePlugins(ArrayList<PluginDetail> result) {
		plugins.clear();
		
		int size = (result==null)?0:result.size();
		
		Label l = new Label();
		l.setText(size + " Plugins");
		l.getElement().getStyle().setPaddingTop(5, Unit.PX);
		l.getElement().getStyle().setPaddingBottom(5, Unit.PX);
		l.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		plugins.add(l);
		
		if (result==null) return;
		
		for (PluginDetail p: result) {
			plugins.add(new PluginDetailWidget(this, p));
		}
	}

	public String getHeader() {
		return "SageTV Plugins";
	}

	public String getHelp() {
		return "Search Plugins and manage existing plugins";
	}

	public boolean isReadonly() {
		return true;
	}

	public void save(final AsyncCallback<UpdatablePanel> callback) {
	}

	public void updatePluginsForAuthor(String text) {
		PluginQuery q = new PluginQuery();
		q.QueryType = PluginQuery.QUERY_AUTHOR;
		q.Query = text;
		queryPlugins(q);
	}

	public HasText getSearchBox() {
		return searchBox;
	}
}
