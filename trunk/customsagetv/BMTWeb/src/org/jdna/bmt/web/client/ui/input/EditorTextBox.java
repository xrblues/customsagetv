package org.jdna.bmt.web.client.ui.input;


import org.jdna.bmt.web.client.Application;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class EditorTextBox extends Composite implements HasValue<String> {
    private static class EditorDialog extends DialogBox {
        private EditorTextBox editor = null;
        private TextEditorPanel panel = null;
        public EditorDialog(EditorTextBox editor, TextEditorPanel panel) {
            super();
            this.editor=editor;
            this.panel=panel;
            
            panel.setValue(editor.getValue());
            setText(editor.getCaption());

            VerticalPanel vp = new VerticalPanel();
            if (panel.getPreferredWidth()!=null) {
                vp.setWidth(panel.getPreferredWidth());
            }
            if (panel.getPreferredHeight()!=null) {
                vp.setHeight(panel.getPreferredHeight());
            }
            vp.add(panel);
            
            HorizontalPanel hp = new HorizontalPanel();
            hp.setSpacing(5);
            Button b = new Button(Application.labels().ok());
            b.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    EditorDialog.this.editor.setValue(EditorDialog.this.panel.getValue(), true);
                    EditorDialog.this.hide();
                }
            });
            hp.add(b);
            Button cancel = new Button(Application.labels().cancel());
            cancel.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    EditorDialog.this.hide();
                }
            });
            hp.add(cancel);
            vp.add(hp);
            
            vp.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_CENTER);
            setModal(true);
            setWidget(vp);
        }
        
        @Override
        protected void onAttach() {
            super.onAttach();
            panel.setFocus(true);
        }
    }
    
    
    private TextBox textbox = null;
    private Button button = null;
    private HorizontalPanel panel = null;
    private DialogBox dialog = null;
    private String caption = null;
    
    public EditorTextBox(TextBox tb, String caption) {
        this.textbox=tb;
        this.caption=caption;
        this.button = new Button("...", new ClickHandler() {
            public void onClick(ClickEvent event) {
                showPopupEntryPanel();
            }
        });
        panel=new HorizontalPanel();
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setSpacing(4);
        panel.add(textbox);
        panel.add(button);
        initWidget(panel);
    }
    
    private void showPopupEntryPanel() {
        TextEditorPanel panel = createEditorPanel();
        dialog = new EditorDialog(this, panel);
        dialog.center();
    }
    
    public TextBox getTextBox() {
        return textbox;
    }
    
    public String getCaption() {
        return caption;
    }
    
    protected abstract TextEditorPanel createEditorPanel();

    public String getValue() {
        return textbox.getValue();
    }

    public void setValue(String value) {
        textbox.setValue(value);
    }

    public void setValue(String value, boolean fireEvents) {
        textbox.setValue(value, fireEvents);
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return textbox.addValueChangeHandler(handler);
    }
    
    public Button getButton() {
        return button;
    }
}
