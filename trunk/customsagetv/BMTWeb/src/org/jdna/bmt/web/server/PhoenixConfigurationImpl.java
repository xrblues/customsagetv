package org.jdna.bmt.web.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.ui.xmleditor.AddMenu;
import org.jdna.bmt.web.client.ui.xmleditor.NamedItem;
import org.jdna.bmt.web.client.ui.xmleditor.NamedItemSorter;
import org.jdna.bmt.web.client.ui.xmleditor.PhoenixConfiguration;
import org.jdna.bmt.web.client.ui.xmleditor.XmlFileEntry;
import org.jdna.bmt.web.client.ui.xmleditor.XmlFileEntry.FileType;
import org.jdna.bmt.web.client.ui.xmleditor.XmlFileEntry.ParserError;
import org.xml.sax.SAXParseException;

import sagex.phoenix.Phoenix;
import sagex.phoenix.common.ManagedDirectory;
import sagex.phoenix.common.SystemConfigurationFileManager.ConfigurationType;
import sagex.phoenix.factory.Factory;
import sagex.phoenix.menu.IMenuItem;
import sagex.phoenix.menu.Menu;
import sagex.phoenix.menu.MenuBuilder;
import sagex.phoenix.menu.MenuItem;
import sagex.phoenix.menu.MenuManager;
import sagex.phoenix.vfs.VFSManager;
import sagex.phoenix.vfs.util.ImmutableVFSManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PhoenixConfigurationImpl extends RemoteServiceServlet implements PhoenixConfiguration {
    private static final Logger log = Logger.getLogger(PhoenixConfigurationImpl.class);
    
    public PhoenixConfigurationImpl() {
        ServicesInit.init();
    }
    
    private transient NamedItemSorter sorter= new NamedItemSorter();
    
    private void addFiles(ArrayList<XmlFileEntry> list, ManagedDirectory dir, FileType ftype, ConfigType ctype) {
    	if (dir!=null && dir.getFiles()!=null) {
			for (File f: dir.getFiles()) {
				XmlFileEntry fe = new XmlFileEntry();
				fe.configType=ctype;
				fe.fileType=ftype;
				fe.file=f.getAbsolutePath();
				fe.name=f.getName();
				list.add(fe);
			}
    	}
    }
    
	@Override
	public ServiceReply<ArrayList<XmlFileEntry>> getConfigurationFiles(ConfigType type) {
		ArrayList<XmlFileEntry> files = new ArrayList<XmlFileEntry>();
		if (type==ConfigType.VFS) {
			addFiles(files, Phoenix.getInstance().getVFSManager().getUserFiles(), FileType.User, type);
			addFiles(files, Phoenix.getInstance().getVFSManager().getSystemFiles(), FileType.System, type);
			return new ServiceReply<ArrayList<XmlFileEntry>>(files);
		} else if (type==ConfigType.Menu) {
			addFiles(files, Phoenix.getInstance().getMenuManager().getUserFiles(), FileType.User, type);
			addFiles(files, Phoenix.getInstance().getMenuManager().getSystemFiles(), FileType.System, type);
			return new ServiceReply<ArrayList<XmlFileEntry>>(files);
		}
		return new ServiceReply<ArrayList<XmlFileEntry>>(1,"Un-handled configuration type " + type);
	}
	
	@Override
	public ServiceReply<XmlFileEntry> loadXmlFile(XmlFileEntry entry) {
		try {
			entry.contents = FileUtils.readFileToString(new File(entry.file));
			return new ServiceReply<XmlFileEntry>(entry);
		} catch (IOException e) {
			return new ServiceReply<XmlFileEntry>(1, e.getMessage(), entry);
		}
	}

	@Override
	public ServiceReply<XmlFileEntry> saveXmlFile(XmlFileEntry entry) {
		if (entry.configType == ConfigType.VFS) {
			return saveVFS(entry);
		} else if (entry.configType == ConfigType.Menu) {
			return saveMenu(entry);
		} else  {
			return new ServiceReply<XmlFileEntry>(1, "Can't save unknown configuration type " + entry.configType);
		}
	}

	private ServiceReply<XmlFileEntry> saveMenu(XmlFileEntry entry) {
		try {
			MenuBuilder.buildMenus(entry.contents, Phoenix.getInstance().getMenuManager().getSystemFiles().getDir());
		} catch (Throwable t) {
			entry.error = new ParserError();
			entry.error.message = t.getMessage();
			if (t instanceof SAXParseException) {
				SAXParseException se = (SAXParseException) t;
				entry.error.line=se.getLineNumber();
				entry.error.col=se.getColumnNumber();
				entry.error.message=se.getLocalizedMessage();
			}
			return new ServiceReply<XmlFileEntry>(2, "Failed to save file: " + t.getMessage(), entry);
		}

		MenuManager mgr = Phoenix.getInstance().getMenuManager();
		String name = entry.name;
		if (!name.toLowerCase().endsWith(".xml")) {
			name = name + ".xml";
		}
		File newFile = new File(mgr.getUserFiles().getDir(), name);
		newFile.getParentFile().mkdirs();
		try {
			FileUtils.writeStringToFile(newFile, entry.contents);
		} catch (Exception e) {
			entry.error = new ParserError();
			entry.error.message = e.getMessage();
			return new ServiceReply<XmlFileEntry>(2, "Failed to save menu file: " + e.getMessage(), entry);
		}
		mgr.visitConfigurationFile(ConfigurationType.User, newFile);
		
		entry.file=newFile.getAbsolutePath();
		entry.name=newFile.getName();
		entry.error=null;
		return new ServiceReply<XmlFileEntry>(entry);
	}

	private ServiceReply<XmlFileEntry> saveVFS(XmlFileEntry entry) {
		VFSManager mgr = new ImmutableVFSManager(new File("userdata/tmp"), Phoenix.getInstance().getVFSManager());
		String name = entry.name;
		if (!name.toLowerCase().endsWith(".xml")) {
			name = name + ".xml";
		}
		File newFile = new File(mgr.getUserFiles().getDir(), name);
		newFile.getParentFile().mkdirs();
		try {
			FileUtils.writeStringToFile(newFile, entry.contents);
		} catch (IOException e) {
			return new ServiceReply<XmlFileEntry>(2, "Failed to save temp file: " + e.getMessage());
		}
		mgr.clearErrors();
		mgr.visitConfigurationFile(ConfigurationType.User, newFile);
		newFile.delete();
		if (mgr.getLastError()!=null) {
			if (mgr.getLastError() instanceof SAXParseException) {
				SAXParseException se = (SAXParseException) mgr.getLastError();
				ParserError pe = new ParserError();
				pe.line=se.getLineNumber();
				pe.col=se.getColumnNumber();
				pe.message=se.getLocalizedMessage();
				entry.error=pe;
				return new ServiceReply<XmlFileEntry>(2, "Parser Failed", entry);
			} else {
				return new ServiceReply<XmlFileEntry>(1, "Failed to save: " + mgr.getLastError().getMessage(), entry);
			}
		}
		
		// all we well, save for real.
		mgr = Phoenix.getInstance().getVFSManager();
		newFile = new File(mgr.getUserFiles().getDir(), name);
		try {
			FileUtils.writeStringToFile(newFile, entry.contents);
		} catch (IOException e) {
			return new ServiceReply<XmlFileEntry>(2, "Failed to save file: " + e.getMessage());
		}
		mgr.clearErrors();
		mgr.visitConfigurationFile(ConfigurationType.User, newFile);
		entry.file=newFile.getAbsolutePath();
		entry.name=newFile.getName();
		entry.error=null;
		return new ServiceReply<XmlFileEntry>(entry);
	}
	
	private ServiceReply<ArrayList<NamedItem>> getFactoryList(List<?> factories) {
		ArrayList<NamedItem> items = new ArrayList<NamedItem>();
		for (Object ff: factories) {
			Factory f = (Factory) ff;
			items.add(new NamedItem(f.getName(), f.getLabel()));
		}
		Collections.sort(items, sorter);
		return new ServiceReply<ArrayList<NamedItem>>(items);
	}

	@Override
	public ServiceReply<ArrayList<NamedItem>> getViews() {
		return getFactoryList(Phoenix.getInstance().getVFSManager().getVFSViewFactory().getFactories());
	}
	
	@Override
	public ServiceReply<ArrayList<NamedItem>> getSources() {
		return getFactoryList(Phoenix.getInstance().getVFSManager().getVFSSourceFactory().getFactories());
	}

	@Override
	public ServiceReply<ArrayList<NamedItem>> getFilters() {
		return getFactoryList(Phoenix.getInstance().getVFSManager().getVFSFilterFactory().getFactories());
	}

	@Override
	public ServiceReply<ArrayList<NamedItem>> getSorts() {
		return getFactoryList(Phoenix.getInstance().getVFSManager().getVFSSortFactory().getFactories());
	}

	@Override
	public ServiceReply<ArrayList<NamedItem>> getGroups() {
		return getFactoryList(Phoenix.getInstance().getVFSManager().getVFSGroupFactory().getFactories());
	}

	@Override
	public ServiceReply<ArrayList<NamedItem>> getMenus() {
		ArrayList<NamedItem> menus = new ArrayList<NamedItem>();
		
		for (Menu m: Phoenix.getInstance().getMenuManager().getMenus()) {
			menus.add(new NamedItem(m.getId(), phoenix.menu.GetLabel(m)));
		}
		Collections.sort(menus, sorter);
		return new ServiceReply<ArrayList<NamedItem>>(menus);
	}

	@Override
	public ServiceReply<ArrayList<NamedItem>> getMenuItems(String menu) {
		ArrayList<NamedItem> items = new ArrayList<NamedItem>();
		for (IMenuItem mi: phoenix.menu.GetMenuItems(menu)) {
			items.add(new NamedItem(mi.getId(), phoenix.menu.GetLabel(mi)));
		}
		
		// don't sort the items
		//Collections.sort(items, sorter);
		return new ServiceReply<ArrayList<NamedItem>>(items);
	}

	@Override
	public ServiceReply<Void> addViewMenu(AddMenu menu) {
		String insertBefore=null, insertAfter=null;
		if (menu.isBefore) {
			insertBefore=menu.parentMenuItemId;
		} else {
			insertAfter=menu.parentMenuItemId;
		}
		
		MenuItem mi = phoenix.menu.CreateMenuItem(null, menu.menuId, menu.menuLabel);
		if (menu.description!=null) {
			mi.description().setValue(menu.description);
		}
		
		phoenix.menu.AddSageEvalAction(mi, "AddGlobalContext(\"DefaultView\", \""+menu.viewId+"\" )");
		
		if (org.apache.commons.lang.StringUtils.isEmpty(menu.flowType)) {
			phoenix.menu.AddScreenAction(mi, "Phoenix Universal Media Browser");
		} else if ("BANNER".equals(menu.flowType) || "GRID".equals(menu.flowType)) {
			phoenix.menu.AddSageEvalAction(mi, "AddStaticContext( \"FlowType\" , \""+menu.flowType+"\" )");
			phoenix.menu.AddScreenAction(mi, "Phoenix UMB - Art Flow");
		} else if ("MOVIE".equals(menu.flowType)) {
			phoenix.menu.AddScreenAction(mi, "Phoenix UMB - Movie Flow");
		} else if ("COVER".equals(menu.flowType)) {
			phoenix.menu.AddScreenAction(mi, "Phoenix UMB - Cover Flow");
		} else {
			phoenix.menu.AddScreenAction(mi, "Phoenix Universal Media Browser");
		}
		
		if (phoenix.menu.SaveFragment(mi, menu.parentMenuId, insertBefore, insertAfter)) {
			phoenix.menu.ReloadMenus();
			return new ServiceReply<Void>(0, "Fragment saved");
		} else {
			return new ServiceReply<Void>(1, "Failed to save/add menu fragment");
		}
	}

}
