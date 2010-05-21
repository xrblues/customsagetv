package org.jdna.bmt.web.client.ui.debug;

import org.jdna.bmt.web.client.media.GWTMediaFile;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DebugDialog extends DialogBox {
    VerticalPanel panel = new VerticalPanel();
    public DebugDialog(GWTMediaFile file) {
        super();
        panel.setWidth("500px");
        panel.add(new DebugPanel(file));
        Button b = new Button("Close");
        b.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        panel.add(b);
        panel.setCellHorizontalAlignment(b, HasHorizontalAlignment.ALIGN_CENTER);
        setWidget(panel);
        setText(file.getTitle());
    }
    
    public static void show(GWTMediaFile file) {
        DebugDialog dlg = new DebugDialog(file);
        dlg.center();
        dlg.show();
    }
}
