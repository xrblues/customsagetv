package org.jdna.bmt.web.server;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.ui.status.StatusServices;
import org.jdna.bmt.web.client.ui.status.StatusValue;
import org.jdna.bmt.web.client.util.StringUtils;
import org.jdna.process.MetadataItem;
import org.jdna.process.ProgressSingleton;

import sagex.api.Configuration;
import sagex.api.Global;
import sagex.phoenix.progress.TrackedItem;
import sagex.phoenix.util.SageTV;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StatusServicesImpl extends RemoteServiceServlet implements StatusServices {
    private static final Logger log  = Logger.getLogger(StatusServicesImpl.class);
    
    public StatusServicesImpl() {
        ServicesInit.init();
    }

    public List<StatusValue> getStatusInfo(String statusType) {
        try {
            List<StatusValue> status = new LinkedList<StatusValue>();
            if ("phoenix".equals(statusType)) {
                status.add(new StatusValue("Version", phoenix.api.GetVersion()));
                status.add(new StatusValue("sagex.api Version", sagex.api.Version.GetVersion(), warn(!phoenix.api.IsAtLeastVersion(sagex.api.Version.GetVersion(), phoenix.api.GetRequiredSagexApiVersion()))));
                status.add(new StatusValue("Fanart Enabled", String.valueOf(phoenix.api.IsFanartEnabled()), warn(!phoenix.api.IsFanartEnabled()), "Fanart Cannot work if it is not enabled :)"));
                status.add(new StatusValue("Fanart Folder", phoenix.api.GetFanartCentralFolder(), error(!new File(phoenix.api.GetFanartCentralFolder()).exists())));

            } else if ("bmt".equals(statusType)) {
                status.add(new StatusValue("Version", bmt.api.GetVersion()));
                String plugins = Configuration.GetServerProperty("mediafile_metadata_parser_plugins", null);
                boolean isMDPluginEnabled = plugins != null && plugins.indexOf("org.jdna.sage.MetadataUpdaterPlugin") != -1;
                status.add(new StatusValue("Automatic Plugin Enabled (Server)", String.valueOf(isMDPluginEnabled), warn(!isMDPluginEnabled)));
                String fields = Configuration.GetServerProperty("custom_metadata_properties", null);
                status.add(new StatusValue("Custom Fields Configured", String.valueOf(!StringUtils.isEmpty(fields)), warn(StringUtils.isEmpty(fields))));

                status.add(new StatusValue("Last Scan Date", String.valueOf(ProgressSingleton.getTracker().getLastUpdated())));

                if (ProgressSingleton.getSuccessCount()>0) {
                    status.add(new StatusValue("Total Success", String.valueOf(ProgressSingleton.getSuccessCount())));
                    List<TrackedItem<MetadataItem>> succ = ProgressSingleton.getSuccess();
                    for (int i = 0; i < succ.size(); i++) {
                        TrackedItem<MetadataItem> mf = succ.get(i);
                        if (mf!=null) {
                            status.add(new StatusValue("MediaFile", mf.getItem().getFile().getTitle()));
                        }
                    }
                }

                if (ProgressSingleton.getFailedCount() > 0) {
                    status.add(new StatusValue("Total Failed", String.valueOf(ProgressSingleton.getFailedCount())));
                    List<TrackedItem<MetadataItem>> failed = ProgressSingleton.getFailed();
                        for (int i = 0; i < failed.size(); i++) {
                            TrackedItem<MetadataItem> fi = failed.get(i);
                            if (fi!=null) {
                                if (fi.getItem()!=null) {
                                    status.add(new StatusValue("(" + (i + 1) + ") Failed MediaFile", fi.getItem().getFile().getTitle(), StatusValue.ERROR, fi.getMessage()));
                                } else {
                                    status.add(new StatusValue("(" + (i + 1) + ") Failed MediaFile", "No Name", StatusValue.ERROR, fi.getMessage()));
                                }
                            } 
                    }
                }
            } else if ("sagetv".equals(statusType)) {
                status.add(new StatusValue("Version", SageTV.getSageVersion()));
                status.add(new StatusValue("Required Version", phoenix.api.GetRequiredSageVersion(), warn(!phoenix.api.IsAtLeastVersion(SageTV.getSageVersion(), phoenix.api.GetRequiredSageVersion()))));
                status.add(new StatusValue("Java Version", System.getProperty("java.version")));
                status.add(new StatusValue("Server", String.valueOf(!Global.IsClient()), warn(Global.IsClient()), "BMT Web UI should be running on the SageTV server."));

                if (!Global.IsDoingLibraryImportScan()) {
                    status.add(new StatusValue("Recordings Used Diskspace", String.format("%,.2f G", ((float) Global.GetUsedVideoDiskspace()) / 1000.0f / 1000.0f / 1000.0f)));
                }
                status.add(new StatusValue("Library Import Scan in Progress", String.valueOf(Global.IsDoingLibraryImportScan()), warn(Global.IsDoingLibraryImportScan())));

                Runtime runtime = Runtime.getRuntime();

                long maxMemory = runtime.maxMemory();
                long allocatedMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();

                status.add(new StatusValue("JVM Max Memory", String.format("%,d M", maxMemory / 1024), warn(maxMemory < 700000000)));
                status.add(new StatusValue("JVM Allocated Memory", String.format("%,d M", allocatedMemory / 1024)));
                status.add(new StatusValue("JVM Total Free Memory", String.format("%,d M", (freeMemory + (maxMemory - allocatedMemory)) / 1024)));
            } else {
                status.add(new StatusValue("Unknown Status", statusType, StatusValue.ERROR));
            }
            return status;
        } catch (Throwable t) {
            log.error("Status Failed:  " + statusType, t);
            throw new RuntimeException(t);
        }
    }

    private int error(boolean b) {
        return (b) ? StatusValue.ERROR : StatusValue.NORMAL;
    }

    private int warn(boolean b) {
        return (b) ? StatusValue.WARN : StatusValue.NORMAL;
    }

}
