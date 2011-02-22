package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.ui.input.NVP;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.TitlePanel;
import org.jdna.bmt.web.client.ui.util.binder.DateBinder;
import org.jdna.bmt.web.client.ui.util.binder.FieldManager;
import org.jdna.bmt.web.client.ui.util.binder.ListBinder;
import org.jdna.bmt.web.client.ui.util.binder.NumberBinder;
import org.jdna.bmt.web.client.ui.util.binder.TextBinder;
import org.jdna.bmt.web.client.util.StringUtils;

import sagex.phoenix.metadata.MediaType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchQueryDialog extends DataDialog<SearchQueryOptions> implements DialogHandler<SearchQueryOptions> {
    private TitlePanel tvOptions;
    private VerticalPanel resultPanelContainer;
    private FlexTable table = new FlexTable();
    private String rowStyleNames[] = new String[] {"Row-Even","Row-Odd"};
    private GWTMediaFile file = null;

    private FieldManager fields = new FieldManager();
	private BrowsePanel controller;
    
    public SearchQueryDialog(BrowsePanel controller, GWTMediaFile file, SearchQueryOptions options) {
        super("Search Options", options, null);
        setHandler(this);
        this.controller=controller;
        this.file=file;
        
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
        
        fields.addField("type", new ListBinder(getData().getType(), ",TV,Movie"));
        ((ListBox)fields.getField("type").getWidget()).addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                setVisibleItems();
            }
        });
        fields.addField("episodeTitle", new TextBinder(getData().getEpisodeTitle()));
        fields.addField("episode", new NumberBinder(getData().getEpisode(), true));
        fields.addField("season", new NumberBinder(getData().getSeason(), true));
        fields.addField("source", new ListBinder(getData().getProvider()));
        fields.addField("search", new TextBinder(getData().getSearchTitle()));
        fields.addField("year", new NumberBinder(getData().getYear(), true));
        fields.addField("aired", new DateBinder(getData().getAiredDate(), "yyyy-MM-dd"));
        
        propPanel.add("Search Type", fields.getWidget("type"));
        propPanel.add("Source", fields.getWidget("source"));
        propPanel.add("Search Title", fields.getWidget("search"));
        propPanel.add("Year", fields.getWidget("year"));

        panel.add(propPanel);

        tvOptions = new TitlePanel("TV Options");
        propPanel = new Simple2ColFormLayoutPanel();
        propPanel.setWidth("100%");
        propPanel.add("Episode Title", fields.getWidget("episodeTitle"));
        propPanel.add("Season", fields.getWidget("season"));
        propPanel.add("Episode", fields.getWidget("episode"));
        propPanel.add("Aired Date (YYYY-MM-DD)", fields.getWidget("aired"));
        tvOptions.setContent(propPanel);
        panel.add(tvOptions);

        resultPanelContainer = new VerticalPanel();
        resultPanelContainer.setWidth("100%");
        resultPanelContainer.setHeight("100px");
        panel.add(resultPanelContainer);
        resultPanelContainer.setVisible(false);
        
        fields.updateFields();
        setVisibleItems();

        controller.getServices().getProviders(new AsyncCallback<List<GWTProviderInfo>>() {
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
        
        return panel;
    }
    
    
    protected void updateProviders(List<GWTProviderInfo> result) {
    	String type = fields.getField("type").getText();
    	MediaType mediaType = null;
    	if (type!=null) {
    		mediaType = MediaType.toMediaType(type);
    	}
    	String setSource = null;
        List<NVP<String>> list = new ArrayList<NVP<String>>();
        for (GWTProviderInfo pi : result) {
        	NVP<String> nvp = new NVP<String>(pi.getName() + " -- ("+pi.getId()+")", pi.getId());
        	list.add(nvp);
        	if (pi.getSupportedSearchTypes().contains(mediaType)) {
        		setSource = pi.getId();
        	}
        }
        
        ListBinder b = (ListBinder) fields.getField("source");
        b.setFieldValues(list);
        b.updateField();
        
        ListBox lb = (ListBox) b.getWidget();
        if (lb.getSelectedIndex()==-1) {
        	if (setSource!=null) {
        		b.setText(setSource);
        	} else {
        		lb.setSelectedIndex(0);
        	}
        }
        
        setVisibleItems();
    }

    protected void setVisibleItems() {
        if ("TV".equals(fields.getField("type").getText())) {
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
    	// validate
        if (StringUtils.isEmpty(fields.getField("search").getText())) {
        	Window.alert("Search Title is required");
        	return;
        }
        
        // validate that we have enough info
        if ("TV".equals(fields.getField("type").getText())) {
        	String ename = fields.getField("episodeTitle").getText();
        	String s = fields.getField("season").getText();
        	String e = fields.getField("episode").getText();
        	String aired = fields.getField("aired").getText();
        	
        	if ((StringUtils.isEmpty(s) || StringUtils.isEmpty(e)) && (StringUtils.isEmpty(ename) && StringUtils.isEmpty(aired))) {
        		Window.alert("Season and Episode are required if Episode Title or Aired Date is blank.");
        		return;
        	}
        }
    	
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
        
        // force the properties to be updates from the UI
        fields.updateProperties();
        
        controller.getServices().searchForMetadata(file, getData(), new AsyncCallback<List<GWTMediaSearchResult>>() {
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

        controller.getServices().getMetadata(res, options, new AsyncCallback<GWTMediaMetadata>() {
            public void onFailure(Throwable caught) {
                wait.hide();
                Application.fireErrorEvent("Unable to get Metadata", caught);
            }

            public void onSuccess(GWTMediaMetadata result) {
                file.attachMetadata(result);
                controller.metadataUpdated(file);
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

	@Override
	protected void updateButtonPanel(HorizontalPanel buttonPan) {
		PushButton pb = new PushButton("Discover Defaults");
		pb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				discoverDefaults();
			}
		});
		
		// add some spacing between this button and the search button
		pb.getElement().getStyle().setMarginRight(10, Unit.PX);
		buttonPan.insert(pb, 0);
	}

	protected void discoverDefaults() {
		controller.getServices().discoverQueryOptions(file, new AsyncCallback<SearchQueryOptions>() {
			@Override
			public void onFailure(Throwable caught) {
				Application.fireErrorEvent("Failed to get query options", caught);
			}

			@Override
			public void onSuccess(SearchQueryOptions result) {
				fields.getField("search").setText(result.getSearchTitle().get());
				fields.getField("year").setText(result.getYear().get());
				fields.getField("episodeTitle").setText(result.getEpisodeTitle().get());
				fields.getField("season").setText(result.getSeason().get());
				fields.getField("episode").setText(result.getEpisode().get());
				
				fields.getField("type").setText(result.getType().get());

				if (!StringUtils.isEmpty(result.getProvider().get())) {
					fields.getField("source").setText(result.getProvider().get());
				}
				
				fields.getField("aired").setText(result.getAiredDate().get());
			}
		});
	}
}
