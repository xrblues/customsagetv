package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ImagePopupLabel extends Composite {
    private Label label = new Label();
    private String url = null;
    public ImagePopupLabel(String label, String url) {
        this.url=url;
        this.label.setText(label);
        this.label.addStyleName("ImagePopupLabel-label");
        this.label.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                popupImage();
            }
        });
        initWidget(this.label);
    }
    
    protected void popupImage() {
        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        Image image = new Image(url);
        image.setWidth("300px");
        hp.add(image);
        Dialogs.showAsDialog(label.getText(), hp);
    }
}
