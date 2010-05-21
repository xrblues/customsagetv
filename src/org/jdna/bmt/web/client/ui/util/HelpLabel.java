package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class HelpLabel extends Composite {
    private Image icon =new Image("images/16x16/help-browser.png");
    private Label label = new Label();
    private String helpText = null;
    
    public HelpLabel(String label, String helpTextHtml) {
        this.label.setWordWrap(false);
        this.label.setText(label);
        this.helpText =helpTextHtml;
        
        icon.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showDialog();
            }
        });
        icon.setStyleName("HelpLabel-icon");
        
        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        hp.setSpacing(2);
        hp.add(icon);
        hp.add(this.label);
        hp.setCellVerticalAlignment(icon, HasVerticalAlignment.ALIGN_MIDDLE);
        hp.setCellVerticalAlignment(this.label, HasVerticalAlignment.ALIGN_MIDDLE);
        hp.setCellWidth(this.label, "100%");
        initWidget(hp);
    }

    protected void showDialog() {
        Dialogs.showMessageDialog(label.getText(), helpText);
    }
}
