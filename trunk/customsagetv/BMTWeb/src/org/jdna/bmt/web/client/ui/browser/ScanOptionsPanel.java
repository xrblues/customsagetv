package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.HelpLabel;
import org.jdna.bmt.web.client.ui.util.TitlePanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScanOptionsPanel extends DataDialog<PersistenceOptionsUI> implements ChangeHandler, ClickHandler {
    protected Simple2ColFormLayoutPanel propPanel = null;

    public ScanOptionsPanel(DialogHandler<PersistenceOptionsUI> handler) {
        this(new PersistenceOptionsUI(), handler);
    }

    public ScanOptionsPanel(String title, PersistenceOptionsUI options, DialogHandler<PersistenceOptionsUI> handler) {
        super(title, options, handler);
        initPanels();
    }

    public ScanOptionsPanel(PersistenceOptionsUI options, DialogHandler<PersistenceOptionsUI> handler) {
        this("Scan Options", options, handler);
    }
    
    @Override
    protected Widget getBodyWidget() {
        System.out.println("Scan Options Panel");
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");

        PersistenceOptionsUI options = getData();
        
        TitlePanel dp = null;
        dp = new TitlePanel(new HelpLabel("Metadata/Fanart Options","Metadata/Fanart Scan Options"));
        propPanel = new Simple2ColFormLayoutPanel();
        propPanel.getFlexTable().addClickHandler(this);
        dp.setWidth("100%");
        propPanel.setWidth("100%");
        if (options.getIncludeSubDirs().isVisible()) {
            //propPanel.add("Include Sub Folders", InputBuilder.checkbox().bind(options.getIncludeSubDirs()).widget());
        }
        
        if (options.getScanOnlyMissingMetadata().isVisible()) {
        	propPanel.add("Only update items that have not been updated previously", InputBuilder.checkbox().bind(options.getScanOnlyMissingMetadata()).widget());
        }
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

    public void onChange(ChangeEvent event) {
    }

    public void onClick(ClickEvent event) {
    }
}
