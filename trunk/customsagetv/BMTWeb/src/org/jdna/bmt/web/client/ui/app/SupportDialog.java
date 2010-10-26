package org.jdna.bmt.web.client.ui.app;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.debug.DebugService;
import org.jdna.bmt.web.client.ui.debug.DebugServiceAsync;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.Dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class SupportDialog extends DataDialog<SupportOptions> implements DialogHandler<SupportOptions> {
    private DebugServiceAsync debug = GWT.create(DebugService.class);
    private Simple2ColFormLayoutPanel panel = new Simple2ColFormLayoutPanel();
    public SupportDialog() {
        super("Support Request", new SupportOptions(), null);
        setHandler(this);
        initPanels();
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.DataDialog#getBodyWidget()
     */
    @Override
    protected Widget getBodyWidget() {
        panel.setWidth("100%");
        panel.clear();
        panel.add(Application.labels().comment(), InputBuilder.textarea().bind(getData().getComment()).widget());
        panel.add(Application.labels().includeLogs(), InputBuilder.checkbox().bind(getData().getIncludeLogs()).widget());
        panel.add(Application.labels().includeProperties(), InputBuilder.checkbox().bind(getData().getIncludeProperties()).widget());
        panel.add(Application.labels().includeImports(), InputBuilder.checkbox().bind(getData().getIncludeSageImports()).widget());
        return panel;
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.DataDialog#getHeaderWidget()
     */
    @Override
    protected Widget getHeaderWidget() {
        return new Label(Application.labels().supportHeaderText());
    }

    public void onCancel() {
    }

    public void onSave(SupportOptions data) {
        final PopupPanel wait = Dialogs.showWaitingPopup(Application.messages().creatingSupportFile());
        debug.createSupportRequest(data, new AsyncCallback<String>() {
            public void onSuccess(String result) {
                wait.hide();
                if (result==null) {
                    onFailure(new Exception("Problem creating file, please check permissions, etc"));
                } else {
                    Dialogs.showAsDialog(Application.labels().success(), 
                            new HTML(Application.messages().supportFileCreated(result)));
                }
            }
            
            public void onFailure(Throwable caught) {
                wait.hide();
                Dialogs.showMessageDialog("Error", caught.getMessage());
            }
        });
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.DataDialog#updateButtonPanel(java.lang.Object)
     */
    @Override
    protected void updateButtonPanel(HorizontalPanel buttonPan) {
        getOkButton().setText(Application.labels().createSupportRequest());
    }
}
