package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.util.DialogHandler;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SaveOptionsPanel extends ScanOptionsPanel {

    public SaveOptionsPanel(PersistenceOptionsUI options, DialogHandler<PersistenceOptionsUI> handler) {
        super("Save Options", options, handler);
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.browser.ScanOptionsPanel#getHeaderWidget()
     */
    @Override
    protected Widget getHeaderWidget() {
        return new Label("Save Options");
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.browser.ScanOptionsPanel#updateButtonPanel(java.lang.Object)
     */
    @Override
    protected void updateButtonPanel(HorizontalPanel buttonPan) {
        okButton.setText("Save");
    }
}