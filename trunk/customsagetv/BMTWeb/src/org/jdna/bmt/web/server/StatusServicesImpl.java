package org.jdna.bmt.web.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.Version;
import org.jdna.bmt.web.client.ui.status.StatusServices;
import org.jdna.bmt.web.client.ui.status.StatusValue;
import org.jdna.bmt.web.client.ui.status.SystemMessage;

import sagex.api.Global;
import sagex.api.PluginAPI;
import sagex.api.SystemMessageAPI;
import sagex.phoenix.ConfigurationErrorEventBus;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.download.DownloadManager;
import sagex.phoenix.metadata.MetadataConfiguration;
import sagex.phoenix.plugin.PhoenixPlugin;
import sagex.phoenix.task.RetryTaskManager;
import sagex.phoenix.tools.support.JarInfo;
import sagex.phoenix.tools.support.JarUtil;
import sagex.phoenix.util.SageTV;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings({ "serial", "deprecation" })
public class StatusServicesImpl extends RemoteServiceServlet implements
		StatusServices {
	private static final Logger log = Logger
			.getLogger(StatusServicesImpl.class);
	private Map<Integer, Object> systemMessages = new HashMap<Integer, Object>();

	public StatusServicesImpl() {
		ServicesInit.init();
	}

	public List<StatusValue> getStatusInfo(String statusType) {
		try {
			List<StatusValue> status = new LinkedList<StatusValue>();
			if ("phoenix".equals(statusType)) {
				MetadataConfiguration config = GroupProxy
						.get(MetadataConfiguration.class);
				status.add(new StatusValue("Version", phoenix.system
						.GetVersion()));
				status.add(new StatusValue("sagex.api Version",
						sagex.api.Version.GetVersion(), warn(!phoenix.util
								.IsAtLeastVersion(sagex.api.Version
										.GetVersion(), phoenix.system
										.GetRequiredSagexApiVersion()))));
				status.add(new StatusValue("Fanart Enabled", String
						.valueOf(phoenix.fanart.IsFanartEnabled()),
						warn(!phoenix.fanart.IsFanartEnabled()),
						"Fanart Cannot work if it is not enabled :)"));
				if (config.isFanartEnabled()) {
					status.add(new StatusValue("Fanart Folder", phoenix.fanart
							.GetFanartCentralFolder(), error(!new File(
							phoenix.fanart.GetFanartCentralFolder()).exists())));
				}

				status.add(new StatusValue(
						"Automatic Metadata/Fanart Lookups Enabled", String
								.valueOf(config.isAutomatedFanartEnabled())));

				if (config.isAutomatedFanartEnabled()) {
					// downloads in progress (ie, fanart)
					DownloadManager dm = Phoenix.getInstance()
							.getDownloadManager();
					DownloadManager.Status dst = dm.getStatus();
					if (dst.waiting > 0) {
						status.add(new StatusValue("Downloads Threads", String
								.valueOf(dst.threads)));
						status.add(new StatusValue("Downloads in Progress",
								String.valueOf(dst.waiting)));
					}

					Object oo[] = PluginAPI.GetAllAvailablePlugins();
					if (oo != null) {
						for (Object p : oo) {
							Object impl = PluginAPI.GetPluginImplementation(p);
							if (impl instanceof PhoenixPlugin) {
								RetryTaskManager rtt = ((PhoenixPlugin) impl)
										.getRetryTaskManager();
								RetryTaskManager.Status rtts = rtt.getStatus();
								if (rtts.queueLength > 0) {
									status.add(new StatusValue(
											"Metadata Retry Threads", String
													.valueOf(rtts.threads)));
									status.add(new StatusValue(
											"Metadata Reties in Progress",
											String.valueOf(rtts.queueLength)));
								}
								break;
							}
						}
					}
				}
				
				File masterXml = new File(Phoenix.getInstance().getVFSManager().getSystemFiles().getDir(), "x-vfs.xml");
				if (!masterXml.exists()) {
					status.add(new StatusValue("Missing Views", "Missing x-vfs.xml file", StatusValue.ERROR, "Missing Phoenix Master VFS file: " + masterXml));
				}
				
				status.add(new StatusValue("Loaded VFS Views", String.valueOf(phoenix.umb.GetVisibleViews().size())));
			} else if ("sagetv".equals(statusType)) {
				status.add(new StatusValue("Version", SageTV.getSageVersion()));
				status.add(new StatusValue("Required Version", phoenix.system
						.GetRequiredSageVersion(), warn(!phoenix.util
						.IsAtLeastVersion(SageTV.getSageVersion(),
								phoenix.system.GetRequiredSageVersion()))));
				status.add(new StatusValue("Java Version", System
						.getProperty("java.version")));
				status.add(new StatusValue("Server", String.valueOf(!Global
						.IsClient()), warn(Global.IsClient()),
						"BMT Web UI should be running on the SageTV server."));

				if (!Global.IsDoingLibraryImportScan()) {
					status.add(new StatusValue(
							"Recordings Used Diskspace",
							String.format(
									"%,.2f G",
									((float) Global.GetUsedVideoDiskspace()) / 1000.0f / 1000.0f / 1000.0f)));
				}

				status.add(new StatusValue("Library Import Scan in Progress",
						String.valueOf(Global.IsDoingLibraryImportScan()),
						warn(Global.IsDoingLibraryImportScan())));

				Runtime runtime = Runtime.getRuntime();

				long maxMemory = runtime.maxMemory();
				long allocatedMemory = runtime.totalMemory();
				long freeMemory = runtime.freeMemory();

				status.add(new StatusValue("JVM Max Memory", String.format(
						"%,d M", maxMemory / 1024), warn(maxMemory < 700000000)));
				status.add(new StatusValue("JVM Allocated Memory", String
						.format("%,d M", allocatedMemory / 1024)));
				status.add(new StatusValue(
						"JVM Total Free Memory",
						String.format(
								"%,d M",
								(freeMemory + (maxMemory - allocatedMemory)) / 1024)));
			} else if ("jars".equals(statusType)) {
				fillJarsStatus(status);
			} else {
				status.add(new StatusValue("Unknown Status", statusType,
						StatusValue.ERROR));
			}
			return status;
		} catch (Throwable t) {
			log.error("Status Failed:  " + statusType, t);
			throw new RuntimeException(t);
		}
	}

	private void fillJarsStatus(List<StatusValue> status) {
		File jarsDir = new File("JARs");
		List<JarInfo> jars = JarUtil.findDuplicateJars(jarsDir);
		if (jars.size() > 0) {
			StatusValue sv = new StatusValue("Duplicate JARs", null);
			sv.setSepartor(true);
			status.add(sv);
			for (JarInfo ji : jars) {
				status.add(new StatusValue(String.valueOf(ji.getFile()), ji
						.getVersion(), StatusValue.ERROR,
						"This Jar should be removed"));
			}
		}

		jars = JarUtil.getJarInfo(jarsDir);
		StatusValue sv = new StatusValue("Installed JARs", null);
		sv.setSepartor(true);
		status.add(sv);
		for (JarInfo ji : jars) {
			status.add(new StatusValue(String.valueOf(ji.getFile()), ji
					.getVersion()));
		}
	}

	private int error(boolean b) {
		return (b) ? StatusValue.ERROR : StatusValue.NORMAL;
	}

	private int warn(boolean b) {
		return (b) ? StatusValue.WARN : StatusValue.NORMAL;
	}

	public List<SystemMessage> getSystemMessages() {
		systemMessages.clear();
		Object[] all = SystemMessageAPI.GetSystemMessages();
		List<SystemMessage> msgs = new ArrayList<SystemMessage>();
		for (Object o : all) {
			SystemMessage sm = new SystemMessage();
			sm.setLevel(SystemMessageAPI.GetSystemMessageLevel(o));
			sm.setRepeat(SystemMessageAPI.GetSystemMessageRepeatCount(o));
			sm.setStartTime(SystemMessageAPI.GetSystemMessageTime(o));
			sm.setEndTime(SystemMessageAPI.GetSystemMessageEndTime(o));
			sm.setTypeCode(SystemMessageAPI.GetSystemMessageTypeCode(o));
			sm.setTypeName(SystemMessageAPI.GetSystemMessageTypeName(o));
			sm.setMessage(SystemMessageAPI.GetSystemMessageString(o));
			sm.setId(o.hashCode());
			msgs.add(sm);
			systemMessages.put(o.hashCode(), o);
		}
		// reverse sort date
		Collections.sort(msgs, new Comparator<SystemMessage>() {
			public int compare(SystemMessage o1, SystemMessage o2) {
				if (o1.getStartTime() > o2.getStartTime())
					return -1;
				if (o1.getStartTime() < o2.getStartTime())
					return 1;
				return 0;
			}
		});
		return msgs;
	}

	public String getBMTVersion() {
		return Version.VERSION;
	}

	public void clearSystemMessages() {
		SystemMessageAPI.DeleteAllSystemMessages();
		systemMessages.clear();
		return;
	}

	public void deleteSystemMessage(int id) {
		Object o = systemMessages.get(id);
		if (o != null) {
			systemMessages.remove(id);
			SystemMessageAPI.DeleteSystemMessage(o);
		}
	}
	
	public boolean hasConfigurationErrors() {
		return ConfigurationErrorEventBus.getBus().getErrors().size() > 0;
	}
}
