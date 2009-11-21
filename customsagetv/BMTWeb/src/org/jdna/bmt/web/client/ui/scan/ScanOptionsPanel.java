package org.jdna.bmt.web.client.ui.scan;

import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.HelpLabel;
import org.jdna.bmt.web.client.ui.util.TitlePanel;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScanOptionsPanel extends DataDialog<ScanOptions> {
    public ScanOptionsPanel(DialogHandler<ScanOptions> handler) {
        this(new ScanOptions(), handler);
    }

    public ScanOptionsPanel(ScanOptions options, DialogHandler<ScanOptions> handler) {
        super("Scan Options", options, handler);
        initPanels();
    }
    
    @Override
    protected Widget getBodyWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");

        ScanOptions options = getData();
        
        TitlePanel dp = new TitlePanel(new HelpLabel("Files to scan", "Select the types of files that you want to include for this scan.  By default, you can leave these unchecked and the scanner will scan all files."));
        Simple2ColFormLayoutPanel propPanel = new Simple2ColFormLayoutPanel();
        dp.setWidth("100%");
        propPanel.setWidth("100%");
        propPanel.add("Scan DVDs", InputBuilder.checkbox().bind(options.getScanDVD()).widget());
        propPanel.add("Scan Videos", InputBuilder.checkbox().bind(options.getScanVideo()).widget());
        propPanel.add("Scan Recordings", InputBuilder.checkbox().bind(options.getScanTV()).widget());
        dp.setContent(propPanel);
        panel.add(dp);
       
        dp = new TitlePanel(new HelpLabel("Only include files that are ...", "Use these options to filter your scan.  ie, if you select Missing Poster, then the scanner will ONLY update files that are missing a poster.  By default you can leave these unchecked and the scanner will include all files."));
        propPanel = new Simple2ColFormLayoutPanel();
        dp.setWidth("100%");
        propPanel.setWidth("100%");
        propPanel.add("Missing Metadata", InputBuilder.checkbox().bind(options.getScanMissingMetadata()).widget());
        propPanel.add("Missing Posters", InputBuilder.checkbox().bind(options.getScanMissingPoster()).widget());
        propPanel.add("Missing Backgrounds", InputBuilder.checkbox().bind(options.getScanMissingBackground()).widget());
        propPanel.add("Missing Banners", InputBuilder.checkbox().bind(options.getScanMissingBanner()).widget());
        dp.setContent(propPanel);
        panel.add(dp);
        
        dp = new TitlePanel(new HelpLabel("Metadata/Fanart Options","Use these options to control how you want to save/update metadata and fanart.  If you only want to update fanart, then check Update Fanart and uncheck the Update Metadata.  You can fine tune whether or not you want to update both metadata and fanart, or just metadata or just fanart, etc."));
        propPanel = new Simple2ColFormLayoutPanel();
        dp.setWidth("100%");
        propPanel.setWidth("100%");
        propPanel.add("Update Metadata", InputBuilder.checkbox().bind(options.getUpdateMetadata()).widget());
        propPanel.add("Overwrite Existing Metadata", InputBuilder.checkbox().bind(options.getOverwriteMetadata()).widget());
        propPanel.add("Update Fanart", InputBuilder.checkbox().bind(options.getUpdateFanart()).widget());
        propPanel.add("Overwrite Existing Fanart", InputBuilder.checkbox().bind(options.getOverwriteFanart()).widget());
        propPanel.add("Import TV as Sage Recordings", InputBuilder.checkbox().bind(options.getImportTV()).widget());
        dp.setContent(propPanel);
        panel.add(dp);
        
        return panel;
    }



    @Override
    protected Widget getHeaderWidget() {
        return new Label("NOTE: When you scan, it will show the total # of items scanned, but only items that could not be automatically updated will be returned.");
    }



    @Override
    protected void updateButtonPanel(Object buttonPan) {
        okButton.setText("Scan");
    }
}
