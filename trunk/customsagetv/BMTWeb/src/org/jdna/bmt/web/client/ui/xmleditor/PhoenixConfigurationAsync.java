package org.jdna.bmt.web.client.ui.xmleditor;

import java.util.ArrayList;

import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.ui.xmleditor.PhoenixConfiguration.ConfigType;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PhoenixConfigurationAsync {
	void getConfigurationFiles(ConfigType type,	AsyncCallback<ServiceReply<ArrayList<XmlFileEntry>>> callback);
	void loadXmlFile(XmlFileEntry entry, AsyncCallback<ServiceReply<XmlFileEntry>> callback);
	void saveXmlFile(XmlFileEntry entry, AsyncCallback<ServiceReply<XmlFileEntry>> callback);
	void getViews(AsyncCallback<ServiceReply<ArrayList<NamedItem>>> callback);
	void getSources(AsyncCallback<ServiceReply<ArrayList<NamedItem>>> callback);
	void getFilters(AsyncCallback<ServiceReply<ArrayList<NamedItem>>> callback);
	void getGroups(AsyncCallback<ServiceReply<ArrayList<NamedItem>>> callback);
	void getSorts(AsyncCallback<ServiceReply<ArrayList<NamedItem>>> callback);
	void getMenus(AsyncCallback<ServiceReply<ArrayList<NamedItem>>> callback);
	void getMenuItems(String menu, AsyncCallback<ServiceReply<ArrayList<NamedItem>>> callback);
	void addViewMenu(AddMenu menu, AsyncCallback<ServiceReply<Void>> callback);
}
