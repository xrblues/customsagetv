package org.jdna.bmt.web.client.ui.xmleditor;

import java.util.ArrayList;

import org.jdna.bmt.web.client.ui.input.NVP;
import org.jdna.bmt.web.client.ui.util.AsyncServiceReply;
import org.jdna.bmt.web.client.ui.xmleditor.PhoenixConfiguration.ConfigType;
import org.jdna.bmt.web.client.util.MessageBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ConfigChooser extends DialogBox {
	private PhoenixConfigurationAsync configServer = GWT.create(PhoenixConfiguration.class);
	
	private static ConfigChooserUiBinder uiBinder = GWT
			.create(ConfigChooserUiBinder.class);

	interface ConfigChooserUiBinder extends UiBinder<Widget, ConfigChooser> {
	}

	@UiField ListBox configType;
	@UiField ListBox configFiles;

	private ArrayList<XmlFileEntry> files;

	private MessageBus bus;
	
	public ConfigChooser(String title, MessageBus bus) {
		super(false, true);
		setText(title);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.bus=bus;
		
		configType.addItem(ConfigType.VFS.name(), ConfigType.VFS.name());
		configType.addItem(ConfigType.Menu.name(), ConfigType.Menu.name());
		configType.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				loadFiles(ConfigType.valueOf(configType.getValue(configType.getSelectedIndex())));
			}
		});
		
		configFiles.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				loadFile(configFiles.getSelectedIndex());
			}
		});
		
		configType.setSelectedIndex(0);
		loadFiles(ConfigType.VFS);
	}

	protected void loadFile(int selectedIndex) {
		configServer.loadXmlFile(files.get(selectedIndex), new AsyncServiceReply<XmlFileEntry>() {
			@Override
			public void onOK(XmlFileEntry result) {
				bus.postMessage(XMLEditorWindow.MSG_XML_DATA, new NVP("xml", result));
				hide();
			}
		});
	}

	protected void loadFiles(ConfigType type) {
		configServer.getConfigurationFiles(type, new AsyncServiceReply<ArrayList<XmlFileEntry>>() {
			@Override
			public void onOK(ArrayList<XmlFileEntry> result) {
				loadFileList(result);
			}
		});
	}

	protected void loadFileList(ArrayList<XmlFileEntry> result) {
		this.files = result;
		configFiles.clear();
		for (XmlFileEntry fe: result) {
			configFiles.addItem(fe.name);
		}
	}
	
	
	@UiHandler("cancel")
	public void onCancel(ClickEvent evt) {
		hide();
	}
}
