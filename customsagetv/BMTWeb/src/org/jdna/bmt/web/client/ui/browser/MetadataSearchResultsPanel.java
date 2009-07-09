package org.jdna.bmt.web.client.ui.browser;

import java.util.List;

import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.util.Log;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MetadataSearchResultsPanel extends Composite {
    private final BrowserServiceAsync browserService = GWT.create(BrowserService.class);

    private SearchQuery query = null;
    private String providers = null;
    
    private AsyncCallback<MediaItem> callbackHandler;
    
    private FlexTable table = new FlexTable();
    
    private String rowStyleNames[] = new String[] {"Row-Even","Row-Odd"};

    private MediaItem mediaItem;
    
    public MetadataSearchResultsPanel(MediaItem item, String providerId, ClickHandler cancelHandler, AsyncCallback<MediaItem> handler) {
        super();

        this.mediaItem = item;
        this.providers=providerId;
        this.callbackHandler=handler;
        
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("MetadataSearchResults");
        panel.setWidth("500px");
        panel.setSpacing(5);

        table.setWidth("100%");

        ScrollPanel sp = new ScrollPanel(table);
        sp.setHeight("300px");
        sp.setWidth("100%");
        panel.add(sp);
        
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.setSpacing(5);
        
        Button cancel = new Button("Cancel", cancelHandler);
        buttons.add(cancel);
        
        panel.add(buttons);
        panel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);
        
        initWidget(panel);

        browserService.searchForMetadata(item, providerId, new AsyncCallback<List<MediaSearchResult>>() {
            public void onFailure(Throwable caught) {
                Log.error("Search Failed: " + query.get(Field.TITLE), caught);
            }

            public void onSuccess(List<MediaSearchResult> result) {
                loadResults(result);
            }
        });
    }

    protected void loadResults(List<MediaSearchResult> results) {
        if (results==null) {
            Log.error("Search Results are Empty");
            return;
        }
        
        if (results.size()==0) {
            Log.error("0 Results");
        }
        
        for (int i=0;i<results.size();i++) {
            final MediaSearchResult res = results.get(i);
            Hyperlink link = new Hyperlink(res.getTitle(), "metadata/"+ res.getTitle());
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    fetchMetadata(res); 
                }
            });
            table.setWidget(i, 0, link);
            table.setWidget(i, 1, new Label(res.getYear()));
            table.setWidget(i, 2, new Label(res.getProviderId()));
            table.getRowFormatter().addStyleName(i, rowStyleNames[i%2]);
        }
    }
    
    protected void fetchMetadata(MediaSearchResult res) {
        final PopupPanel wait = Dialogs.showWaitingPopup("Fetching Metadata...");
        browserService.getMediaItem(res, new AsyncCallback<MediaItem>() {
            public void onFailure(Throwable caught) {
                wait.hide();
                callbackHandler.onFailure(caught);
            }

            public void onSuccess(MediaItem result) {
                wait.hide();
                callbackHandler.onSuccess(result);
            }
        });
    }

    public static void searchMetadataDialog(MediaItem item, final AsyncCallback<MediaItem> resultHandler) {
        final DialogBox box = new DialogBox(false, true);
        box.setText("Search");
        box.setWidget(new MetadataSearchResultsPanel(item, null, new ClickHandler() {
            public void onClick(ClickEvent event) {
                box.hide();
            }
        }, new AsyncCallback<MediaItem>() {
            public void onFailure(Throwable caught) {
                box.hide();
                resultHandler.onFailure(caught);
            }

            public void onSuccess(MediaItem result) {
                box.hide();
                resultHandler.onSuccess(result);
            }
        }));
        box.center();
        box.show();
    }
}
