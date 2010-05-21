package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.HelpLabel;
import org.jdna.bmt.web.client.ui.util.TitlePanel;
import org.jdna.bmt.web.client.ui.util.WidgetUtils;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
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
        
        dp = new TitlePanel(new HelpLabel("Metadata/Fanart Options","Use these options to control how you want to save/update metadata and fanart.  If you only want to update fanart, then check Update Fanart and uncheck the Update Metadata.  You can fine tune whether or not you want to update both metadata and fanart, or just metadata or just fanart, etc."));
        propPanel = new Simple2ColFormLayoutPanel();
        propPanel.getFlexTable().addClickHandler(this);
        dp.setWidth("100%");
        propPanel.setWidth("100%");
        if (options.getIncludeSubDirs().isVisible()) {
            propPanel.add("Include Sub Folders", InputBuilder.checkbox().bind(options.getIncludeSubDirs()).widget());
        }
        propPanel.add("Update Metadata", InputBuilder.checkbox("scanoptions-updatemd").bind(options.getUpdateMetadata()).widget());
        propPanel.add("Overwrite Existing Metadata", InputBuilder.checkbox("scanoptions-overwritemd").bind(options.getOverwriteMetadata()).widget());
        propPanel.add("Create .properties Files", InputBuilder.checkbox("scanoptions-createprops").bind(options.getCreatePropertyFiles()).widget());
        propPanel.add("Update Fanart", InputBuilder.checkbox("scanoptions-updatefanart").bind(options.getUpdateFanart()).widget());
        propPanel.add("Overwrite Existing Fanart", InputBuilder.checkbox("scanoptions-overwritefanart").bind(options.getOverwriteFanart()).widget());
        propPanel.add("Generate Thumbanils for Default STV", InputBuilder.checkbox().bind(options.getCreateDefaultSTVThumbnail()).widget());
        propPanel.add("Update Wiz.bin", InputBuilder.checkbox("scanoptions-updatewiz").bind(options.getUpdateWizBin()).addClickHandler(this).widget());
        if (options.getImportTV().isVisible()) {
            propPanel.add("Import TV as Sage Recordings", InputBuilder.checkbox("scanoptions-importtv").bind(options.getImportTV()).widget());
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
        refreshUI();
    }
    
    private void refreshUI() {
        CheckBox importTV = (CheckBox) WidgetUtils.getWidgetForId(propPanel.getFlexTable(), "scanoptions-importtv");
        CheckBox updateWiz = (CheckBox) WidgetUtils.getWidgetForId(propPanel.getFlexTable(), "scanoptions-updatewiz");
        importTV.setEnabled(updateWiz.getValue());

        CheckBox update = (CheckBox) WidgetUtils.getWidgetForId(propPanel.getFlexTable(), "scanoptions-updatefanart");
        CheckBox overwrite = (CheckBox) WidgetUtils.getWidgetForId(propPanel.getFlexTable(), "scanoptions-overwritefanart");
        overwrite.setEnabled(update.getValue());

        update = (CheckBox) WidgetUtils.getWidgetForId(propPanel.getFlexTable(), "scanoptions-updatemd");
        overwrite = (CheckBox) WidgetUtils.getWidgetForId(propPanel.getFlexTable(), "scanoptions-overwritemd");
        overwrite.setEnabled(update.getValue());
    }
}
