package org.jdna.bmt.web.client.ui.scan;

import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.input.NVP;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.TitlePanel;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchQueryPanel extends DataDialog<SearchQueryOptions> {
    private final BrowserServiceAsync browserService = GWT.create(BrowserService.class);

    private ListBox providers;
    private ListBox type;
    private TextBox episodeTitle;
    private TextBox episode;
    private TextBox season;
    private TitlePanel tvOptions;

    @Override
    protected Widget getBodyWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        
        Simple2ColFormLayoutPanel propPanel = new Simple2ColFormLayoutPanel();
        propPanel.setWidth("100%");
        List<NVP<String>> nvp = new ArrayList<NVP<String>>();
        nvp.add(new NVP<String>("System Default", null));
        
        type = InputBuilder.combo(",TV,Movie").bind(getData().getType()).widget();
        type.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                setVisibleItems();
            }
        });
        
        episodeTitle = InputBuilder.textbox().bind(getData().getEpisodeTitle()).widget();
        episode = InputBuilder.textbox().bind(getData().getEpisode()).widget();
        season = InputBuilder.textbox().bind(getData().getSeason()).widget();
        
        providers = InputBuilder.combo(nvp).bind(getData().getProvider()).widget();

        propPanel.add("Metadata Source", providers);
        propPanel.add("Search Type", type);
        propPanel.add("Search Title", InputBuilder.textbox().bind(getData().getSearchTitle()).widget());
        panel.add(propPanel);

        tvOptions = new TitlePanel("TV Options");
        propPanel = new Simple2ColFormLayoutPanel();
        propPanel.setWidth("100%");
        propPanel.add("Episode Title", episodeTitle);
        propPanel.add("Season", season);
        propPanel.add("Episode", episode);
        tvOptions.setContent(propPanel);
        panel.add(tvOptions);

        setVisibleItems();
        
        return panel;
    }
    
    public SearchQueryPanel(SearchQueryOptions options, DialogHandler<SearchQueryOptions> handler) {
        super("Search Options", options, handler);

        
        browserService.getProviders(new AsyncCallback<List<GWTProviderInfo>>() {
            public void onFailure(Throwable caught) {
                Log.error("Failed to get Metadata Sources!", caught);
            }

            public void onSuccess(List<GWTProviderInfo> result) {
                updateProviders(result);
            }
        });

        initPanels();
    }
    
    protected void updateProviders(List<GWTProviderInfo> result) {
        providers.clear();
        for (GWTProviderInfo pi : result) {
            providers.addItem(pi.getName() + " -- ("+pi.getId()+")", pi.getId());
        }
        providers.setSelectedIndex(0);
    }

    protected void setVisibleItems() {
        if ("TV".equals(type.getValue(type.getSelectedIndex()))) {
            tvOptions.setVisible(true);
        } else {
            tvOptions.setVisible(false);
        }
    }
}
