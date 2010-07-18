package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.input.NVP;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.TitlePanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchQueryDialog extends DataDialog<SearchQueryOptions> implements DialogHandler<SearchQueryOptions> {
    private ListBox providers;
    private ListBox type;
    private TextBox episodeTitle;
    private TextBox episode;
    private TextBox season;
    private TitlePanel tvOptions;
    
    private VerticalPanel resultPanelContainer;
    private FlexTable table = new FlexTable();
    private String rowStyleNames[] = new String[] {"Row-Even","Row-Odd"};
    private GWTMediaFile file = null;

    public SearchQueryDialog(GWTMediaFile file, SearchQueryOptions options) {
        super("Search Options", options, null);
        setHandler(this);
        this.file=file;
        
        BrowsingServicesManager.getInstance().getServices().getProviders(new AsyncCallback<List<GWTProviderInfo>>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to get Metadata Sources!", caught);
            }

            public void onSuccess(List<GWTProviderInfo> result) {
                if (result==null) {
                    Application.fireErrorEvent("Error: Unable to find any metadata providers.");
                    return;
                }
                updateProviders(result);
            }
        });

        initPanels();
        
        // don't close on save
        setCloseOnSave(false);
    }

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
        
        propPanel.add("Search Type", type);
        propPanel.add("Source", (providers=InputBuilder.combo("").bind(getData().getProvider()).widget()));
        propPanel.add("Search Title", InputBuilder.textbox().bind(getData().getSearchTitle()).widget());
        propPanel.add("Year", InputBuilder.textbox().bind(getData().getYear()).widget());

        panel.add(propPanel);

        tvOptions = new TitlePanel("TV Options");
        propPanel = new Simple2ColFormLayoutPanel();
        propPanel.setWidth("100%");
        propPanel.add("Episode Title", episodeTitle);
        propPanel.add("Season", season);
        propPanel.add("Episode", episode);
        tvOptions.setContent(propPanel);
        panel.add(tvOptions);

        resultPanelContainer = new VerticalPanel();
        resultPanelContainer.setWidth("100%");
        resultPanelContainer.setHeight("100px");
        panel.add(resultPanelContainer);
        resultPanelContainer.setVisible(false);
        
        setVisibleItems();
        
        return panel;
    }
    
    
    protected void updateProviders(List<GWTProviderInfo> result) {
        providers.clear();
        for (GWTProviderInfo pi : result) {
           providers.addItem(pi.getName() + " -- ("+pi.getId()+")", pi.getId());
           if ("TV".equals(type) && pi.getId()!=null && pi.getId().contains("tv")) {
               providers.setSelectedIndex(providers.getItemCount()-1);
           }
        }
        providers.setSelectedIndex(0);
    }

    protected void setVisibleItems() {
        if ("TV".equals(type.getValue(type.getSelectedIndex()))) {
            tvOptions.setVisible(true);
        } else {
            tvOptions.setVisible(false);
        }
        resultPanelContainer.setVisible(false);
    }

    public void onCancel() {
        // TODO Auto-generated method stub
    }

    public void onSave(SearchQueryOptions data) {
        // do the search...
        final PopupPanel wait = Dialogs.showWaitingPopup("Fetching Metadata...");
        
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("MetadataSearchResults");
        panel.setWidth("500px");
        panel.setSpacing(5);

        table.setWidth("100%");

        ScrollPanel sp = new ScrollPanel(table);
        sp.setHeight("100px");
        sp.setWidth("100%");
        panel.add(sp);

        resultPanelContainer.clear();
        resultPanelContainer.add(new Label("Results"));
        resultPanelContainer.add(panel);
        
        BrowsingServicesManager.getInstance().getServices().searchForMetadata(file, getData(), new AsyncCallback<List<GWTMediaSearchResult>>() {
            public void onFailure(Throwable caught) {
                wait.hide();
                resultPanelContainer.setVisible(false);
                Application.fireErrorEvent("Search Failed!", caught);
            }

            public void onSuccess(List<GWTMediaSearchResult> result) {
                wait.hide();
                loadResults(result);
            }
        });
    }
    
    
    protected void loadResults(List<GWTMediaSearchResult> results) {
        table.clear();
        table.removeAllRows();
        
        if (results==null) {
            resultPanelContainer.setVisible(false);
            Application.fireErrorEvent("Search Results are Empty");
            return;
        }
        
        if (results.size()==0) {
            resultPanelContainer.setVisible(false);
            Application.fireErrorEvent("No Results");
            return;
        }
        
        resultPanelContainer.setVisible(true);
        for (int i=0;i<results.size();i++) {
            final GWTMediaSearchResult res = results.get(i);
            Label label = new Label(res.getTitle());
            label.addStyleName("clickable");
            label.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    fetchMetadata(res); 
                }
            });
            table.setWidget(i, 0, label);
            table.setWidget(i, 1, new Label(String.valueOf(res.getYear())));
            table.setWidget(i, 2, new Label(res.getProviderId()));
            table.getRowFormatter().addStyleName(i, rowStyleNames[i%2]);
        }
        
        center();
    }
    
    protected void fetchMetadata(GWTMediaSearchResult res) {
        final PopupPanel wait = Dialogs.showWaitingPopup("Fetching Metadata...");

        GWTPersistenceOptions options = new GWTPersistenceOptions();
        options.setImportAsTV(file.getSageRecording().get());
        options.setUseTitleMasks(true);

        BrowsingServicesManager.getInstance().getServices().getMetadata(res, options, new AsyncCallback<GWTMediaMetadata>() {
            public void onFailure(Throwable caught) {
                wait.hide();
                Application.fireErrorEvent("Unable to get Metadata", caught);
            }

            public void onSuccess(GWTMediaMetadata result) {
                file.attachMetadata(result);
                BrowsingServicesManager.getInstance().metadataUpdated(file);
                wait.hide();
                hide();
            }
        });
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.DataDialog#userInitDialog()
     */
    @Override
    protected void userInitDialog() {
        super.userInitDialog();
        
        if (okButton != null) {
            okButton.setText("Search");
        }
    }
}
