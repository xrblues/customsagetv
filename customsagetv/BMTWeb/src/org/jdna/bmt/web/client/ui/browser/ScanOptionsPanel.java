package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.Dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ScanOptionsPanel extends Composite {
    private ScanOptions options = new ScanOptions();
    private final BrowserServiceAsync browserService = GWT.create(BrowserService.class);

    public ScanOptionsPanel(final AsyncCallback<MediaResult[]> scanHandler, final ClickHandler closeHandler) {
        VerticalPanel panel = new VerticalPanel();
        Simple2ColFormLayoutPanel propPanel1 = new Simple2ColFormLayoutPanel();
        Simple2ColFormLayoutPanel propPanel2 = new Simple2ColFormLayoutPanel();
        HorizontalPanel propPanels = new HorizontalPanel();
        propPanels.setSpacing(10);
        propPanels.add(propPanel1);
        propPanels.add(propPanel2);
        panel.setSpacing(5);
        panel.add(propPanels);

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        buttons.setSpacing(5);
        buttons.add(new Button("Scan", new ClickHandler() {
            public void onClick(ClickEvent event) {
                closeHandler.onClick(event);
                final PopupPanel wait = Dialogs.showWaitingPopup("Scanning...");
                browserService.scan(options, new AsyncCallback<MediaResult[]>() {
                    public void onFailure(Throwable caught) {
                        wait.hide();
                        scanHandler.onFailure(caught);
                    }

                    public void onSuccess(MediaResult[] result) {
                        wait.hide();
                        scanHandler.onSuccess(result);
                    }
                });
            }
        }));

        buttons.add(new Button("Cancel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                closeHandler.onClick(event);
            }
        }));
        
        panel.add(buttons);
        panel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);
        
        propPanel1.add("Scan Entire Collection", InputBuilder.checkbox().bind(options.getScanAll()).widget());
        propPanel1.add("-- Scan DVDs", InputBuilder.checkbox().bind(options.getScanDVD()).widget());
        propPanel1.add("-- Scan Vidoes", InputBuilder.checkbox().bind(options.getScanVideo()).widget());
        propPanel1.add("-- Scan Recordings", InputBuilder.checkbox().bind(options.getScanTV()).widget());
        propPanel1.add("Include Missing Metadata", InputBuilder.checkbox().bind(options.getScanMissingMetadata()).widget());
        propPanel1.add("Include Missing Posters", InputBuilder.checkbox().bind(options.getScanMissingPoster()).widget());
        propPanel1.add("Include Missing Backgrounds", InputBuilder.checkbox().bind(options.getScanMissingBackground()).widget());
        propPanel1.add("Include Missing Banners", InputBuilder.checkbox().bind(options.getScanMissingBanner()).widget());
        propPanel2.add("Search for Metadata", InputBuilder.checkbox().bind(options.getUpdateMetadata()).widget());
        propPanel2.add("Search for Fanart", InputBuilder.checkbox().bind(options.getUpdateFanart()).widget());
        propPanel2.add("Overwrite Existing Metadata", InputBuilder.checkbox().bind(options.getOverwriteMetadata()).widget());
        propPanel2.add("Overwrite Existing Fanart", InputBuilder.checkbox().bind(options.getOverwriteFanart()).widget());
        propPanel2.add("Import TV as Sage Recordings", InputBuilder.checkbox().bind(options.getImportTV()).widget());

        // spacer
        propPanel2.add(new HTML("<hr/>"), new HTML("<hr/>"));
        
        propPanel2.add("Don't Update Anything, Just Scan", InputBuilder.checkbox().bind(options.getDontUpdate()).widget());
        propPanel2.add("Run in Background", InputBuilder.checkbox().bind(options.getRunInBackground()).widget());
        
        HorizontalPanel fp = new HorizontalPanel();
        fp.setSpacing(5);
        fp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        fp.setWidth("100%");
        fp.add(propPanel2.createLabel("Filter By"));
        fp.add(InputBuilder.textbox().bind(options.getFilter()).widget());
        propPanel2.add(fp, new Label());
        
        initWidget(panel);
    }
    
    public static void showDialog(final AsyncCallback<MediaResult[]> callback) {
        final DialogBox dialog = new DialogBox(false, true);
        dialog.setText("New Metadata Scan");
        dialog.setWidget(new ScanOptionsPanel(new AsyncCallback<MediaResult[]>() {
            public void onFailure(Throwable caught) {
                dialog.hide();
                callback.onFailure(caught);
            }
            public void onSuccess(MediaResult[] result) {
                dialog.hide();
                callback.onSuccess(result);
            }
        }, new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialog.hide();
            }
        }));
        dialog.center();
        dialog.show();
    }
}
