package org.jdna.bmt.web.client.ui.status;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("status")
public interface StatusServices extends RemoteService {
    public List<StatusValue> getStatusInfo(String base);
    public List<SystemMessage> getSystemMessages();
    public void clearSystemMessages();
    public void deleteSystemMessage(int id);
    public String getBMTVersion();
    
}
