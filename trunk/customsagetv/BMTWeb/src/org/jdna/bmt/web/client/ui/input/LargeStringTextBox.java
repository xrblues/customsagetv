package org.jdna.bmt.web.client.ui.input;


import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LargeStringTextBox extends EditorTextBox {
    private static class TextAreaPanel extends TextEditorPanel {
        public TextAreaPanel() {
            super(new TextArea());
            initWidget((Widget) getHasValueWidget());
            setWidth("100%");
            
            // set our preferred width in px for the dialog
            setPreferredWidth("450px");
        }
    }

    public LargeStringTextBox(TextBox tb, String caption) {
        super(tb, caption);
    }
    
    @Override
    protected TextEditorPanel createEditorPanel() {
        return new TextAreaPanel();
    }
}
