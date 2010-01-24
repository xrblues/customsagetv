package org.jdna.bmt.web.client.ui.scan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.browser.BrowserView;
import org.jdna.bmt.web.client.ui.browser.MetadataService;
import org.jdna.bmt.web.client.ui.browser.MetadataServiceAsync;
import org.jdna.bmt.web.client.ui.browser.MetadataServicesManager;
import org.jdna.bmt.web.client.ui.browser.MetadataUpdatedEvent;
import org.jdna.bmt.web.client.ui.browser.MetadataUpdatedHandler;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.input.LargeStringTextBox;
import org.jdna.bmt.web.client.ui.layout.FlowGrid;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.ImagePopupLabel;
import org.jdna.bmt.web.client.ui.util.OKDialogHandler;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.util.Log;
import org.jdna.bmt.web.client.util.StringUtils;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.MetadataKey;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditorMetadataPanel extends Composite implements MetadataUpdatedHandler {
    private final MetadataServiceAsync browserService  = GWT.create(MetadataService.class);

    private GWTMediaFile mediaFile = null;
    private GWTMediaMetadata metadata = null;
    
    private VerticalPanel scrolledDetails = new VerticalPanel();
    private VerticalPanel metadataPanel = new VerticalPanel();

    private AsyncCallback<GWTMediaFile> updateHandler;
    
    private HandlerRegistration metadataUpdatedHandler = null;

    private BrowserView browserView;
    
    public MediaEditorMetadataPanel(GWTMediaFile mediaFile, BrowserView view) {
        this.browserView = view;
        scrolledDetails.setWidth("100%");
        metadataPanel.setWidth("100%");
        metadataPanel.setSpacing(5);
        
        initWidget(metadataPanel);
        this.mediaFile = mediaFile;
    }
    
    private void init(GWTMediaFile mf) {
        mediaFile = mf;
        metadata = mf.getMetadata();

        metadataPanel.clear();
        scrolledDetails.clear();
        metadataPanel.setWidth("100%");
        
        HorizontalButtonBar hp = new HorizontalButtonBar();

        Button save = new Button("Save");
        save.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                saveMetadata(new SaveOptions());
            }
        });

        Button saveFanart = new Button("Save ...");
        saveFanart.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DataDialog.showDialog(new SaveOptionsPanel(new OKDialogHandler<SaveOptions>() {
                    public void onSave(SaveOptions data) {
                        if (data!=null) {
                            saveMetadata(data);
                        }
                    }
                }));
            }
        });
        
        Button find = new Button("Find Metadata ...");
        find.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //SearchQueryPanel.showDialog(mediaFile);
                SearchQueryOptions options = new SearchQueryOptions(mediaFile);
                MetadataSearchResultsPanel.searchMetadataDialog(mediaFile, options, new AsyncCallback<GWTMediaMetadata>() {
                    public void onFailure(Throwable caught) {
                        Log.error("Failed to update metadata!", caught);
                    }

                    public void onSuccess(GWTMediaMetadata result) {
                        mediaFile.attachMetadata(result);
                        init(mediaFile);
                    }
                });
            }
        });

        Button back = new Button("Back");
        back.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                browserView.back();
            }
        });
        
        hp.add(back);
        hp.add(find);
        hp.add(save);
        hp.add(saveFanart);
        
        String width="96%";
        
        metadataPanel.add(hp);
        metadataPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
        
        metadataPanel.add(scrolledDetails);
        metadataPanel.setCellHorizontalAlignment(scrolledDetails, HasHorizontalAlignment.ALIGN_RIGHT);
        metadataPanel.setCellWidth(scrolledDetails, "100%");

        
        // Metadata
        DisclosurePanel dp = new DisclosurePanel("Metadata", true);
        dp.setWidth(width);
        
        Simple2ColFormLayoutPanel panel = new Simple2ColFormLayoutPanel();
        panel.setWidth("99%");
        
        LargeStringTextBox tb = new LargeStringTextBox(InputBuilder.textbox().bind(mediaFile.getPath()).widget(), "PATH");
        tb.setReadOnly(true);
        
        panel.add("Sage Recording?", InputBuilder.checkbox().bind(mediaFile.getSageRecording()).widget());
        panel.add("Media Type", InputBuilder.combo(",Movie,TV").bind(metadata.getProperty(MetadataKey.MEDIA_TYPE)).widget());

        panel.add("Display Title", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.DISPLAY_TITLE)).widget());
        panel.add("Show Title", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MEDIA_TITLE)).widget());
        panel.add("Episode Title", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.EPISODE_TITLE)).widget());

        panel.add("Description", new LargeStringTextBox(InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.DESCRIPTION)).widget(), ""));

        panel.add("Year", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.YEAR)).widget());
        panel.add("Release Date", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.RELEASE_DATE)).widget());
        
        panel.add("Disc #", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.DVD_DISC)).widget());
        panel.add("Season #", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.SEASON)).widget());
        panel.add("Episode #", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.EPISODE)).widget());

        panel.add("MPAA", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MPAA_RATING)).widget());
        panel.add("MPAA Description", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MPAA_RATING_DESCRIPTION)).widget());
        panel.add("User Rating", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.USER_RATING)).widget());

        panel.add("Running Time", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.RUNNING_TIME)).widget());
        panel.add("Duration", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.DURATION)).widget());

        panel.add("Metadata Id", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MEDIA_PROVIDER_DATA_ID)).widget());
        panel.add("Media Id", new Label(String.valueOf(mediaFile.getSageMediaFileId())));
        panel.add("Airing Id", new Label(String.valueOf(mediaFile.getAiringId())));
        panel.add("Show Id", new Label(String.valueOf(mediaFile.getShowId())));
        panel.add("Uri", tb);

        dp.setContent(panel);
        scrolledDetails.add(dp);
        scrolledDetails.setCellWidth(dp, "100%");

        
        // Cast Members
        dp = new DisclosurePanel("Cast Members", false);
        dp.setWidth(width);
        FlexTable grid = new FlexTable();
        grid.setWidth("99%");
        grid.setCellPadding(3);
        
        Map<Integer,String> labels = new HashMap<Integer, String>();
        labels.put(ICastMember.ACTOR, "Actor");
        labels.put(ICastMember.WRITER, "Writer");
        labels.put(ICastMember.DIRECTOR, "Director");
        labels.put(ICastMember.OTHER, "Other");
        
        List<ICastMember> cmlist = metadata.getCastMembers();
        if (cmlist!=null) {
            for (int row=0;row<cmlist.size();row++) {
                ICastMember cm = cmlist.get(row);
                grid.setText(row, 0, labels.get(cm.getType()));
                grid.setText(row, 1, cm.getName());
                if (!StringUtils.isEmpty(cm.getPart())) {
                    grid.setText(row, 2, cm.getPart());
                }
            }
        }
        dp.setContent(grid);
        scrolledDetails.add(dp);
        scrolledDetails.setCellWidth(dp, "100%");
        
        
        // Fanart
        dp = new DisclosurePanel("Fanart", false);
        dp.setWidth(width);
        grid = new FlexTable();
        grid.setWidth("99%");
        grid.setCellPadding(3);
        List<IMediaArt> fanart = metadata.getFanart();
        if (fanart!=null) {
            for (int row=0;row<fanart.size();row++) {
                IMediaArt ma = fanart.get(row);
                grid.setText(row, 0, ma.getType().name());
                grid.setWidget(row, 1, new ImagePopupLabel(ma.getLabel(),ma.getDownloadUrl()) );
            }
        }
        
        // add in the default locations for media types
        DisclosurePanel dp2 = new DisclosurePanel("Default Fanart Files", false);
        dp2.setWidth("100%");
        FlowGrid loc = new FlowGrid(2);
        if (mediaFile.getDefaultPoster()!=null) {
            loc.add("Poster");
            loc.add(new ImagePopupLabel(mediaFile.getDefaultPoster().getLabel(), mediaFile.getDefaultPoster().getDownloadUrl()));
        }
        if (mediaFile.getDefaultBackground()!=null) {
            loc.add("Background");
            loc.add(new ImagePopupLabel(mediaFile.getDefaultBackground().getLabel(),mediaFile.getDefaultBackground().getDownloadUrl()));
        }
        if (mediaFile.getDefaultBanner()!=null) {
            loc.add("Banner");
            loc.add(new ImagePopupLabel(mediaFile.getDefaultBanner().getLabel(), mediaFile.getDefaultBanner().getDownloadUrl()));
        }
        dp2.add(loc);
        int grow = grid.getRowCount();
        grid.setWidget(grow, 0, dp2);
        grid.getFlexCellFormatter().setColSpan(grow, 0, 2);

        
        // add in default fanart dirs
        dp2 = new DisclosurePanel("Default Fanart Dirs", false);
        dp2.setWidth("100%");
        loc = new FlowGrid(2);
        if (mediaFile.getDefaultPosterDir()!=null) {
            loc.add("Posters Dir");
            loc.add(new Label(mediaFile.getDefaultPosterDir()));
        }
        if (mediaFile.getDefaultBackgroundDir()!=null) {
            loc.add("Backgrounds Dir");
            loc.add(new Label(mediaFile.getDefaultBackgroundDir()));
        }
        if (mediaFile.getDefaultBannerDir()!=null) {
            loc.add("Banners Dir");
            loc.add(new Label(mediaFile.getDefaultBannerDir()));
        }
        dp2.add(loc);
        grow = grid.getRowCount();
        grid.setWidget(grow, 0, dp2);
        grid.getFlexCellFormatter().setColSpan(grow, 0, 2);

        dp.setContent(grid);
        scrolledDetails.add(dp);
        scrolledDetails.setCellWidth(dp, "100%");

        // Genres
        dp = new DisclosurePanel("Genres", false);
        dp.setWidth(width);
        grid = new FlexTable();
        grid.setWidth("99%");
        grid.setCellPadding(3);
        List<String> genres = metadata.getGenres();
        if (genres!=null) {
            for (int row=0;row<genres.size();row++) {
                String genre = genres.get(row);
                grid.setText(row, 0, genre);
            }
        }

        dp.setContent(grid);
        scrolledDetails.add(dp);
        scrolledDetails.setCellWidth(dp, "100%");
    }

    private void saveFanart() {
        SaveOptions options = new SaveOptions();
        options.getOverwriteMetadata().set(false);
        options.getUpdateMetadata().set(false);
        saveMetadata(options);
    }

    protected void saveMetadata(SaveOptions options) {
        final PopupPanel popup = Dialogs.showWaitingPopup("Saving...");
        
        browserService.saveMetadata(mediaFile, options, new AsyncCallback<ServiceReply<GWTMediaFile>>() {
            public void onFailure(Throwable caught) {
                popup.hide();
                Log.error("Failed to save", caught);
            }

            public void onSuccess(ServiceReply<GWTMediaFile> result) {
                popup.hide();
                if (result.getCode()==0) {
                    Dialogs.showMessage("Saved");
                    if (result.getData()!=null) {
                        mediaFile = result.getData();
                        metadata = result.getData().getMetadata();
                        if (updateHandler!=null) {
                            updateHandler.onSuccess(mediaFile);
                        }
                        init(mediaFile);
                    }
                } else {
                    Log.error("Failed; Error: " + result.getCode() + "; Message: " + result.getMessage());
                }
            }
        });
    }

    public void setUpdateListener(AsyncCallback<GWTMediaFile> asyncCallback) {
        this.updateHandler = asyncCallback;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        metadataUpdatedHandler = Application.events().addHandler(MetadataUpdatedEvent.TYPE, this);
        MetadataServicesManager.getInstance().requestUpdatedMetadata(mediaFile);
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onDetach()
     */
    @Override
    protected void onDetach() {
        super.onDetach();
        metadataUpdatedHandler.removeHandler();
    }

    public void onMetadataUpdated(MetadataUpdatedEvent event) {
        if (event.getFile() == mediaFile) {
            // udpate
            metadata = event.getFile().getMetadata();
            init(event.getFile());
        }
    }
}
