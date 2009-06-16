package org.jdna.bmt.web.server;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jdna.bmt.web.client.ui.status.StatusServices;
import org.jdna.bmt.web.client.ui.status.StatusValue;
import org.jdna.bmt.web.client.util.StringUtils;
import org.jdna.media.IMediaFile;
import org.jdna.sage.ScanningStatus;
import org.jdna.util.ProgressTracker.FailedItem;

import sagex.api.Configuration;
import sagex.api.Global;
import sagex.phoenix.util.SageTV;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StatusServicesImpl extends RemoteServiceServlet implements StatusServices {
    public StatusServicesImpl() {
        ServicesInit.init();
    }

    public List<StatusValue> getStatusInfo(String statusType) {
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
            status.add(new StatusValue("Custom Fields Configured",String.valueOf(!StringUtils.isEmpty(fields)),warn(StringUtils.isEmpty(fields))));
            
            status.add(new StatusValue("Last Scan Date",String.valueOf(new Date(ScanningStatus.getInstance().getLastScanTime()))));
            status.add(new StatusValue("Total Scanned",String.valueOf(ScanningStatus.getInstance().getTotalScanned())));
            
            List<IMediaFile> succ = ScanningStatus.getInstance().getSuccessfulItems();
            if (succ.size()>0) {
                for (int i=0;i<succ.size();i++) {
                    IMediaFile mf = succ.get(0);
                    status.add(new StatusValue("Last Scanned MediaFile", mf.getTitle()));
                }
            }
            
            if (ScanningStatus.getInstance().getTotalFailed()>0) {
                status.add(new StatusValue("Total Failed",String.valueOf(ScanningStatus.getInstance().getTotalFailed())));
                List<FailedItem<IMediaFile>> failed = ScanningStatus.getInstance().getFailedItems();
                if (ScanningStatus.getInstance().getTotalFailed()>0) {
                    for (int i=0;i<failed.size();i++) {
                        FailedItem<IMediaFile> fi = failed.get(i);
                        status.add(new StatusValue("("+(i+1)+") Failed MediaFile", fi.getItem().getName(), StatusValue.ERROR, fi.getMessage()));
                    }
                }
            }
        } else if ("sagetv".equals(statusType)) {
            status.add(new StatusValue("Version", SageTV.getSageVersion()));
            status.add(new StatusValue("Required Version", phoenix.api.GetRequiredSageVersion(), warn(!phoenix.api.IsAtLeastVersion(SageTV.getSageVersion(), phoenix.api.GetRequiredSageVersion()))));
            status.add(new StatusValue("Java Version", System.getProperty("java.version")));
            status.add(new StatusValue("Server", String.valueOf(!Global.IsClient()), warn(Global.IsClient()), "BMT Web UI should be running on the SageTV server."));

            status.add(new StatusValue("Recordings Used Diskspace", String.format("%,.2fG", ((float)Global.GetUsedVideoDiskspace())/1000.0f/1000.0f/1000.0f)));
            status.add(new StatusValue("Library Import Scan in Progress", String.valueOf(Global.IsDoingLibraryImportScan())));
        } else {
            status.add(new StatusValue("Unknown Status", statusType, StatusValue.ERROR));
        }
        return status;
    }

    private int error(boolean b) {
        return (b) ? StatusValue.ERROR : StatusValue.NORMAL;
    }

    private int warn(boolean b) {
        return (b) ? StatusValue.WARN : StatusValue.NORMAL;
    }

}
