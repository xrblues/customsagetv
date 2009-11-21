package org.jdna.bmt.web.client.ui.scan;

import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.HelpLabel;
import org.jdna.bmt.web.client.ui.util.TitlePanel;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BrowseOptionsPanel extends DataDialog<ScanOptions>  {
    private TextBox searchBox = new TextBox();
    
    public BrowseOptionsPanel(ScanOptions options, DialogHandler<ScanOptions> handler) {
        super("Browse Options", options, handler);
        initPanels();
    }

    private void focusSearch() {
        searchBox.setFocus(true);
    }

    @Override
    protected Widget getBodyWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");

        // tell the options to just scan
        data.getDontUpdate().set(true);
        
        TitlePanel title = new TitlePanel(new HelpLabel("Filter title", "If you specify a value in the filter, then only the items that contain your filter keyword will be returned."));
        title.setWidth("100%");
        Simple2ColFormLayoutPanel fp = new Simple2ColFormLayoutPanel();
        fp.setWidth("100%");
        if (searchBox==null) {
            Log.error("searchbox is null!!!");
        }
        
        InputBuilder.textbox(searchBox).bind(data.getFilter());
        fp.add("Title Contains", searchBox);
        title.setContent(fp);
        panel.add(title);

        TitlePanel dp = new TitlePanel(new HelpLabel("Browse Files", "Select the types of files that you want to include for this scan.  By default, you can leave these unchecked and all video media files will be returned."));
        Simple2ColFormLayoutPanel propPanel = new Simple2ColFormLayoutPanel();
        dp.setWidth("100%");
        propPanel.setWidth("100%");
        propPanel.add("Browse DVDs", InputBuilder.checkbox().bind(data.getScanDVD()).widget());
        propPanel.add("Browse Videos", InputBuilder.checkbox().bind(data.getScanVideo()).widget());
        propPanel.add("Browse Recordings", InputBuilder.checkbox().bind(data.getScanTV()).widget());
        dp.setContent(propPanel);
        panel.add(dp);

        return panel;
    }

    @Override
    protected Widget getHeaderWidget() {
        return null;
    }


    @Override
    protected void updateButtonPanel(Object buttonPan) {
        okButton.setText("Browse");
    }

    @Override
    protected boolean updateValues() {
        return true;
    }

    @Override
    public void focus() {
        Log.debug("focus called");
        focusSearch();
    }
}
