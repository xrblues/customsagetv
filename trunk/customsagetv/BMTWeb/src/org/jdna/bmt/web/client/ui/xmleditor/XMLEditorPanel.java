package org.jdna.bmt.web.client.ui.xmleditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class XMLEditorPanel extends Composite {
	private static XMLEditorPanelUiBinder uiBinder = GWT
			.create(XMLEditorPanelUiBinder.class);

	interface XMLEditorPanelUiBinder extends UiBinder<Widget, XMLEditorPanel> {
	}

	@UiField
	TextAreaElement code;

	// CodeMirror Editor Instance
	private JavaScriptObject editor;
	private String origData;
	private XmlFileEntry file;
	private MenuBar contextMenu;

	public XMLEditorPanel(XmlFileEntry data, MenuBar contextMenu) {
		initWidget(uiBinder.createAndBindUi(this));
		code.setInnerText(data.contents);
		origData = data.contents;
		file = data;
		this.contextMenu = contextMenu;
		sinkEvents(Event.ONCONTEXTMENU);
	}

	public native void initEditor(TextAreaElement el) /*-{
		var xeditor = $wnd.CodeMirror.fromTextArea(el, {
			mode : "application/xml",
			lineNumbers : true,
			indentWithTabs : true,
			indentUnit : 4,
			onCursorActivity : function() {
				xeditor.setLineClass(hlLine, null);
				hlLine = xeditor.setLineClass(xeditor.getCursor().line,
						"activeline");
			}
		});
		var hlLine = xeditor.setLineClass(0, "activeline");
		this.@org.jdna.bmt.web.client.ui.xmleditor.XMLEditorPanel::editor = xeditor;
	}-*/;

	@Override
	protected void onAttach() {
		super.onAttach();
		if (editor == null) {
			initEditor(code);
		}
		focus();
	}

	public native String getValue() /*-{
		return this.@org.jdna.bmt.web.client.ui.xmleditor.XMLEditorPanel::editor
				.getValue();
	}-*/;

	public native void setValue(String data) /*-{
		this.@org.jdna.bmt.web.client.ui.xmleditor.XMLEditorPanel::editor
				.setValue(data);
	}-*/;

	public native void setMarker(int line, String text, String className) /*-{
		this.@org.jdna.bmt.web.client.ui.xmleditor.XMLEditorPanel::editor
				.setMarker(line, text, className);
	}-*/;

	public native void clearMarker(int line) /*-{
		this.@org.jdna.bmt.web.client.ui.xmleditor.XMLEditorPanel::editor
				.clearMarker(line);
	}-*/;

	public native void focus() /*-{
		this.@org.jdna.bmt.web.client.ui.xmleditor.XMLEditorPanel::editor
				.focus();
	}-*/;

	public boolean isChanged() {
		return (!getValue().equals(origData));
	}

	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONCONTEXTMENU:
			if (contextMenu!=null) {
				event.preventDefault();
				contextMenu(event);
			} else {
				super.onBrowserEvent(event);
			}
			break;
		default:
			super.onBrowserEvent(event);
			break;
		}
	}

	private void contextMenu(Event event) {
		  PopupPanel popupPanel = new PopupPanel(true);
		  popupPanel.add(contextMenu);
		  int x = DOM.eventGetClientX(event);
		  int y = DOM.eventGetClientY(event);
		  popupPanel.setPopupPosition(x, y);
		  popupPanel.show();
	}

	public native String getCurrentToken() /*-{
		var editor = this.@org.jdna.bmt.web.client.ui.xmleditor.XMLEditorPanel::editor;
		var cur = editor.getCursor(false);
		var token = editor.getTokenAt(cur)
		if (token==null) {
			token = {start: cur.ch, end: cur.ch, string: "", state: token.state, className: null};
		}
		return token.string;
	}-*/;
	
	public native void insertText(String text) /*-{
		var editor = this.@org.jdna.bmt.web.client.ui.xmleditor.XMLEditorPanel::editor;
		var cur = editor.getCursor(false);
		var token = editor.getTokenAt(cur)
		if (token==null) {
			token = {start: cur.ch, end: cur.ch, string: "", state: token.state, className: null};
		}
		editor.replaceRange(text, {line: cur.line, ch: token.start}, {line: cur.line, ch: token.end});
	}-*/;
}
