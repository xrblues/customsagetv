package org.jdna.bmt.web.client.ui.app;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.debug.DebugService;
import org.jdna.bmt.web.client.ui.debug.DebugServiceAsync;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class CleanPropertiesDialog extends DataDialog<Void> implements DialogHandler<Void> {
    private DebugServiceAsync debug = GWT.create(DebugService.class);
    public CleanPropertiesDialog() {
        super("Clean .properties Files", null, null);
        setHandler(this);
        initPanels();
    }

    public void onCancel() {
    }

    public void onSave(Void data) {
        final PopupPanel wait = Dialogs.showWaitingPopup(Application.messages().cleaningProperties());
        debug.removeMetadataProperties(new AsyncCallback<Integer>() {
            public void onSuccess(Integer result) {
                wait.hide();
                Dialogs.showAsDialog("Success", new HTML(Application.messages().removedProperties(result)));
            }
            
            public void onFailure(Throwable caught) {
                wait.hide();
                Dialogs.showMessageDialog("Error", caught.getMessage());
            }
        });
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.DataDialog#getBodyWidget()
     */
    @Override
    protected Widget getBodyWidget() {
        WaitingPanel panel = new WaitingPanel();
        panel.setVisible(false);
        return panel;
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.DataDialog#getHeaderWidget()
     */
    @Override
    protected Widget getHeaderWidget() {
        return new HTML(Application.messages().cleanProperties());
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.DataDialog#updateButtonPanel(java.lang.Object)
     */
    @Override
    protected void updateButtonPanel(HorizontalPanel buttonPan) {
        getOkButton().setHTML(Application.labels().deleteProperties());
    }
}
