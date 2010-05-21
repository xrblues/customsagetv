package org.jdna.bmt.web.client.ui.app;



import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("sage")
public interface SageService extends RemoteService {
    public String refreshLibrary(boolean fullScan);
    public ConnectionInfo getSagexApiConnectionInfo();
    public ConnectionInfo[] getSagexApiConnections();
    public void setSagexApiConnection(String server);
}
