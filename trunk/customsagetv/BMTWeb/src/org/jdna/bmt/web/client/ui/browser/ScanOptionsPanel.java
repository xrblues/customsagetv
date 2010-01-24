package org.jdna.bmt.web.client.ui.browser;

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
        
        TitlePanel dp = null;
        Simple2ColFormLayoutPanel propPanel = null;
        
        
        dp = new TitlePanel(new HelpLabel("Metadata/Fanart Options","Use these options to control how you want to save/update metadata and fanart.  If you only want to update fanart, then check Update Fanart and uncheck the Update Metadata.  You can fine tune whether or not you want to update both metadata and fanart, or just metadata or just fanart, etc."));
        propPanel = new Simple2ColFormLayoutPanel();
        dp.setWidth("100%");
        propPanel.setWidth("100%");
        propPanel.add("Include Sub Folders", InputBuilder.checkbox().bind(options.getIncludeSubDirs()).widget());
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
    protected void updateButtonPanel(Object buttonPan) {
        okButton.setText("Scan");
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.DataDialog#getHeaderWidget()
     */
    @Override
    protected Widget getHeaderWidget() {
        return new Label(getData().getScanPath().get().getPath());
    }
}
