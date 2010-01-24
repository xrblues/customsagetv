package org.jdna.bmt.web.client.ui.scan;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditorMediaFileWidget extends Composite implements HasClickHandlers, MouseOutHandler, MouseOverHandler, ClickHandler {
    private GWTMediaFile mediaItem;
    
    private HorizontalPanel panel = new HorizontalPanel();
    private Image thumb = null;
    private int imageHeight=40;
    
    // Not typically a good idea, but given this can only be run in
    // a single UI, then it's ok. i guess.
    // holds a reference to the currently selected widget in a list of widgets
    private static MediaEditorMediaFileWidget currentSelectedItem = null;
    
    public MediaEditorMediaFileWidget(GWTMediaFile item) {
        this.mediaItem=item;
        
        panel.setWidth("100%");
        panel.setHeight(imageHeight+"px");
        panel.setSpacing(5);
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        
        thumb = new Image(item.getThumbnailUrl());
        thumb.setHeight(imageHeight+"px");
        thumb.addErrorHandler(new ErrorHandler() {
            public void onError(ErrorEvent event) {
                Log.debug("No Thumb: " + thumb.getUrl());
                panel.remove(thumb);
            }
        });
        
        thumb.addLoadHandler(new LoadHandler() {
            public void onLoad(LoadEvent event) {
                Image img = (Image) event.getSource();
                if (img.getWidth()>img.getHeight()) {
                    img.setWidth(imageHeight+"px");
                }
            }
        });
        
        panel.add(thumb);
        panel.setCellHorizontalAlignment(thumb, HasAlignment.ALIGN_CENTER);
        panel.setCellWidth(thumb, (imageHeight*.75)+"px");
        
        VerticalPanel titlePanel = new VerticalPanel();
        Label title = new Label(item.getTitle());
        title.addStyleName("MediaItem-PrimaryTitle");
        titlePanel.add(title);
        titlePanel.setCellHorizontalAlignment(title, HasAlignment.ALIGN_LEFT);
        titlePanel.setCellWidth(title, "100%");
        
        if (item.getMinorTitle()!=null) {
            Label title2 = new Label(item.getMinorTitle());
            title2.addStyleName("MediaItem-MinorTitle");
            titlePanel.add(title2);
            titlePanel.setCellHorizontalAlignment(title2, HasAlignment.ALIGN_LEFT);
            titlePanel.setCellWidth(title2, "100%");
        }
        
        panel.add(titlePanel);
        panel.setCellVerticalAlignment(titlePanel, HasAlignment.ALIGN_MIDDLE);
        panel.setCellHorizontalAlignment(titlePanel, HasAlignment.ALIGN_LEFT);
        panel.setCellWidth(title, "100%");
        
        Image arrow = new Image("listArrow.png");
        panel.add(arrow);
        panel.setCellWidth(arrow, "5px");
        panel.setCellHorizontalAlignment(arrow, HasHorizontalAlignment.ALIGN_RIGHT);
        
        initWidget(panel);
        addStyleName("MediaItem");
        
        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
        addDomHandler(this, ClickEvent.getType());
    }
    
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public void onMouseOut(MouseOutEvent event) {
        panel.removeStyleName("MediaItem_hover");
    }

    public void onMouseOver(MouseOverEvent event) {
        panel.addStyleName("MediaItem_hover");
    }

    public GWTMediaFile getMediaFile() {
        return mediaItem;
    }

    public void onClick(ClickEvent event) {
        if (currentSelectedItem!=null) {
            try {
                currentSelectedItem.removeStyleName("MediaItem_selected");
            } catch (Exception e) {
            }
        }
        this.currentSelectedItem = this;
        this.addStyleName("MediaItem_selected");
    }

    public void onUpdate(GWTMediaFile result) {
        this.mediaItem = result;
        addStyleName("MediaItem_updated");
    }
}
