package org.jdna.bmt.web.client.ui.prefs;

import org.jdna.bmt.web.client.ui.input.ArrayEditorTextBox;
import org.jdna.bmt.web.client.ui.input.FileChooserTextBox;
import org.jdna.bmt.web.client.ui.input.InputFactory;
import org.jdna.bmt.web.client.ui.input.LargeStringTextBox;
import org.jdna.bmt.web.client.ui.input.RegexEditorTextBox;
import org.jdna.bmt.web.client.ui.util.HelpLabel;

import sagex.phoenix.configuration.Config;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PreferenceItemEditor extends Composite {
    private HelpLabel  label  = null;
    private Widget   editor = null;

    public PreferenceItemEditor(final PrefItem item) {
        this.label = new HelpLabel(item.getLabel(), createHelpText(item));

        if (item.getHints()!=null) {
        	if (item.getHints().getBooleanValue(Config.Hint.REGEX, false)) {
        		this.editor = new RegexEditorTextBox(InputFactory.bind(new TextBox(), item), item.getLabel());
        	}
        }
        
        if (item.getListSeparator()!=null) {
        	this.editor = new ArrayEditorTextBox(InputFactory.bind(new TextBox(), item), item.getLabel(), item.getListSeparator());
        }

        if (this.editor==null) {
	        if (Config.Type.BOOL.equals(item.getType())) {
	        	this.editor = InputFactory.bind(new CheckBox(), item);
	        } else if (Config.Type.NUMBER.equals(item.getType())) {
	        	this.editor = InputFactory.bind(InputFactory.createFloatTextbox(), item);
	        } else if (Config.Type.FILE.equals(item.getType())) {
	        	this.editor = new FileChooserTextBox(InputFactory.bind(new TextBox(), item), item.getLabel(), false);
	        } else if (Config.Type.DIRECTORY.equals(item.getType())) {
	        	this.editor = new FileChooserTextBox(InputFactory.bind(new TextBox(), item), item.getLabel(), true);
	        } else {
	            this.editor = new LargeStringTextBox(InputFactory.bind(new TextBox(), item), item.getLabel());
	        }
        }
        
    }

    private String createHelpText(PrefItem item2) {
        //return "<div class=\"HelpLabel-Property\">Sage Property: " +item2.getKey() + " ("+item2.getType()+")</div><div class=\"HelpLabel-HelpText\">" + item2.getDescription() + "</div>";
    	PrefItemHelpCaption help = new PrefItemHelpCaption(item2);
    	return help.getElement().getInnerHTML();
    }

    public Widget getLabel() {
        return label;
    }

    public Widget getEditor() {
        return editor;
    }
}
