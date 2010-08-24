package org.jdna.bmt.web.client.ui.input;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RegexEditorTextBox extends EditorTextBox {
    public class RegexPanel extends TextEditorPanel {
        public RegexPanel() {
            super(new RegexEditorPanel(RegexEditorTextBox.this.getValue()));
            initWidget((Widget) getHasValueWidget());
            setWidth("500px");
        }
    }
    
    public RegexEditorTextBox(TextBox tb, String caption) {
        super(tb, caption);
    }
    
    @Override
    protected TextEditorPanel createEditorPanel() {
        return new RegexPanel();
    }
}
