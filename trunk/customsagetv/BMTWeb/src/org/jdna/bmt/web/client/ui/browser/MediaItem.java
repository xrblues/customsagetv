package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.ui.debug.DebugDialog;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaItem extends Composite implements HasClickHandlers, MouseOutHandler, MouseOverHandler, ClickHandler {
    private GWTMediaResource res;
    private DockPanel        panel   = new DockPanel();
    private VerticalPanel    titles  = new VerticalPanel();
    private HorizontalPanel  actions = new HorizontalPanel();
    private BrowserView      view    = null;

    public MediaItem(final GWTMediaResource res, BrowserView view) {
        this.view = view;
        this.res = res;
        titles.setWidth("100%");
        panel.add(titles, DockPanel.NORTH);
        panel.setCellVerticalAlignment(titles, HasVerticalAlignment.ALIGN_TOP);
        panel.setStyleName("MediaItem");
        setTitles(res);

        final Image img = new Image();
        img.addStyleName("MediaItem-Image");
        img.addErrorHandler(new ErrorHandler() {
            public void onError(ErrorEvent event) {
                if (res instanceof GWTMediaFolder) {
                    img.setUrl("images/128x128/folder_video.png");
                } else {
                    img.setUrl("images/128x128/video.png");
                }
            }
        });
        
        if (res instanceof GWTMediaFolder) {
            img.setUrl("images/128x128/folder_video.png");
        } else {
            img.setUrl(res.getThumbnailUrl());
        }

        // set the actions
        if (!(res instanceof GWTMediaFolder)) {
            Image img2 = new Image("images/16x16/applications-system.png");
            img2.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    DebugDialog.show((GWTMediaFile) res);
                    event.stopPropagation();
                }
            });
            actions.add(img2);
        }
        
        panel.add(img, DockPanel.CENTER);
        panel.setCellHorizontalAlignment(img, HasHorizontalAlignment.ALIGN_CENTER);
        panel.setCellVerticalAlignment(img, HasVerticalAlignment.ALIGN_BOTTOM);

        panel.add(actions, DockPanel.SOUTH);
        panel.setCellHorizontalAlignment(actions, HasHorizontalAlignment.ALIGN_RIGHT);
        panel.setCellVerticalAlignment(actions, HasVerticalAlignment.ALIGN_BOTTOM);
        panel.setCellHeight(actions, "20px");

        if (res instanceof GWTMediaFolder) {
            panel.addStyleName("MediaFolder");
        }

        if (res.getMessage() != null) {
            panel.setTitle(res.getMessage());
        }

        initWidget(panel);

        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
        addDomHandler(this, ClickEvent.getType());
    }

    private void setTitles(GWTMediaResource res2) {
        titles.clear();
        Label title1 = new Label(res2.getTitle());
        titles.add(title1);
        titles.setCellHorizontalAlignment(title1, HasHorizontalAlignment.ALIGN_CENTER);
        title1.setStyleName("MediaItem-Title1");

        //if (res2 instanceof GWTMediaFile) {
            String epTitle = res2.getMinorTitle();
            if (!StringUtils.isEmpty(epTitle) && !epTitle.equals(res.getTitle())) {
                Label title2 = new Label(epTitle);
                
                titles.add(title2);
                titles.setCellHorizontalAlignment(title2, HasHorizontalAlignment.ALIGN_CENTER);
                title2.setStyleName("MediaItem-Title2");
            }
        //}
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public void onMouseOut(MouseOutEvent event) {
        // TODO Auto-generated method stub
    }

    public void onMouseOver(MouseOverEvent event) {
        // TODO Auto-generated method stub
    }

    public void onClick(ClickEvent event) {
        if (res instanceof GWTMediaFolder) {
            BrowsingServicesManager.getInstance().browseFolder((GWTMediaFolder) res, 0, ((GWTMediaFolder) res).getPageSize());
        } else {
            view.setDisplay(new MediaEditorMetadataPanel((GWTMediaFile) res, view));
        }
    }
}
