package org.jdna.bmt.web.client.ui.debug;

import java.util.Map;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.app.SupportOptions;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;




@RemoteServiceRelativePath("debug")
public interface DebugService extends RemoteService {
    public ServiceReply<Map<String,String>>getMetadata(String source, GWTMediaFile file);
    public long updateTimestamp(GWTMediaFile file);
    public String createSupportRequest(SupportOptions options);
    public int removeMetadataProperties();
    public void backupWizBin();
    public String[] getWizBinBackups();
}
