package org.jdna.bmt.web.client.ui.scan;

import org.jdna.bmt.web.client.media.GWTMediaFile;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class MediaItemWidget extends Composite implements HasClickHandlers {
    private DockPanel panel = new DockPanel();
    private Image thumb = null;
    private GWTMediaFile mediaFile;
    public MediaItemWidget(GWTMediaFile item) {
        this.mediaFile=item;
        panel.setWidth("150px");
        panel.setSpacing(7);
        thumb = new Image(item.getPosterUrl());
        thumb.setHeight("120px");
        thumb.addErrorHandler(new ErrorHandler() {
            public void onError(ErrorEvent event) {
                panel.remove(thumb);
                MissingImagePanel p = new MissingImagePanel();
                p.setPixelSize(80, 120);
                panel.add(p, DockPanel.CENTER);
                panel.setCellHorizontalAlignment(p, HasHorizontalAlignment.ALIGN_CENTER);
            }
        });
        thumb.addLoadHandler(new LoadHandler() {
            public void onLoad(LoadEvent event) {
                Image img = (Image) event.getSource();
                if (img.getWidth()>img.getHeight()) {
                    img.setWidth("130px");
                }
            }
        });
        panel.add(thumb, DockPanel.CENTER);
        panel.setCellHorizontalAlignment(thumb, HasAlignment.ALIGN_CENTER);
        
        Label title = new Label(item.getTitle());
        panel.add(title, DockPanel.SOUTH);
        panel.setCellHorizontalAlignment(title, HasAlignment.ALIGN_CENTER);
        
        initWidget(panel);
        addStyleName("MediaItem");
        if (item.getMessage()!=null) {
            panel.setTitle(item.getMessage());
            panel.addStyleName("MediItem-Error");
        }
    }
    
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }
}
