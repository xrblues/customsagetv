package org.jdna.bmt.web.client.ui.input;


import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LargeStringTextBox extends EditorTextBox {
    private boolean readonly;

    private static class TextAreaPanel extends TextEditorPanel {
        public TextAreaPanel() {
            super(new TextArea());
            initWidget((Widget) getHasValueWidget());
            setWidth("100%");
            
            // set our preferred width in px for the dialog
            setPreferredWidth("450px");
        }

        public void setReadOnly(boolean readonly) {
            ((TextArea)getHasValueWidget()).setReadOnly(readonly);
        }
    }

    public LargeStringTextBox(TextBox tb, String caption) {
        super(tb, caption);
    }
    
    @Override
    protected TextEditorPanel createEditorPanel() {
        TextAreaPanel text = new TextAreaPanel();
        text.setReadOnly(readonly);
        return text;
    }
    
    public void setReadOnly(boolean readonly) {
        this.readonly=readonly;
        getTextBox().setReadOnly(readonly);
    }
}
