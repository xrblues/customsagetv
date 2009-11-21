package org.jdna.bmt.web.client.ui.scan;

import java.util.List;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.OKDialogHandler;
import org.jdna.bmt.web.client.util.Log;

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

    private AsyncCallback<GWTMediaMetadata> callbackHandler;
    
    private FlexTable table = new FlexTable();
    
    private String rowStyleNames[] = new String[] {"Row-Even","Row-Odd"};

    private GWTMediaFile mediaItem;
    private GWTMediaMetadata metadata;
    
    public MetadataSearchResultsPanel(GWTMediaFile item, SearchQueryOptions options, ClickHandler cancelHandler, AsyncCallback<GWTMediaMetadata> handler) {
        super();

        this.mediaItem = item;
        this.metadata=item.getMetadata();
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

        browserService.searchForMetadata(item, options, new AsyncCallback<List<GWTMediaSearchResult>>() {
            public void onFailure(Throwable caught) {
                Log.error("Search Failed!", caught);
            }

            public void onSuccess(List<GWTMediaSearchResult> result) {
                loadResults(result);
            }
        });
    }

    protected void loadResults(List<GWTMediaSearchResult> results) {
        if (results==null) {
            Log.error("Search Results are Empty");
            return;
        }
        
        if (results.size()==0) {
            Log.error("0 Results");
        }
        
        for (int i=0;i<results.size();i++) {
            final GWTMediaSearchResult res = results.get(i);
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
    
    protected void fetchMetadata(GWTMediaSearchResult res) {
        final PopupPanel wait = Dialogs.showWaitingPopup("Fetching Metadata...");

        GWTPersistenceOptions options = new GWTPersistenceOptions();
        options.setImportAsTV(mediaItem.getSageRecording().get());
        options.setUseTitleMasks(true);

        browserService.getMetadata(res, options, new AsyncCallback<GWTMediaMetadata>() {
            public void onFailure(Throwable caught) {
                wait.hide();
                callbackHandler.onFailure(caught);
            }

            public void onSuccess(GWTMediaMetadata result) {
                wait.hide();
                mediaItem.attachMetadata(result);
                callbackHandler.onSuccess(result);
            }
        });
    }

    public static void searchMetadataDialog(final GWTMediaFile item, final SearchQueryOptions options, final AsyncCallback<GWTMediaMetadata> resultHandler) {
        DataDialog.showDialog(new SearchQueryPanel(options, new OKDialogHandler<SearchQueryOptions>() {
            public void onSave(SearchQueryOptions data) {
                searchMetadataResultsDialog(item, options, resultHandler);
            }
        }));
    }
    
    public static void searchMetadataResultsDialog(GWTMediaFile item, SearchQueryOptions options, final AsyncCallback<GWTMediaMetadata> resultHandler) {
        final DialogBox box = new DialogBox(false, true);
        box.setText("Search");
        box.setWidget(new MetadataSearchResultsPanel(item, options, new ClickHandler() {
            public void onClick(ClickEvent event) {
                box.hide();
            }
        }, new AsyncCallback<GWTMediaMetadata>() {
            public void onFailure(Throwable caught) {
                box.hide();
                resultHandler.onFailure(caught);
            }

            public void onSuccess(GWTMediaMetadata result) {
                box.hide();
                resultHandler.onSuccess(result);
            }
        }));
        box.center();
        box.show();
    }
}
