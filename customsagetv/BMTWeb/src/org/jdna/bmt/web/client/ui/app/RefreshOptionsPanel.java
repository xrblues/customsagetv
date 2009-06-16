package org.jdna.bmt.web.client.ui.app;

import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RefreshOptionsPanel extends Composite {
    private static final SageServiceAsync sage = GWT.create(SageService.class);
    
    private DialogBox dialog = null;
    private RadioButton newOnly = null;
    private RadioButton fullReindex = null;
    
    public RefreshOptionsPanel(DialogBox dlg) {
        this.dialog = dlg;
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.setSpacing(5);
        
        newOnly = new RadioButton("refresh", "Scan for new media");
        newOnly.setValue(true);
        fullReindex = new RadioButton("refresh", "Reindex entire media library");
        
        panel.add(newOnly);
        panel.add(fullReindex);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        hp.setSpacing(5);
        Button refresh = new Button("Refresh", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialog.hide();
                sage.refreshLibrary(fullReindex.getValue(), new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        Log.error("Failed to start scan", caught);
                    }

                    public void onSuccess(String result) {
                        Dialogs.showMessage(result, 4000);
                    }
                });
            }
        });
        Button cancel = new Button("Cancel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialog.hide();
            }
        });
        hp.add(refresh);
        hp.add(cancel);

        panel.add(hp);
        panel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
        
        initWidget(panel);
    }
    
    public static void showDialog() {
        final DialogBox dialog = new DialogBox(false, true);
        dialog.setText("Refresh SageTV Library");
        dialog.setWidget(new RefreshOptionsPanel(dialog));
        dialog.center();
        dialog.show();
    }
}
