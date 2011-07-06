package org.jdna.bmt.web.client.ui.xmleditor;

import java.util.ArrayList;

import org.jdna.bmt.web.client.ui.util.ServiceReply;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("phoenixconfig")
public interface PhoenixConfiguration extends RemoteService {
	public static enum ConfigType {VFS, Menu, MediaTitles};
	public ServiceReply<ArrayList<XmlFileEntry>> getConfigurationFiles(ConfigType type);
	public ServiceReply<XmlFileEntry> loadXmlFile(XmlFileEntry entry);
	public ServiceReply<XmlFileEntry> saveXmlFile(XmlFileEntry entry);
	public ServiceReply<ArrayList<NamedItem>> getViews();
	public ServiceReply<ArrayList<NamedItem>> getSources();
	public ServiceReply<ArrayList<NamedItem>> getFilters();
	public ServiceReply<ArrayList<NamedItem>> getSorts();
	public ServiceReply<ArrayList<NamedItem>> getGroups();
	public ServiceReply<ArrayList<NamedItem>> getMenus();
	public ServiceReply<ArrayList<NamedItem>> getMenuItems(String menu);
	public ServiceReply<Void> addViewMenu(AddMenu menu);
}
