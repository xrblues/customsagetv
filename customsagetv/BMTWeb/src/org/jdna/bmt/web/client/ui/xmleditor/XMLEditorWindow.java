package org.jdna.bmt.web.client.ui.xmleditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.util.AsyncServiceReply;
import org.jdna.bmt.web.client.ui.util.InputDialog;
import org.jdna.bmt.web.client.ui.util.MessageDialog;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.ui.widgets.PopupMenu;
import org.jdna.bmt.web.client.ui.widgets.PopupMenu.SimpleMenuAdapter;
import org.jdna.bmt.web.client.ui.xmleditor.PhoenixConfiguration.ConfigType;
import org.jdna.bmt.web.client.ui.xmleditor.XmlFileEntry.FileType;
import org.jdna.bmt.web.client.util.MessageBus;
import org.jdna.bmt.web.client.util.MessageHandler;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class XMLEditorWindow extends Composite implements MessageHandler {
	private PhoenixConfigurationAsync configServer = GWT
			.create(PhoenixConfiguration.class);

	protected static final int NEWFILE_VFS = 1;
	protected static final int NEWFILE_MENU = 2;

	public static final String MSG_XML_DATA = "xmleditor.loadxmldata";

	protected static final String SAMPLE_VFS = "<!DOCTYPE vfs SYSTEM \"vfs.dtd\">\n"
			+ "<vfs>\n"
			+ "    <views>\n"
			+ "		<view name=\"my.test.viewid\" label=\"My Test View Name\">\n"
			+ "			<description>Description of your view goes here</description>\n"
			+ "\n"
			+ "		    <!-- Add tags so that you view is visible in different sections -->\n"
			+ "			<tag value=\"video\"/>\n"
			+ "			<tag value=\"tv\"/>\n"
			+ "		    \n"
			+ "		        <!-- Check the main vfs for the complete list of view sources -->\n"
			+ "			<view-source name=\"phoenix.view.source.videos\"/>\n"
			+ "\n"
			+ "		        <!-- presentations control how the information is displayed at each folder level -->\n"
			+ "			<presentation>\n"
			+ "				<group by=\"parental-ratings\">\n"
			+ "					<option name=\"empty-foldername\" value=\"Not Rated\"/>\n"
			+ "				</group>\n"
			+ "				<sort by=\"title\">\n"
			+ "					<option name=\"sort-order\" value=\"asc\"/>\n"
			+ "				</sort>\n"
			+ "			</presentation>\n"
			+ "		</view>		\n"
			+ "	</views>\n" + "</vfs>";
	
	protected static final String SAMPLE_MENU = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<!DOCTYPE menus SYSTEM \"menus.dtd\">\n" + 
			"\n" + 
			"<menus>\n" + 
			"  <fragment parentMenu=\"phoenix.menu.lz\" insertBefore=\"phoenix.view.util.recentimports\">\n" + 
			"    <menuItem name=\"my.test.menuid\" label=\"My Test Menu Label\" visible=\"true\">\n" + 
			"      <description><![CDATA[" +
			"         Describe it Here!!" +
			"      ]]></description>\n" + 
			"      <eval>AddGlobalContext(\"DefaultView\", \"phoenix.view.source.allimportedvideo\")</eval>\n" + 
			"      <screen name=\"Phoenix Universal Media Browser\"/>\n" + 
			"    </menuItem>\n" + 
			"  </fragment>\n" + 
			"</menus>";

	private static XMLEditorWindowUiBinder uiBinder = GWT
			.create(XMLEditorWindowUiBinder.class);

	interface XMLEditorWindowUiBinder extends UiBinder<Widget, XMLEditorWindow> {
	}

	private MessageBus eventBus = new MessageBus();

	@UiField
	SimplePanel panel;

	@UiField
	Label filename;
	@UiField
	Label error;

	private XmlFileEntry editingFile;

	public XMLEditorWindow() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@UiHandler("load")
	public void loadFile(ClickEvent evt) {
		ConfigChooser chooser = new ConfigChooser("Load Configuration",
				eventBus);
		chooser.center();
		chooser.show();
	}

	@UiHandler("save")
	public void saveFile(ClickEvent evt) {
		if (editingFile == null) {
			MessageDialog.showMessage("Missing File",
					"You don't have any files to save");
			return;
		}

		if (StringUtils.isEmpty(editingFile.name)) {
			InputDialog d = new InputDialog("File Name",
					"Enter filename for the file", "") {
				@Override
				public void onOK(String text) {
					if (StringUtils.isEmpty(text)) {
						MessageDialog.showMessage("Save",
								"Can't save without a filename");
						return;
					}
					editingFile.name = text;
					hide();
					save();
				}
			};
			d.center();
			d.show();
		} else {
			save();
		}
	}

	protected void save() {
		if (editingFile.error != null) {
			XMLEditorPanel e = getXMLEditor();
			if (e != null) {
				e.clearMarker(editingFile.error.line - 1);
			}
			error.setText("");
		}
		editingFile.contents = getXMLEditor().getValue();
		configServer.saveXmlFile(editingFile,
				new AsyncCallback<ServiceReply<XmlFileEntry>>() {
					@Override
					public void onSuccess(ServiceReply<XmlFileEntry> result) {
						if (result.getCode() == 0) {
							Application.fireNotification("Saved");
						} else {
							if (result.getData().error != null) {
								Application
										.fireErrorEvent("Unable to save; there were XML Parsing errors");
							} else {
								Application
										.fireErrorEvent("Failed to save file.  Message was "
												+ result.getMessage());
							}
						}
						updateEditingPanel(result.getData());
					}

					@Override
					public void onFailure(Throwable caught) {
						Application.fireErrorEvent("Failed to save", caught);
					}
				});
	}

	protected void updateMarkers(XmlFileEntry data) {
		if (data.error == null) {
			// clear markers
		} else {
			XMLEditorPanel w = getXMLEditor();
			// w.setMarker(data.error.line-1, "\u2639", "XMLError");
			w.setMarker(data.error.line - 1,
					"<span style=\"color: #900\">●</span> %N%", null);
			GWT.log("Added Markers: " + data.error.line + "; "
					+ data.error.message);
			error.setText(data.error.message);
		}
	}

	private XMLEditorPanel getXMLEditor() {
		if (panel.getWidget() instanceof XMLEditorPanel) {
			return (XMLEditorPanel) panel.getWidget();
		}
		return null;
	}

	@UiHandler("newfile")
	public void newFile(ClickEvent evt) {
		PopupMenu pm = new PopupMenu("Select New File Type",
				new PopupMenu.MenuSelectionHandler() {
					@Override
					public void onMenuItemSelected(Object data) {
						int id = (Integer) ((SimpleMenuAdapter.SimpleItem) data)
								.getId();
						switch (id) {
						case NEWFILE_VFS: {
							HashMap<String, Object> msg = new HashMap<String, Object>();
							XmlFileEntry fe = new XmlFileEntry();
							fe.configType = ConfigType.VFS;
							fe.fileType = FileType.User;
							fe.contents = SAMPLE_VFS;
							msg.put("xml", fe);
							eventBus.postMessage(MSG_XML_DATA, msg);
							break;
						}
						case NEWFILE_MENU: {
							HashMap<String, Object> msg2 = new HashMap<String, Object>();
							XmlFileEntry fe = new XmlFileEntry();
							fe.configType = ConfigType.Menu;
							fe.fileType = FileType.User;
							fe.contents = SAMPLE_MENU;
							msg2.put("xml", fe);
							eventBus.postMessage(MSG_XML_DATA, msg2);
							break;
						}
						}
					}
				});

		SimpleMenuAdapter sma = (SimpleMenuAdapter) pm.getListAdapter();
		sma.addItem(NEWFILE_VFS, "New VFS File");
		sma.addItem(NEWFILE_MENU, "New Menu Fragment File");
		sma.fireDataChanged();
		pm.center();
		pm.show();
	}

	@Override
	public void onMessageReceived(String msg, Map<String, ?> args) {
		if (MSG_XML_DATA.equals(msg)) {

			// if (panel.getWidget() instanceof XMLEditorPanel) {
			// if (((XMLEditorPanel)panel.getWidget()).isChanged()) {
			// if
			// (Window.confirm("You have unsaved changes.  If you continue those changes will be lost."))
			// {
			// return;
			// }
			// }
			// }
			updateEditingPanel((XmlFileEntry) args.get("xml"));
		}
	}

	private void updateEditingPanel(XmlFileEntry fe) {
		if (fe.name != null) {
			filename.setText(fe.file);
		} else {
			filename.setText("Untitled File");
		}
		panel.setWidget(new XMLEditorPanel(fe, getContextMenu(fe)));
		editingFile = fe;
		updateMarkers(fe);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		eventBus.addHandler(MSG_XML_DATA, this);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		eventBus.removeHandler(MSG_XML_DATA, this);
	}

	private abstract class InsertTextCommand implements Command {
		protected String text;

		public InsertTextCommand(String text) {
			this.text = text;
		}

		@Override
		public void execute() {
			XMLEditorPanel p = getXMLEditor();
			String tok = p.getCurrentToken();
			p.insertText(resolveText(tok));
		}

		protected abstract String resolveText(String token);
	}

	private class InsertViewTextCommand extends InsertTextCommand {
		public InsertViewTextCommand(String text) {
			super(text);
		}

		@Override
		protected String resolveText(String tok) {
			String ins = text;
			if (tok == null || tok.trim().length() == 0) {
				ins = "<view-source name=\"" + text + "\"/>";
			} else if (tok.charAt(0) == '"') {
				ins = '"' + text + '"';
			}
			return ins;
		}
	}

	private class InsertSourceTextCommand extends InsertTextCommand {
		public InsertSourceTextCommand(String text) {
			super(text);
		}

		@Override
		protected String resolveText(String tok) {
			String ins = text;
			if (tok == null || tok.trim().length() == 0) {
				ins = "<view-source name=\"" + text + "\"/>";
			} else if (tok.charAt(0) == '"') {
				ins = '"' + text + '"';
			}
			return ins;
		}
	}

	private MenuBar getContextMenu(XmlFileEntry file) {
		MenuBar popupMenuBar = new MenuBar(true);
		MenuBar views = new MenuBar(true) {
			@Override
			protected void onLoad() {
				super.onLoad();
				clearItems();
				configServer
						.getViews(new AsyncServiceReply<ArrayList<NamedItem>>() {
							@Override
							public void onOK(ArrayList<NamedItem> result) {
								GWT.log("Got Views: " + result.size());
								for (NamedItem ni : result) {
									GWT.log("Adding item: " + ni.getName());
									addItem(ni.getName(),
											new InsertViewTextCommand(ni
													.getName()));
								}
							}
						});
			}
		};
		views.addItem("Loading...", (Command) null);

		MenuBar sources = new MenuBar(true) {
			@Override
			protected void onLoad() {
				super.onLoad();
				clearItems();
				configServer
						.getSources(new AsyncServiceReply<ArrayList<NamedItem>>() {
							@Override
							public void onOK(ArrayList<NamedItem> result) {
								for (NamedItem ni : result) {
									addItem(ni.getName(),
											new InsertSourceTextCommand(ni
													.getName()));
								}
							}
						});
			}
		};
		sources.addItem("Loading...", (Command) null);

		MenuBar filters = new MenuBar(true) {
			@Override
			protected void onLoad() {
				super.onLoad();
				clearItems();
				configServer
						.getFilters(new AsyncServiceReply<ArrayList<NamedItem>>() {
							@Override
							public void onOK(ArrayList<NamedItem> result) {
								for (NamedItem ni : result) {
									addItem(ni.getName(),
											new InsertSourceTextCommand(ni
													.getName()));
								}
							}
						});
			}
		};
		filters.addItem("Loading...", (Command) null);

		MenuBar sorts = new MenuBar(true) {
			@Override
			protected void onLoad() {
				super.onLoad();
				clearItems();
				configServer
						.getSorts(new AsyncServiceReply<ArrayList<NamedItem>>() {
							@Override
							public void onOK(ArrayList<NamedItem> result) {
								for (NamedItem ni : result) {
									addItem(ni.getName(),
											new InsertSourceTextCommand(ni
													.getName()));
								}
							}
						});
			}
		};
		sorts.addItem("Loading...", (Command) null);

		MenuBar groups = new MenuBar(true) {
			@Override
			protected void onLoad() {
				super.onLoad();
				clearItems();
				configServer
						.getGroups(new AsyncServiceReply<ArrayList<NamedItem>>() {
							@Override
							public void onOK(ArrayList<NamedItem> result) {
								for (NamedItem ni : result) {
									addItem(ni.getName(),
											new InsertSourceTextCommand(ni
													.getName()));
								}
							}
						});
			}
		};
		groups.addItem("Loading...", (Command) null);

		popupMenuBar.addItem("Views", views);
		popupMenuBar.addItem("Sources", sources);
		popupMenuBar.addItem("Filters", filters);
		popupMenuBar.addItem("Sorts", sorts);
		popupMenuBar.addItem("Groups", groups);

		popupMenuBar.setVisible(true);
		return popupMenuBar;

	}
}
