package org.jdna.bmt.web.client.ui.scan;

import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SaveOptionsPanel extends DataDialog<SaveOptions> {
    public SaveOptionsPanel(DialogHandler<SaveOptions> handler) {
        this(new SaveOptions(), handler);
    }
    
    public SaveOptionsPanel(SaveOptions options, DialogHandler<SaveOptions> handler) {
        super("Save Options", options, handler);
        initPanels();
    }
    
    @Override
    protected Widget getBodyWidget() {
        Simple2ColFormLayoutPanel propPanel = new Simple2ColFormLayoutPanel();
        propPanel.setWidth("100%");
        propPanel.add("Save Metadata", InputBuilder.checkbox().bind(getData().getUpdateMetadata()).widget());
        propPanel.add("Save Fanart", InputBuilder.checkbox().bind(getData().getUpdateFanart()).widget());
        propPanel.add("Overwrite Existing Fanart", InputBuilder.checkbox().bind(getData().getOverwriteFanart()).widget());
        return propPanel;
    }
    @Override
    protected Widget getHeaderWidget() {
        return new Label("NOTE: If you select to save Fanart Only, then it will still update the Custom Metadata Fields (which are required for fanart to work correctly), but it will not update the core metadata fields.");
    }
    
    
    
    @Override
    protected void updateButtonPanel(HorizontalPanel buttonPan) {
        super.updateButtonPanel(buttonPan);
        
        okButton.setText("Save");
    }
}
