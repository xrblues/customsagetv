package org.jdna.bmt.web.client.ui.browser;

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

public class MediaEditorWidget extends Composite implements HasClickHandlers, MouseOutHandler, MouseOverHandler {
    private MediaResult mediaResult;
    
    private HorizontalPanel panel = new HorizontalPanel();
    private Image thumb = null;
    private int imageHeight=40;
    
    public MediaEditorWidget(MediaResult item) {
        this.mediaResult=item;
        
        panel.setWidth("100%");
        panel.setHeight(imageHeight+"px");
        panel.setSpacing(5);
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        
        thumb = new Image(item.getPosterUrl());
        thumb.setHeight(imageHeight+"px");
        thumb.addErrorHandler(new ErrorHandler() {
            public void onError(ErrorEvent event) {
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
        
        Label title = new Label(item.getMediaTitle());
        panel.add(title);
        panel.setCellHorizontalAlignment(title, HasAlignment.ALIGN_LEFT);
        panel.setCellWidth(title, "100%");
        
        Image arrow = new Image("listArrow.png");
        panel.add(arrow);
        panel.setCellWidth(arrow, "5px");
        panel.setCellHorizontalAlignment(arrow, HasHorizontalAlignment.ALIGN_RIGHT);
        
        initWidget(panel);
        addStyleName("MediaItem");
        
        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
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

    public MediaResult getMediaItem() {
        return mediaResult;
    }
}
