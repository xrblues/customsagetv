package org.jdna.bmt.web.client.ui.prefs;

import org.jdna.bmt.web.client.ui.databrowser.DataBrowser;
import org.jdna.bmt.web.client.ui.input.FileChooserTextBox;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditorFactory {
    public static Widget xcreateEditor(String id, TextBox tb, String caption) {
        if (id==null || id.trim().length()==0) {
            return null;
        }
        
        if ("dirChooser".equals(id)) {
        } else if ("fileChooser".equals(id)) {
                return new FileChooserTextBox(tb, caption, false);
        } else {
            Log.debug("Missing Editor: " + id + " for field: " + caption);
        }
        return null;
    }

    public static Widget createEditor(String editor) {
        if ("log4jEditor".equals(editor)) {
            return new Log4jPropertiesPanel();
        } else if ("refreshConfigurations".equals(editor)) {
            return new RefreshPanel();
        } else if ("videoSourcesEditor".equals(editor)) {
            return new VideoSourcesEditorPanel();
        } else if ("viewSageProperties".equals(editor)) {
            return new SagePropertiesViewerPanel();
        } else if ("channels".equals(editor)) {
            return new ChannelsEditorPanel();
        } else if ("plugins".equals(editor)) {
            return new PluginsEditorPanel();
        } else if ("menus".equals(editor)) {
            return new MenuEditorPanel();
        } else if ("userstores".equals(editor)) {
            return new DataBrowser();
        } else {
        	return new Label("Unknown Editor: " + editor);
        }
    }
}
