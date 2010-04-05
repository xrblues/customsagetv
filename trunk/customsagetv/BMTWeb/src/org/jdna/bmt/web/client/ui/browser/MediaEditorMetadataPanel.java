package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.ImagePopupLabel;
import org.jdna.bmt.web.client.ui.util.OKDialogHandler;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar.Layout;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.MetadataKey;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditorMetadataPanel extends Composite implements MetadataUpdatedHandler, ChangeHandler {
    private final MetadataServiceAsync browserService  = GWT.create(MetadataService.class);

    private GWTMediaFile mediaFile = null;
    private GWTMediaMetadata metadata = null;
    
    private VerticalPanel metadataPanel = new VerticalPanel();
    
    private Simple2ColFormLayoutPanel metadataContainer = null;
    private ListBox typeListBox=null;
    private List<Integer> hideRows = new ArrayList<Integer>();

    private AsyncCallback<GWTMediaFile> updateHandler;
    
    private HandlerRegistration metadataUpdatedHandler = null;
    
    private BrowserView browserView;
    private PersistenceOptionsUI options = new PersistenceOptionsUI();

    
    public MediaEditorMetadataPanel(GWTMediaFile mediaFile, BrowserView view) {
        this.browserView = view;
        metadataPanel.setWidth("100%");
        metadataPanel.setSpacing(5);
        WaitingPanel p = new WaitingPanel();
        metadataPanel.add(p);
        metadataPanel.setCellHorizontalAlignment(p, HasHorizontalAlignment.ALIGN_CENTER);
        metadataPanel.setCellVerticalAlignment(p, HasVerticalAlignment.ALIGN_MIDDLE);
        initWidget(metadataPanel);
        this.mediaFile = mediaFile;
    }
    
    private void init(GWTMediaFile mf) {
        mediaFile = mf;
        metadata = mf.getMetadata();

        metadataPanel.clear();
        metadataPanel.setWidth("100%");
        
        HorizontalButtonBar hp = new HorizontalButtonBar();

        Button saveFanart = new Button("Save ...");
        saveFanart.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                options.getIncludeSubDirs().setVisible(false);
                options.getImportTV().setVisible("TV".equals(typeListBox.getValue(typeListBox.getSelectedIndex())));
                if (options.getImportTV().isVisible()) {
                    options.getImportTV().set(mediaFile.getSageRecording().get());
                }
                DataDialog.showDialog(new SaveOptionsPanel(options, new OKDialogHandler<PersistenceOptionsUI>() {
                    public void onSave(PersistenceOptionsUI data) {
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
                SearchQueryOptions options = new SearchQueryOptions(mediaFile);
                DataDialog.showDialog(new SearchQueryDialog(mediaFile, options));
            }
        });

        Button back = new Button("Back");
        back.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                browserView.back();
            }
        });
        
        // add our icon to the panel
        final Image img = new Image();
        img.addErrorHandler(new ErrorHandler() {
            public void onError(ErrorEvent event) {
                img.setUrl("images/128x128/video.png");
            }
        });
        img.setUrl(mf.getThumbnailUrl());
        img.addStyleName("MediaMetadata-PosterSmall");
        img.addStyleName("clickable");
        img.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showFanartDialog();
            }
        });
        hp.add(img, Layout.Right);

        hp.add(back);
        hp.add(find);
        //hp.add(save);
        hp.add(saveFanart);

        
        metadataPanel.add(hp);
        metadataPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
        
        
        HorizontalPanel cols = new HorizontalPanel();
        cols.setWidth("100%");
        metadataPanel.add(cols);
        metadataPanel.setCellWidth(cols, "100%");
        
        // Metadata
        Simple2ColFormLayoutPanel panel = new Simple2ColFormLayoutPanel();
        panel.setWidth("99%");
        metadataContainer = panel;
        
        panel.add("Sage Recording?", InputBuilder.checkbox().bind(mediaFile.getSageRecording()).widget());
        panel.add("Media Type", typeListBox=InputBuilder.combo(",Movie,TV").bind(metadata.getProperty(MetadataKey.MEDIA_TYPE)).addChangeHandler(this).widget());

        panel.add("Display Title", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.DISPLAY_TITLE)).widget());
        panel.add("Show Title", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MEDIA_TITLE)).widget());
        panel.add("Episode Title", InputBuilder.textbox("tv-title").bind(metadata.getProperty(MetadataKey.EPISODE_TITLE)).widget());
        hideRows.add(panel.getFlexTable().getRowCount()-1);
        panel.add("Season #", InputBuilder.textbox("tv-season").bind(metadata.getProperty(MetadataKey.SEASON)).widget());
        hideRows.add(panel.getFlexTable().getRowCount()-1);
        panel.add("Episode #", InputBuilder.textbox("tv-episode").bind(metadata.getProperty(MetadataKey.EPISODE)).widget());
        hideRows.add(panel.getFlexTable().getRowCount()-1);

        TextArea ta = InputBuilder.textarea().bind(metadata.getProperty(MetadataKey.DESCRIPTION)).widget();
        ta.setWidth("300px");
        ta.setVisibleLines(5);
        panel.add("Description", ta);

        panel.add("Year", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.YEAR)).widget());
        panel.add("Release Date", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.RELEASE_DATE)).widget());
        
        panel.add("Disc #", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.DVD_DISC)).widget());

        panel.add("MPAA", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MPAA_RATING)).widget());
        panel.add("MPAA Description", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.MPAA_RATING_DESCRIPTION)).widget());
        panel.add("User Rating", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.USER_RATING)).widget());

        panel.add("Running Time", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.RUNNING_TIME)).widget());
        
        // Duration cannot be set
        //panel.add("Duration", InputBuilder.textbox().bind(metadata.getProperty(MetadataKey.DURATION)).widget());


        // Genres
        StringBuilder sb = new StringBuilder();
        List<String> genres = metadata.getGenres();
        if (genres!=null) {
            for (int row=0;row<genres.size();row++) {
                if (sb.length()>0) {
                    sb.append(" / ");
                }
                sb.append(genres.get(row));
            }
        }

        panel.add("Genre", new Label(sb.toString()));
        
        panel.add("Metadata Id", new Label(metadata.getProperty(MetadataKey.MEDIA_PROVIDER_DATA_ID).get()));
        panel.add("Media Id", new Label(String.valueOf(mediaFile.getSageMediaFileId())));
        panel.add("Airing Id", new Label(String.valueOf(mediaFile.getAiringId())));
        panel.add("Show Id", new Label(String.valueOf(mediaFile.getShowId())));
        panel.add("Location", new Label(mediaFile.getPath()));

        cols.add(panel);
        cols.setCellWidth(panel, "75%");
        

        // Fanart
        FlexTable grid = new FlexTable();
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
        
        panel.add("Fanart Location", new Label(mf.getFanartDir()));
        
        // Cast Members
        CastMemberPanel cmPanel = new CastMemberPanel(mf);
        cols.add(cmPanel);
        cols.setCellWidth(cmPanel, "25%");
        
        
        onChange(null);
    }

    protected void showFanartDialog() {
        Dialogs.showAsDialog("Fanart", new FanartManagerPanel(mediaFile));
    }

    private void saveFanart() {
        PersistenceOptionsUI options = new PersistenceOptionsUI();
        options.getOverwriteMetadata().set(false);
        options.getUpdateMetadata().set(false);
        saveMetadata(options);
    }

    protected void saveMetadata(PersistenceOptionsUI options) {
        MetadataServicesManager.getInstance().saveMetadata(mediaFile, options);
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

    public void onChange(ChangeEvent event) {
        for (int i : hideRows) {
            metadataContainer.getFlexTable().getRowFormatter().setVisible(i, "TV".equals(typeListBox.getValue(typeListBox.getSelectedIndex())));
        }
        metadataContainer.stripe();
    }
}
