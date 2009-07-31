package org.jdna.bmt.web.client.ui.browser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.input.LargeStringTextBox;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.util.Log;
import org.jdna.bmt.web.client.util.StringUtils;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.MetadataKey;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditorMetadataPanel extends Composite {
    private static final int FANART_TAB = 1;
    private static final int ACTORS_TAB = 2;
    private static final int PROPERTIES_TAB = 3;

    private final BrowserServiceAsync browserService  = GWT.create(BrowserService.class);

    private GWTMediaFile mediaFile = null;
    private GWTMediaMetadata metadata = null;
    private VerticalPanel metadataPanel = new VerticalPanel();
    private VerticalPanel fanartPanel = new VerticalPanel();
    private VerticalPanel propertiesPanel = new VerticalPanel();
    private VerticalPanel actorsPanel = new VerticalPanel();
    
    
    private DecoratedTabPanel metadataTabs = new DecoratedTabPanel();
    

    public MediaEditorMetadataPanel(GWTMediaFile mediaFile) {
        metadataTabs.setWidth("100%");
        
        metadataPanel.setWidth("100%");
        metadataPanel.setSpacing(5);
        
        metadataTabs.add(metadataPanel, "Metadata");
        metadataTabs.add(fanartPanel, "Fanart");
        metadataTabs.add(actorsPanel, "Cast");
        metadataTabs.add(propertiesPanel, "RAW Properties");

        metadataTabs.selectTab(0);
        
        metadataTabs.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                updatePanelData(event.getItem());
            }
        });
        
        initWidget(metadataTabs);

        init(mediaFile);
    }
    
    private void init(GWTMediaFile mf) {
        mediaFile = mf;
        metadata = mf.getMetadata();

        metadataPanel.clear();
        
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(5);
        
        Hyperlink link = new Hyperlink("Find Metadata","md");
        link.addClickHandler(new ClickHandler() {
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
        
        hp.add(link);
        
        metadataPanel.add(hp);
        metadataPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
        
        Simple2ColFormLayoutPanel panel = new Simple2ColFormLayoutPanel();
        panel.setWidth("100%");
        
        LargeStringTextBox tb = new LargeStringTextBox(InputBuilder.textbox().bind(mediaFile.getLocation().toURI()).widget(), "URI");
        tb.setReadOnly(true);
        
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

        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.GENRE_LIST)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.CAST_MEMBER_LIST)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.BACKGROUND_ART)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.BANNER_ART)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.MEDIA_ART_LIST)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.POSTER_ART)).widget());
        
        panel.add("MPAA", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MPAA_RATING)).widget());
        panel.add("MPAA Description", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MPAA_RATING_DESCRIPTION)).widget());
        panel.add("User Rating", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.USER_RATING)).widget());

        panel.add("Running Time", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.RUNNING_TIME)).widget());
        panel.add("Duration", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.DURATION)).widget());

        panel.add("Metadata Id", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MEDIA_PROVIDER_DATA_ID)).widget());
        panel.add("Media Id", new Label(String.valueOf(mediaFile.getSageMediaFileId())));
        panel.add("Uri", tb);

        //panel.add("Poster Count", new Label(metadata.getNonEditField(NonEditField.POSTER_COUNT).get()));
        //tb = new LargeStringTextBox(InputBuilder.textbox().bind(metadata.getNonEditField(NonEditField.POSTER_URI)).widget(), "Current Poster Folder");
        //tb.setReadOnly(true);
        //panel.add("Posters Folder", tb);
        
        //panel.add("Background Count", new Label(metadata.getNonEditField(NonEditField.BACKGROUND_COUNT).get()));
        //tb = new LargeStringTextBox(InputBuilder.textbox().bind(metadata.getNonEditField(NonEditField.BACKGROUND_URI)).widget(), "Current Background Folder");
        //tb.setReadOnly(true);
        //panel.add("Backgrounds Folder", tb);
        
        //panel.add("Banner Count", new Label(metadata.getNonEditField(NonEditField.BANNER_COUNT).get()));
        //tb = new LargeStringTextBox(InputBuilder.textbox().bind(metadata.getNonEditField(NonEditField.BANNER_URI)).widget(), "Current Banner Folder");
        //tb.setReadOnly(true);
        //panel.add("Banners Folder", tb);
        
        Button save = new Button("Save");
        save.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                saveMetadata();
            }
        });
        panel.add("", save);
        
        metadataPanel.add(panel);
        
        System.out.println("Sage Media File: " + mediaFile.getLocation());
    }

    
    protected void saveMetadata() {
        final PopupPanel popup = Dialogs.showWaitingPopup("Saving...");
        
        browserService.saveMetadata(mediaFile, new AsyncCallback<ServiceReply>() {
            public void onFailure(Throwable caught) {
                popup.hide();
                Log.error("Failed to save", caught);
            }

            public void onSuccess(ServiceReply result) {
                popup.hide();
                if (result.getCode()==0) {
                    Dialogs.showMessage("Saved");
                } else {
                    Log.error("Failed; Error: " + result.getCode() + "; Message: " + result.getMessage());
                }
            }
        });
    }

    protected void updatePanelData(int item) {
        if (item==ACTORS_TAB) {
            actorsPanel.clear();
            actorsPanel.setWidth("100%");
            actorsPanel.setSpacing(5);

            FlexTable grid = new FlexTable();
            grid.setWidth("100%");
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
            
            actorsPanel.add(grid);
        }
        
        if (item==FANART_TAB) {
            fanartPanel.clear();
            fanartPanel.setWidth("100%");
            fanartPanel.setSpacing(5);
            
            FlexTable grid = new FlexTable();
            List<IMediaArt> fanart = metadata.getFanart();
            if (fanart!=null) {
                for (int row=0;row<fanart.size();row++) {
                    IMediaArt ma = fanart.get(row);
                    grid.setText(row, 0, ma.getType().name());
                    grid.setText(row, 1, ma.getDownloadUrl());
                }
            }
        }
        
        if (item==PROPERTIES_TAB) {
        }
    }
}
