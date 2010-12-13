package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar.Layout;
import org.jdna.bmt.web.client.ui.util.ImagePopupLabel;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;
import org.jdna.bmt.web.client.util.Property;

import sagex.phoenix.metadata.IMediaArt;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditorMetadataPanel extends Composite implements MetadataUpdatedHandler, ChangeHandler {
    private GWTMediaFile mediaFile = null;
    private GWTMediaMetadata metadata = null;
    
    private VerticalPanel metadataPanel = new VerticalPanel();
    
    private Simple2ColFormLayoutPanel metadataContainer = null;
    private ListBox typeListBox=null;
    private List<Integer> hideRows = new ArrayList<Integer>();

    private AsyncCallback<GWTMediaFile> updateHandler;
    
    private HandlerRegistration metadataUpdatedHandler = null;
    
    private BrowserView browserView;

	private int movieTitleRow;
	private TextBox formattedTitle;
	private TextBox movieTitle;
	private TextBox showTitle;
	private TextBox episodeName;
	private TextArea description;
	private TextBox year;
	private TextBox originalAirDate;
	private TextBox parentalRatings;
	private TextBox extendedRatings;
	private TextBox runningTime;
	private TextBox misc;
	private TextBox externalId;
	private CheckBox sageRecording;
	private CheckBox archived;
	private CheckBox watched;
	
	private TextBox seriesInfoId;
	private TextBox vfsId;
    
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

        Button saveFanart = new Button("Save");
        saveFanart.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	saveMetadata(null);
            }
        });
        
        Button find = new Button("Find Metadata ...");
        find.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                SearchQueryOptions options = new SearchQueryOptions(mediaFile);
                DataDialog.showDialog(new SearchQueryDialog(mediaFile, options));
            }
        });

        Button clearMetadata = new Button("Clear Metadata");
        clearMetadata.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
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
        if (mf.getSageMediaFileId()>0) {
	        hp.add(find);
	        hp.add(saveFanart);
        }
        
        metadataPanel.add(hp);
        metadataPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
        
        if (metadata.getPreserveRecordingMetadata().get() && mediaFile.getSageRecording().get()) {
	        HorizontalPanel headerWidget = new HorizontalPanel();
	        headerWidget.setStyleName("DataDialog-HeaderBox");
	        headerWidget.setWidth("95%");
	        headerWidget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	        Label head = new Label("This is a Sage Recording and you have Preserve Recording Metadata enabled, so some fields are not editable.");
	        headerWidget.add(head);
	        headerWidget.setCellHorizontalAlignment(head, HasHorizontalAlignment.ALIGN_CENTER);
	        metadataPanel.add(headerWidget);
        }
        
        HorizontalPanel cols = new HorizontalPanel();
        cols.setWidth("100%");
        metadataPanel.add(cols);
        metadataPanel.setCellWidth(cols, "100%");
        
        // Metadata
        Simple2ColFormLayoutPanel panel = new Simple2ColFormLayoutPanel();
        panel.setWidth("99%");
        metadataContainer = panel;
        
        Label l = new Label(mediaFile.getFormattedTitle(), false);
        l.addStyleName("MediaMetadata-LargeTitle");
        metadataPanel.add(l);
        
        panel.add(l,new Label());

        panel.add("Sage Recording?", sageRecording=InputBuilder.checkbox().bind(mediaFile.getSageRecording()).widget());
        panel.add("Media Type", typeListBox=InputBuilder.combo(",Movie,TV").bind(metadata.getMediaType()).addChangeHandler(this).widget());
        panel.add("Fanart Title", InputBuilder.textbox().bind(metadata.getMediaTitle()).widget());
        panel.add("Movie Title", movieTitle=InputBuilder.textbox("movie-title").bind(metadata.getEpisodeName()).widget());
        movieTitleRow = panel.getFlexTable().getRowCount()-1;
        
        panel.add("Show Title", showTitle=InputBuilder.textbox("tv-title").bind(metadata.getTitle()).widget());
        hideRows.add(panel.getFlexTable().getRowCount()-1);
        panel.add("Episode Name", episodeName=InputBuilder.textbox("tv-episode-title").bind(metadata.getEpisodeName()).widget());
        hideRows.add(panel.getFlexTable().getRowCount()-1);
        panel.add("Season #", InputBuilder.textbox("tv-season").bind(metadata.getSeasonNumber()).widget());
        hideRows.add(panel.getFlexTable().getRowCount()-1);
        panel.add("Episode #", InputBuilder.textbox("tv-episode").bind(metadata.getEpisodeNumber()).widget());
        hideRows.add(panel.getFlexTable().getRowCount()-1);

        description = InputBuilder.textarea().bind(metadata.getDescription()).widget();
        description.setWidth("300px");
        description.setVisibleLines(5);
        panel.add("Description", description);

        panel.add("Year", year=InputBuilder.textbox().bind(metadata.getYear()).widget());
        panel.add("Original Air Date", originalAirDate=InputBuilder.textbox().bind(metadata.getOriginalAirDate()).widget());
        
        panel.add("Disc #", InputBuilder.textbox().bind(metadata.getDiscNumber()).widget());

        panel.add("Parental Rating", parentalRatings=InputBuilder.textbox().bind(metadata.getParentalRating()).widget());
        panel.add("Extended Ratings", extendedRatings=InputBuilder.textbox().bind(metadata.getExtendedRatings()).widget());
        panel.add("User Rating", InputBuilder.textbox().bind(metadata.getUserRating()).widget());

        panel.add("Running Time", runningTime=InputBuilder.textbox().bind(metadata.getRunningTime()).widget());
        panel.add("Misc", misc=InputBuilder.textbox().bind(metadata.getMisc()).widget());
        panel.add("ExternalId", externalId=InputBuilder.textbox().bind(metadata.getExternalID()).widget());
        panel.add("Archived?", archived=InputBuilder.checkbox().bind(mediaFile.getIsLibraryFile()).widget());
        panel.add("Watched?", watched=InputBuilder.checkbox().bind(mediaFile.getIsWatched()).widget());
        
        // Genres
        StringBuilder sb = new StringBuilder();
        List<Property<String>> genres = metadata.getGenres();
        if (genres!=null) {
            for (int row=0;row<genres.size();row++) {
                if (sb.length()>0) {
                    sb.append(" / ");
                }
                sb.append(genres.get(row).get());
            }
        }

        panel.add("Genre", new Label(sb.toString()));
        panel.add("IMDb Id", new Label(metadata.getIMDBID().get()));
        panel.add("Metadata Id", new Label(metadata.getMediaProviderDataID().get()));
        panel.add("Sage MediaFile Id", new Label(String.valueOf(mediaFile.getSageMediaFileId())));
        panel.add("Sage Airing Id", new Label(String.valueOf(mediaFile.getAiringId())));
        panel.add("Sage Show Id", new Label(String.valueOf(mediaFile.getShowId())));
        panel.add("Sage Series Info Id", new Label(mediaFile.getSeriesInfoId()));
        panel.add("VFS Id", new Label(mediaFile.getVFSID()));
        panel.add("Location", new Label(mediaFile.getPath()));

        cols.add(panel);
        cols.setCellWidth(panel, "75%");

        panel.add("Fanart Location", new Label(mf.getFanartDir()));
        List<GWTMediaArt> fanart = metadata.getFanart();
        if (fanart!=null) {
            for (int row=0;row<fanart.size();row++) {
                IMediaArt ma = fanart.get(row);
                panel.add("Fanart " + ma.getType().name(),  new ImagePopupLabel(ma.getDownloadUrl(), ma.getDownloadUrl()));
            }
        }
        
        // Cast Members
        CastMemberPanel cmPanel = new CastMemberPanel(mf);
        cols.add(cmPanel);
        cols.setCellWidth(cmPanel, "25%");
        
        
        onChange(null);
    }

    protected void showFanartDialog() {
    	Composite c = new FanartManagerPanel(mediaFile);
    	//c.setPixelSize(600, 400);
        Dialogs.showAsDialog("Fanart", c);
    }

    protected void saveMetadata(PersistenceOptionsUI options) {
        BrowsingServicesManager.getInstance().saveMetadata(mediaFile, options);
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
        BrowsingServicesManager.getInstance().requestUpdatedMetadata(mediaFile);
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
        // udpate
        metadata = event.getFile().getMetadata();
        init(event.getFile());
    }

    public void onChange(ChangeEvent event) {
        for (int i : hideRows) {
            metadataContainer.getFlexTable().getRowFormatter().setVisible(i, "TV".equals(typeListBox.getValue(typeListBox.getSelectedIndex())));
        }
        metadataContainer.getFlexTable().getRowFormatter().setVisible(movieTitleRow, !"TV".equals(typeListBox.getValue(typeListBox.getSelectedIndex())));
        
        if (mediaFile!=null && metadata!=null && metadata.getPreserveRecordingMetadata().get()) {
	        // set the readonly fields
	    	movieTitle.setEnabled(!mediaFile.getSageRecording().get());
	    	//showTitle.setEnabled(!mediaFile.getSageRecording().get());
	    	episodeName.setEnabled(!mediaFile.getSageRecording().get());
	    	description.setEnabled(!mediaFile.getSageRecording().get());
	    	year.setEnabled(!mediaFile.getSageRecording().get());
	    	originalAirDate.setEnabled(!mediaFile.getSageRecording().get());
	    	parentalRatings.setEnabled(!mediaFile.getSageRecording().get());
	    	extendedRatings.setEnabled(!mediaFile.getSageRecording().get());
	    	runningTime.setEnabled(!mediaFile.getSageRecording().get());
	    	misc.setEnabled(!mediaFile.getSageRecording().get());
	    	externalId.setEnabled(!mediaFile.getSageRecording().get());
        }
        
        if (mediaFile!=null && mediaFile.getSageRecording().get()) {
	    	externalId.setEnabled(false);
	    	archived.setEnabled(true);
        } else {
	    	archived.setEnabled(false);
        }
        
        //sageRecording.setEnabled(false);
        metadataContainer.stripe();
    }
}
