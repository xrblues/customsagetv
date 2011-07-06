package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.ui.HTMLTemplates;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar.Layout;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;
import org.jdna.bmt.web.client.ui.util.binder.CheckBinder;
import org.jdna.bmt.web.client.ui.util.binder.DateBinder;
import org.jdna.bmt.web.client.ui.util.binder.FieldManager;
import org.jdna.bmt.web.client.ui.util.binder.ListBinder;
import org.jdna.bmt.web.client.ui.util.binder.NumberBinder;
import org.jdna.bmt.web.client.ui.util.binder.TextAreaBinder;
import org.jdna.bmt.web.client.ui.util.binder.TextBinder;
import org.jdna.bmt.web.client.util.DateFormatUtil;
import org.jdna.bmt.web.client.util.MessageHandler;
import org.jdna.bmt.web.client.util.NumberUtil;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MediaEditorMetadataPanel extends Composite implements ChangeHandler, HasMediaFile, MessageHandler {
    private GWTMediaFile mediaFile = null;
    private GWTMediaMetadata metadata = null;
    
    private VerticalPanel metadataPanel = new VerticalPanel();
    
    private Simple2ColFormLayoutPanel metadataContainer = null;

    private List<Integer> tvRows = new ArrayList<Integer>();
    private List<Integer> movieRows = new ArrayList<Integer>();

    private static final HTMLTemplates templates = GWT.create(HTMLTemplates.class);
    
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
	private TextBinder runningTime;
	private TextBinder misc;
	private TextBinder externalId;
	private CheckBinder sageRecording;
	private CheckBinder archived;
	private CheckBinder watched;
	private TextBinder genres;

	private CheckBinder preserveMetadata;
	
	private FieldManager fields = new FieldManager();
	private BrowsePanel controller;

	private Image posterImage = new Image();
    
	DecoratedTabPanel tabs = new DecoratedTabPanel();
	private Button saveFanart;
	private Button saveNextFanart;
    public MediaEditorMetadataPanel(GWTMediaFile mediaFile, BrowsePanel controller) {
		controller.getMessageBus().postMessage(BrowsePanel.MSG_HIDE_VIEWS);

		History.newItem("editmetadata", false);
		
        this.controller = controller;
        metadataPanel.setWidth("100%");
        metadataPanel.setSpacing(5);
        WaitingPanel p = new WaitingPanel();
        metadataPanel.add(p);
        metadataPanel.setCellHorizontalAlignment(p, HasHorizontalAlignment.ALIGN_CENTER);
        metadataPanel.setCellVerticalAlignment(p, HasVerticalAlignment.ALIGN_MIDDLE);
        initWidget(metadataPanel);
        this.mediaFile = mediaFile;

        tabs.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				int tab = event.getItem();
				Widget w = tabs.getWidget(tab);
				if (w instanceof Label) {
					if (tab == 2) {
						tabs.remove(tab);
						tabs.insert(new FanartManagerPanel(MediaEditorMetadataPanel.this.controller, MediaEditorMetadataPanel.this.mediaFile), "Fanart", tab);
					} else if (tab == 3) {
						tabs.remove(tab);
						tabs.insert(new PropertiesPanel(MediaEditorMetadataPanel.this.mediaFile), "Properties File", tab);
					}
				}
			}
		});
        
        controller.requestUpdatedMetadata(mediaFile, this);
    }
    
    private void init(GWTMediaFile mf) {
    	int selected = tabs.getTabBar().getSelectedTab();
    	if (selected < 0) selected=0;
    	tabs.clear();
    	
        mediaFile = mf;
        metadata = mf.getMetadata();

        metadataPanel.clear();
        metadataPanel.setWidth("100%");

        HorizontalButtonBar hp = new HorizontalButtonBar();

        saveFanart = new Button("Save");
        saveFanart.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	saveMetadata(null);
            }
        });

        saveNextFanart = new Button("Save and Next");
        saveNextFanart.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	saveMetadataAndMoveNext(null);
            }
        });
        
        Button find = new Button("Find Metadata ...");
        find.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                SearchQueryOptions options = new SearchQueryOptions(mediaFile);
                DataDialog.showDialog(new SearchQueryDialog(controller, mediaFile, options));
            }
        });

        Button rawMetadata = new Button("Edit Raw Metadata");
        rawMetadata.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	controller.editraw(mediaFile);
            }
        });

        Button newMediatitles = new Button("Add Matcher");
        newMediatitles.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	controller.addmatcher(mediaFile);
            }
        });
        
        Button back = new Button("Back");
        back.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
        		controller.getMessageBus().postMessage(BrowsePanel.MSG_SHOW_VIEWS);
                controller.back();
            }
        });

        Button next = new Button("Next");
        next.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	next();
            }
        });
        Button previous = new Button("Previous");
        previous.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	previous();
            }
        });
        
        // add our icon to the panel
        posterImage.addErrorHandler(new ErrorHandler() {
            public void onError(ErrorEvent event) {
                posterImage.setUrl("images/128x128/video.png");
            }
        });
        posterImage.setUrl(mf.getThumbnailUrl());
        posterImage.addStyleName("MediaMetadata-PosterSmall");
        posterImage.addStyleName("clickable");
        posterImage.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showFanartDialog();
            }
        });
        hp.add(posterImage, Layout.Right);

        hp.add(back);
        hp.add(previous);
        hp.add(next);
        hp.add(find);
        hp.add(rawMetadata);
        hp.add(newMediatitles);
        hp.add(saveFanart);
        hp.add(saveNextFanart);
        
        metadataPanel.add(hp);
        metadataPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);

        
        HorizontalPanel titleArea = new HorizontalPanel();
        titleArea.setSpacing(5);
        
        Label l = new Label(mediaFile.getFormattedTitle(), false);
        l.addStyleName("MediaMetadata-LargeTitle");
        titleArea.add(l);
        
        if (!StringUtils.isEmpty(metadata.getIMDBID().get())) {
        	String url = "http://www.imdb.com/title/"+metadata.getIMDBID().get()+"/";
        	HTMLPanel p = new HTMLPanel(templates.createMetadataPunchout("IMDb", url, "imdb"));
        	titleArea.add(p);
        }
        
        if (!StringUtils.isEmpty(metadata.getMediaProviderID().get())) {
        	String url = null;
        	String label = null;
        	if ("tmdb".equals(metadata.getMediaProviderID().get())) {
        		label = "TMDb";
        		url = "http://www.themoviedb.org/movie/" + metadata.getMediaProviderDataID().get();
        	} else if ("tvdb".equals(metadata.getMediaProviderID().get())) {
            	label = "TheTVDB";
            	url = "http://thetvdb.com/?tab=series&id="+metadata.getMediaProviderDataID().get();
        	} else {
        		GWT.log("Unknown Provider: " + metadata.getMediaProviderID().get());
        	}
        	
        	if (url!=null) {
            	HTMLPanel p = new HTMLPanel(templates.createMetadataPunchout(label, url, "_"+"label"));
            	titleArea.add(p);
        	}
        }
        
        metadataPanel.add(titleArea);
        
        metadataPanel.add(tabs);
        
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
        
//        HorizontalPanel cols = new HorizontalPanel();
//        cols.setWidth("100%");
//        metadataPanel.add(cols);
//        metadataPanel.setCellWidth(cols, "100%");
        tabs.setWidth("100%");
        tabs.getDeckPanel().setWidth("100%");
        tabs.getDeckPanel().getElement().getStyle().setBorderStyle(BorderStyle.NONE);
        //tabs.getDeckPanel().getElement().getStyle().setProperty("border-top", "2px solid black");

        // Metadata
        Simple2ColFormLayoutPanel panel = new Simple2ColFormLayoutPanel();
        panel.setWidth("100%");
        metadataContainer = panel;

        if (mediaFile.getSageRecording().get()) {
	        preserveMetadata = (CheckBinder) fields.addField("preserveRecording", new CheckBinder(metadata.getPreserveRecordingMetadata()));
	        ((CheckBox)preserveMetadata.getWidget()).addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					preserveMetadata.updateProperty();
					onChange(null);
				}
			});
	        panel.add("Preserve Original Metadata?", preserveMetadata.getWidget());
        }

        sageRecording = (CheckBinder) fields.addField("sageRecording", new CheckBinder(mediaFile.getSageRecording()));
        panel.add("Sage Recording?", sageRecording.getWidget());

        typeListBox=(ListBinder) fields.addField("type", new ListBinder(metadata.getMediaType(),",Movie,TV"));
        ((ListBox) typeListBox.getWidget()).addChangeHandler(this);
        panel.add("Media Type", typeListBox.getWidget());
        
        panel.add("Fanart Title", fields.addField("fanart-title", new TextBinder(metadata.getMediaTitle())).addStyle("MetadataInput").getWidget());

        showTitle=(TextBinder) fields.addField("tv-title", new TextBinder(metadata.getTitle())).addStyle("MetadataInput");
        panel.add("Show Title", showTitle.getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);
        
        episodeName=(TextBinder) fields.addField("episode-name", new TextBinder(metadata.getEpisodeName())).addStyle("MetadataInput");
        panel.add("Episode Name", episodeName.getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);
        
        movieTitle=(TextBinder) fields.addField("movie-title", new TextBinder(metadata.getEpisodeName())).addStyle("MetadataInput");
        panel.add("Movie Title", movieTitle.getWidget());
        movieRows.add(panel.getFlexTable().getRowCount()-1);
        
        panel.add("Season #", fields.addField("tv-season", new NumberBinder(metadata.getSeasonNumber(), true)).getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);
        
        panel.add("Episode #", fields.addField("tv-episode", new NumberBinder(metadata.getEpisodeNumber(), true)).getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);

        description = (TextAreaBinder) fields.addField("description", new TextAreaBinder(metadata.getDescription())).addStyle("MetadataInput");
        ((TextArea) description.getWidget()).setVisibleLines(5);
        panel.add("Description", description.getWidget());

        year=(NumberBinder) fields.addField("year", new NumberBinder(metadata.getYear(), true));
        panel.add("Year", year.getWidget());
        movieRows.add(panel.getFlexTable().getRowCount()-1);
        
        genres = (TextBinder) fields.addField("genres", new TextBinder(metadata.getGenres()));
        panel.add("Genres (Separate with forward Slash '/')", genres.getWidget());
        
        originalAirDate=(DateBinder) fields.addField("oad", new DateBinder(metadata.getOriginalAirDate(),"yyyy-MM-dd HH:mm"));
        panel.add("Original Air Date (YYY-MM-DD hh:mm)", originalAirDate.getWidget());
        
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

        metadata.getRunningTime().set(DateFormatUtil.formatDuration(NumberUtil.toLong(metadata.getRunningTime().get(),0)));
        
        runningTime=(TextBinder) fields.addField("running-time", new TextBinder(metadata.getRunningTime()));
        panel.add("Running Time ('### min' or exact milliseconds)", runningTime.getWidget());
        
        misc=(TextBinder) fields.addField("misc", new TextBinder(metadata.getMisc()));
        panel.add("Misc", misc.getWidget());
        
        externalId=(TextBinder) fields.addField("externalid", new TextBinder(metadata.getExternalID()));
        panel.add("ExternalId", externalId.getWidget());
        ((Label)panel.getLabelWidget(panel.getRowCount()-1)).addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				externalId.setEnabled(true);
			}
		});
        
        archived=(CheckBinder) fields.addField("archived", new CheckBinder(mediaFile.getIsLibraryFile()));
        panel.add("Archived?", archived.getWidget());
        tvRows.add(panel.getFlexTable().getRowCount()-1);

        watched=(CheckBinder) fields.addField("watched",new CheckBinder(mediaFile.getIsWatched()));
        panel.add("Watched?", watched.getWidget());
        
        if (!StringUtils.isEmpty(metadata.getIMDBID().get())) {
        	panel.add("IMDb Id", new HTMLPanel(templates.createIMDBPunchout(metadata.getIMDBID().get(), metadata.getIMDBID().get())));
        }
        
        if (!StringUtils.isEmpty(metadata.getMediaProviderDataID().get())) {
        	Widget p = null;
        	if ("imdb".equals(metadata.getMediaProviderID().get())) {
        		p = new HTMLPanel(templates.createIMDBPunchout(metadata.getMediaProviderDataID().get(), metadata.getMediaProviderDataID().get()));
        	} else if ("tvdb".equals(metadata.getMediaProviderID().get())) {
        		p = new HTMLPanel(templates.createTVDBPunchout(metadata.getMediaProviderDataID().get(), metadata.getMediaProviderDataID().get()));
        	} else if ("tmdb".equals(metadata.getMediaProviderID().get())) {
        		p = new HTMLPanel(templates.createTMDBPunchout(metadata.getMediaProviderDataID().get(), metadata.getMediaProviderDataID().get()));
        	} else {
        		p = new Label(metadata.getMediaProviderDataID().get());
        	}
        	panel.add("Metadata Id", p);
        }
        
        panel.add("Sage MediaFile Id", new Label(String.valueOf(mediaFile.getSageMediaFileId())));
        panel.add("Sage Airing Id", new Label(String.valueOf(mediaFile.getAiringId())));
        panel.add("Sage Show Id", new Label(String.valueOf(mediaFile.getShowId())));
        panel.add("Sage Series Info Id", new Label(mediaFile.getSeriesInfoId()));
        panel.add("VFS Id", new Label(mediaFile.getVFSID()));
        panel.add("Location", new Label(mediaFile.getPath()));

        tabs.add(panel, "Metadata");
        
        
        //cols.add(panel);
        //cols.setCellWidth(panel, "75%");

        panel.add("Fanart Location", new Label(mf.getFanartDir()));
//        List<GWTMediaArt> fanart = metadata.getFanart();
//        if (fanart!=null) {
//            for (int row=0;row<fanart.size();row++) {
//                IMediaArt ma = fanart.get(row);
//                panel.add("Fanart " + ma.getType().name(),  new ImagePopupLabel(ma.getDownloadUrl(), ma.getDownloadUrl()));
//            }
//        }
        
        // Cast Members
        CastMemberPanel cmPanel = new CastMemberPanel(mf);
        //cols.add(cmPanel);
        //cols.setCellWidth(cmPanel, "25%");
        tabs.add(cmPanel, "Cast");
        
        //FanartManagerPanel mgr = new FanartManagerPanel(controller, mf);
        //mgr.setWidth("100%");
        tabs.add(new Label(), "Fanart");
        tabs.add(new Label(), "Properties File");
        tabs.selectTab(selected, true);
        
        // update the UI
        fields.updateFields();
        
        onChange(null);
    }

	protected void showFanartDialog() {
		tabs.selectTab(2, true);
    }

    protected void saveMetadata(PersistenceOptionsUI options) {
    	// validate some fields
    	
    	// copy metadata
    	fields.updateProperties();
    	
        controller.saveMetadata(mediaFile, options, this);
    }

    protected void saveMetadataAndMoveNext(PersistenceOptionsUI options) {
    	fields.updateProperties();
        controller.saveMetadata(mediaFile, options, this, true);
    	next();
	}
    
    protected void next() {
    	GWTMediaResource r = controller.getFolder().next(mediaFile);
    	if (r instanceof GWTMediaFile) {
    		controller.requestUpdatedMetadata((GWTMediaFile) r, this);
    	} else {
    		Application.fireNotification("No more files");
    	}
    }

    protected void previous() {
    	GWTMediaResource r = controller.getFolder().previous(mediaFile);
    	if (r instanceof GWTMediaFile) {
    		controller.requestUpdatedMetadata((GWTMediaFile) r, this);
    	} else {
    		Application.fireNotification("No previous file");
    	}
    }

    public void onChange(ChangeEvent event) {
    	String mt = typeListBox.getText();
        for (int i : tvRows) {
            metadataContainer.getFlexTable().getRowFormatter().setVisible(i, "TV".equals(mt));
        }

        for (int i : movieRows) {
            metadataContainer.getFlexTable().getRowFormatter().setVisible(i, !"TV".equals(mt));
        }
        
        episodeName.setEnabled("TV".equals(mt));
    	movieTitle.setEnabled(!"TV".equals(mt));
        
    	if (mediaFile==null || metadata==null) return;
    	
    	if (mediaFile.getSageRecording().get()) {
        	boolean preserve = metadata.getPreserveRecordingMetadata().get();
	        // set the readonly fields
	    	movieTitle.setEnabled(!"TV".equals(mt) && !preserve);
	    	episodeName.setEnabled("TV".equals(mt) && !preserve);
	    	
	    	showTitle.setEnabled(!preserve);
	    	description.setEnabled(!preserve);
	    	year.setEnabled(!preserve);
	    	originalAirDate.setEnabled(!preserve);
	    	parentalRatings.setEnabled(!preserve);
	    	extendedRatings.setEnabled(!preserve);
	    	runningTime.setEnabled(!preserve);
	    	misc.setEnabled(!preserve);
	    	externalId.setEnabled(!preserve);
	    	genres.setEnabled(!preserve);

	    	externalId.setEnabled(false);
	    	archived.setEnabled(true);
        } else {
	    	archived.setEnabled(false);
        }
        
        metadataContainer.stripe();
    }

	@Override
	public void setMediaFile(GWTMediaFile file) {
		init(file);
	}

	@Override
	public void onMessageReceived(String msg, Map<String, ?> args) {
		if (BrowsePanel.MSG_METADATA_CHANGED.equals(msg)) {
			setMediaFile((GWTMediaFile) args.get("mediafile"));
		} else if (BrowsePanel.MSG_POSTER_UPDATED.equals(msg)) {
			GWTMediaArt file = (GWTMediaArt) args.get("poster");
			if (file!=null) {
				posterImage.setUrl(file.getDisplayUrl());
			}
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		controller.getMessageBus().addHandler(BrowsePanel.MSG_METADATA_CHANGED, this);
		controller.getMessageBus().addHandler(BrowsePanel.MSG_POSTER_UPDATED, this);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		controller.getMessageBus().removeHandler(BrowsePanel.MSG_METADATA_CHANGED, this);
		controller.getMessageBus().removeHandler(BrowsePanel.MSG_POSTER_UPDATED, this);
	}
}
