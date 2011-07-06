package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.ui.util.AbstractClickableItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class FanartImage extends AbstractClickableItem {

    private static FanartImageUiBinder uiBinder = GWT.create(FanartImageUiBinder.class);

    interface FanartImageUiBinder extends UiBinder<Widget, FanartImage> {
    }

    private GWTMediaArt art = null;
    private FanartPanel panel;
    @UiField Image image;
    public FanartImage(GWTMediaArt ma, FanartPanel panel) {
        initWidget(uiBinder.createAndBindUi(this));
        image.setUrl(ma.getDisplayUrl());
        this.panel=panel;
        addStyleName("FanartImageNormal");
        setHoverStyleName("FanartImageHover");
        this.art = ma;
    }
    
    @Override
    public void onClick(ClickEvent event) {
        panel.setCurImage(this);
    }

    public GWTMediaArt getMediaArt() {
        return art;
    }
}
