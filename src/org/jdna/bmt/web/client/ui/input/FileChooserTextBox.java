package org.jdna.bmt.web.client.ui.input;

import org.jdna.bmt.web.client.ui.filechooser.FileChooser;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FileChooserTextBox extends EditorTextBox {
    private boolean dirsOnly = false;
    
    public class ChooserPanel extends TextEditorPanel {
        public ChooserPanel() {
            super(new FileChooser(FileChooserTextBox.this.getValue(), FileChooserTextBox.this.dirsOnly));
            initWidget((Widget) getHasValueWidget());
        }
    }
    
    public FileChooserTextBox(TextBox tb, String caption, boolean dirsOnly) {
        super(tb, caption);
        this.dirsOnly = dirsOnly;
    }
    
    @Override
    protected TextEditorPanel createEditorPanel() {
        return new ChooserPanel();
    }
}
