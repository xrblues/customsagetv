package org.jdna.bmt.web.client.ui.browser;

import java.util.Date;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.layout.FlowGrid;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.util.Log;
import org.jdna.bmt.web.client.util.NumberUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VideoThumbnailsPanel extends Composite {
    private GWTMediaFile mediaFile = null;
	private BrowsePanel controller;
	private DateTimeFormat format = null;
    
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlowGrid flow = new FlowGrid(3);
	
    public VideoThumbnailsPanel(GWTMediaFile mediaFile, BrowsePanel controller) {
        this.controller = controller;
        this.mediaFile = mediaFile;

        format = DateTimeFormat.getFormat("HH:mm:ss");
        
        mainPanel.setWidth("100%");
        mainPanel.setSpacing(5);
        initWidget(mainPanel);

        HorizontalButtonBar hp = new HorizontalButtonBar();

        Button back = new Button("Back");
        back.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                VideoThumbnailsPanel.this.controller.back();
            }
        });
        hp.add(back);
        hp.addSpacer();
        
        Label l = new Label("Frames"); 
        hp.add(l);
        final TextBox frames = new TextBox();
        frames.setWidth("1cm");
        frames.setValue("12");
        hp.add(frames);
        Button update = new Button("Update");
        update.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateFrames(NumberUtil.toInt(frames.getValue(), 12));
			}
		});
        hp.add(update);
        mainPanel.add(hp);
        mainPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
        mainPanel.add(flow);

        updateFrames(12);
    }
    
    public void updateFrames(int frames) {
    	flow.clear();
        int w=300;
        int h=300;
        int thumbs = frames;
        long dur = mediaFile.getDuration();
        if (dur==0) dur = 30*60*1000;
        long dursec = dur/1000;
        int secs=(int) (dursec/thumbs);
        long size = mediaFile.getSize();
        long size_onesec = size/dursec;
        for (int i=0;i<thumbs;i++) {
        	flow.add(createImageWidget(i*secs, w, h, /*disabled*/0*i*secs*size_onesec));
        }
    }

	private Widget createImageWidget(int sec, int w, int h, long offset) {
		final Image img = new Image();
		if (offset>0) {
			img.setUrl("videothumbnail?file=" + mediaFile.getSageMediaFileId() + "&sb="+offset+"&w="+w+"&h="+h);
		} else {
			img.setUrl("videothumbnail?file=" + mediaFile.getSageMediaFileId() + "&ss="+sec+"&w="+w+"&h="+h);
		}
		img.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				Log.error("Failed to load video thumbnail for " + img.getUrl());
			}
		});
		Date d = new Date(0, 0, 0, (sec/60/60)%24, (sec/60)%60, sec%60);
		if (offset>0) {
			img.setTitle("byte offset: " + String.valueOf(offset));
		} else {
			img.setTitle(format.format(d));
		}
		return img;
	}
}
