package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;

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
                browserService.scan(options, scanHandler);
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
        propPanel1.add("Scan DVDs", InputBuilder.checkbox().bind(options.getScanDVD()).widget());
        propPanel1.add("Scan Vidoes", InputBuilder.checkbox().bind(options.getScanVideo()).widget());
        propPanel1.add("Scan Recordings", InputBuilder.checkbox().bind(options.getScanTV()).widget());
        propPanel1.add("Scan Missing Metadata", InputBuilder.checkbox().bind(options.getScanMissingMetadata()).widget());
        propPanel1.add("Scan Missing Posters", InputBuilder.checkbox().bind(options.getScanMissingPoster()).widget());
        propPanel1.add("Scan Missing Backgrounds", InputBuilder.checkbox().bind(options.getScanMissingBackground()).widget());
        propPanel1.add("Scan Missing Banners", InputBuilder.checkbox().bind(options.getScanMissingBanner()).widget());
        propPanel2.add("Update Metadata", InputBuilder.checkbox().bind(options.getUpdateMetadata()).widget());
        propPanel2.add("Overwrite Metadata", InputBuilder.checkbox().bind(options.getOverwriteMetadata()).widget());
        propPanel2.add("Import TV as Sage Recordings", InputBuilder.checkbox().bind(options.getImportTV()).widget());
        propPanel2.add("Update Fanart", InputBuilder.checkbox().bind(options.getUpdateFanart()).widget());
        propPanel2.add("Overwrite Fanart", InputBuilder.checkbox().bind(options.getOverwriteFanart()).widget());

        // spacer
        propPanel2.add(new HTML("<hr/>"), new HTML("<hr/>"));
        
        propPanel2.add("Don't Update Anything, Just Scan", InputBuilder.checkbox().bind(options.getDontUpdate()).widget());
        propPanel2.add("Run in Background", InputBuilder.checkbox().bind(options.getRunInBackground()).widget());
        
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
