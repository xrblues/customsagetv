package org.jdna.bmt.web.client.ui.browser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.input.LargeStringTextBox;
import org.jdna.bmt.web.client.ui.layout.FlowGrid;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.ImagePopupLabel;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.util.Log;
import org.jdna.bmt.web.client.util.StringUtils;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.MetadataKey;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditorMetadataPanel extends Composite {
    private final BrowserServiceAsync browserService  = GWT.create(BrowserService.class);

    private GWTMediaFile mediaFile = null;
    private GWTMediaMetadata metadata = null;
    
    private ScrollPanel scrollPanel = new ScrollPanel();
    private VerticalPanel scrolledDetails = new VerticalPanel();
    private VerticalPanel metadataPanel = new VerticalPanel();

    private AsyncCallback<GWTMediaFile> updateHandler;

    public MediaEditorMetadataPanel(GWTMediaFile mediaFile) {
        scrollPanel.setWidth("100%");
        scrolledDetails.setWidth("100%");
        metadataPanel.setWidth("100%");
        metadataPanel.setSpacing(5);
        
        initWidget(metadataPanel);
        init(mediaFile);
    }
    
    private void init(GWTMediaFile mf) {
        mediaFile = mf;
        metadata = mf.getMetadata();

        metadataPanel.clear();
        scrolledDetails.clear();
        scrollPanel.clear();
        metadataPanel.setWidth("100%");
        
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(5);

        Button save = new Button("Save");
        save.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                saveMetadata();
            }
        });
        
        Button find = new Button("Find Metadata");
        find.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                MetadataSearchResultsPanel.searchMetadataDialog(mediaFile, new AsyncCallback<GWTMediaMetadata>() {
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
        
        hp.add(find);
        hp.add(save);
        
        String width="96%";
        
        metadataPanel.add(hp);
        metadataPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
        
        metadataPanel.add(scrollPanel);
        metadataPanel.setCellHorizontalAlignment(scrollPanel, HasHorizontalAlignment.ALIGN_RIGHT);
        metadataPanel.setCellWidth(scrollPanel, "100%");

        
        // Metadata
        DisclosurePanel dp = new DisclosurePanel("Metadata", true);
        dp.setWidth(width);
        
        Simple2ColFormLayoutPanel panel = new Simple2ColFormLayoutPanel();
        panel.setWidth("99%");
        
        LargeStringTextBox tb = new LargeStringTextBox(InputBuilder.textbox().bind(mediaFile.getLocation().toURI()).widget(), "URI");
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

        
        scrollPanel.setWidget(scrolledDetails);
        
        System.out.println("Sage Media File: " + mediaFile.getLocation());
    }
    
    protected void saveMetadata() {
        final PopupPanel popup = Dialogs.showWaitingPopup("Saving...");
        
        browserService.saveMetadata(mediaFile, new AsyncCallback<ServiceReply<GWTMediaFile>>() {
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

    public void adjustSize(int windowWidth, int windowHeight) {
        // take away the decorations
        int width = windowWidth - scrollPanel.getAbsoluteLeft();
        if (width < 1) {
            width = 1;
        }

        int height = windowHeight - scrollPanel.getAbsoluteTop();
        if (height < 1) {
            height = 1;
        }
        
        System.out.println("MediaEditorMetadataPanel(): Adjusting Metadata Scroll Size: " + width + ";" + height);
        metadataPanel.setWidth(width + "px");
        scrollPanel.setHeight(height + "px");
    }

    public void setUpdateListener(AsyncCallback<GWTMediaFile> asyncCallback) {
        this.updateHandler = asyncCallback;
    }
}
