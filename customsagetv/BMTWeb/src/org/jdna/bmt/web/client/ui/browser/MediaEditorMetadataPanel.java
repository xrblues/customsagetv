package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar.Layout;
import org.jdna.bmt.web.client.ui.util.ImagePopupLabel;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;
import org.jdna.bmt.web.client.ui.util.binder.CheckBinder;
import org.jdna.bmt.web.client.ui.util.binder.DateBinder;
import org.jdna.bmt.web.client.ui.util.binder.FieldManager;
import org.jdna.bmt.web.client.ui.util.binder.ListBinder;
import org.jdna.bmt.web.client.ui.util.binder.NumberBinder;
import org.jdna.bmt.web.client.ui.util.binder.TextAreaBinder;
import org.jdna.bmt.web.client.ui.util.binder.TextBinder;
import org.jdna.bmt.web.client.util.Property;

import sagex.phoenix.metadata.IMediaArt;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditorMetadataPanel extends Composite implements MetadataUpdatedHandler, ChangeHandler {
    private GWTMediaFile mediaFile = null;
    private GWTMediaMetadata metadata = null;
    
    private VerticalPanel metadataPanel = new VerticalPanel();
    
    private Simple2ColFormLayoutPanel metadataContainer = null;

    private List<Integer> tvRows = new ArrayList<Integer>();
    private List<Integer> movieRows = new ArrayList<Integer>();

    private HandlerRegistration metadataUpdatedHandler = null;
    
    private BrowserView browserView;

    private ListBinder typeListBox=null;
	private TextBinder movieTitle;
	private TextBinder showTitle;
	private TextBinder episodeName;
	private TextAreaBinder description;
	private NumberBinder year;
	private DateBinder originalAirDate;
	private TextBinder mpaaRatings;
	private TextBinder parentalRatings;
	private TextBinder extendedRatings;
	private NumberBinder runningTime;
	private TextBinder misc;
	private TextBinder externalId;
	private CheckBinder sageRecording;
	private CheckBinder archived;
	private CheckBinder watched;
	private TextBinder genres;
	
	private FieldManager fields = new FieldManager();
    
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

        sageRecording = (CheckBinder) fields.addField("sageRecording", new CheckBinder(mediaFile.getSageRecording()));
        panel.add("Sage Recording?", sageRecording.getWidget());

        typeListBox=(ListBinder) fields.addField("type", new ListBinder(metadata.getMediaType(),",Movie,TV"));
        ((ListBox) typeListBox.getWidget()).addChangeHandler(this);
        panel.add("Media Type", typeListBox.getWidget());
        
        panel.add("Fanart Title", fields.addField("fanart-title", new TextBinder(metadata.getMediaTitle())).getWidget());

        movieTitle=(TextBinder) fields.addField("movie-title", new TextBinder(metadata.getEpisodeName()));
        panel.add("Movie Title", movieTitle.getWidget());
        movieRows.add(panel.getFlexTable().getRowCount()-1);
        
        showTitle=(TextBinder) fields.addField("tv-title", new TextBinder(metadata.getTitle()));
        panel.add("Show Title", showTitle.getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);
        
        episodeName=(TextBinder) fields.addField("tv-episode-title", new TextBinder(metadata.getEpisodeName()));
        panel.add("Episode Name", episodeName.getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);
        
        panel.add("Season #", fields.addField("tv-season", new NumberBinder(metadata.getSeasonNumber(), true)).getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);
        
        panel.add("Episode #", fields.addField("tv-episode", new NumberBinder(metadata.getEpisodeNumber(), true)).getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);

        description = (TextAreaBinder) fields.addField("description", new TextAreaBinder(metadata.getDescription()));
        description.getWidget().setWidth("300px");
        ((TextArea) description.getWidget()).setVisibleLines(5);
        panel.add("Description", description.getWidget());

        year=(NumberBinder) fields.addField("year", new NumberBinder(metadata.getYear(), true));
        panel.add("Year", year.getWidget());
        movieRows.add(panel.getFlexTable().getRowCount()-1);
        
        genres = (TextBinder) fields.addField("genres", new TextBinder(metadata.getGenres()));
        panel.add("Genres (Comma Separated)", genres.getWidget());
        
        originalAirDate=(DateBinder) fields.addField("oad", new DateBinder(metadata.getOriginalAirDate(),"yyyy-MM-dd"));
        panel.add("Original Air Date", originalAirDate.getWidget());
        
        panel.add("Disc #", fields.addField("disc", new NumberBinder(metadata.getDiscNumber(),true)).getWidget());

        mpaaRatings=(TextBinder) fields.addField("mpaa", new TextBinder(metadata.getRated()));
        panel.add("MPAA Rating", mpaaRatings.getWidget());
        movieRows.add(panel.getFlexTable().getRowCount()-1);

        parentalRatings=(TextBinder) fields.addField("parental-ratings",new TextBinder(metadata.getParentalRating()));
        panel.add("TV Rating", parentalRatings.getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);

        extendedRatings=(TextBinder) fields.addField("extended-ratings", new TextBinder(metadata.getExtendedRatings()));
        panel.add("Extended Ratings", extendedRatings.getWidget());
        panel.add("User Rating", fields.addField("user-rating", new NumberBinder(metadata.getUserRating(), true)).getWidget());

        runningTime=(NumberBinder) fields.addField("running-time", new NumberBinder(metadata.getRunningTime()));
        panel.add("Running Time", runningTime.getWidget());
        
        misc=(TextBinder) fields.addField("misc", new TextBinder(metadata.getMisc()));
        panel.add("Misc", misc.getWidget());
        
        externalId=(TextBinder) fields.addField("externalid", new TextBinder(metadata.getExternalID()));
        panel.add("ExternalId", externalId.getWidget());
        
        archived=(CheckBinder) fields.addField("archived", new CheckBinder(mediaFile.getIsLibraryFile()));
        panel.add("Archived?", archived.getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);

        watched=(CheckBinder) fields.addField("watched",new CheckBinder(mediaFile.getIsWatched()));
        panel.add("Watched?", watched.getWidget());
        
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
        
        // update the UI
        fields.updateFields();
        
        onChange(null);
    }

    protected void showFanartDialog() {
    	Composite c = new FanartManagerPanel(mediaFile);
    	//c.setPixelSize(600, 400);
        Dialogs.showAsDialog("Fanart", c);
    }

    protected void saveMetadata(PersistenceOptionsUI options) {
    	// validate some fields
    	
    	// copy metadata
    	fields.updateProperties();
    	
        BrowsingServicesManager.getInstance().saveMetadata(mediaFile, options);
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
    	String mt = typeListBox.getText();
        for (int i : tvRows) {
            metadataContainer.getFlexTable().getRowFormatter().setVisible(i, "TV".equals(mt));
        }

        for (int i : movieRows) {
            metadataContainer.getFlexTable().getRowFormatter().setVisible(i, !"TV".equals(mt));
        }
        
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
