package org.jdna.bmt.web.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.ui.app.ConnectionInfo;
import org.jdna.bmt.web.client.ui.app.SageService;

import sagex.SageAPI;
import sagex.api.Configuration;
import sagex.api.Global;
import sagex.remote.rmi.RMISageAPI;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SageServicesImpl extends RemoteServiceServlet implements SageService {
    private static final Logger log  = Logger.getLogger(SageServicesImpl.class);
    
    public SageServicesImpl() {
        ServicesInit.init();
    }

    public String refreshLibrary(boolean fullScan) {
        if (Global.IsDoingLibraryImportScan()) {
            return "A scan is already in progress";
        }
        
        String msg = null;
        log.debug("Running library import scan; FullScan " + fullScan);
        if (fullScan) {
            Configuration.SetServerProperty("force_full_content_reindex", "true");
            msg = "A Full reindex has been started.  The full scan could take awhile depending on the size of your collection";
        } else {
            msg = "Sage is looking for new media";
        }
        Global.RunLibraryImportScan(false);
        log.debug(msg);
        return msg;
    }

    public ConnectionInfo getSagexApiConnectionInfo() {
        if (SageAPI.isRemote()) {
            return createConnectionInfo(SageAPI.getProviderProperties());
        } else {
            return new ConnectionInfo("LOCAL",0);
        }
    }
    
    private ConnectionInfo createConnectionInfo(Properties props) {
        String server = props.getProperty(SageAPI.PROP_REMOTE_SERVER);
        String port = props.getProperty(SageAPI.PROP_REMOTE_RMI_PORT);
        if (StringUtils.isEmpty(port)) {
            port = props.getProperty(SageAPI.PROP_REMOTE_HTTP_PORT);
        }
        return new ConnectionInfo(server, NumberUtils.toInt(port));
    }

    public ConnectionInfo[] getSagexApiConnections() {
        List<ConnectionInfo> all = new ArrayList<ConnectionInfo>();
        for (Properties p: SageAPI.getKnownRemoteAPIProviders()) {
            all.add(createConnectionInfo(p));
        }
        return all.toArray(new ConnectionInfo[] {});
    }

    public void setSagexApiConnection(String server) {
        Properties props = null;
        
        for (Properties p: SageAPI.getKnownRemoteAPIProviders()) {
            if (server.equals(p.getProperty(SageAPI.PROP_REMOTE_SERVER))) {
                props=p;
                break;
            }
        }
        
        if (props==null) {
            throw new RuntimeException("Server not found: " + server);
        }
        
        log.info("Set new sage api remote provider");
        SageAPI.setProviderProperties(props);
        SageAPI.setProvider(new RMISageAPI(props.getProperty(SageAPI.PROP_REMOTE_SERVER), NumberUtils.toInt(props.getProperty(SageAPI.PROP_REMOTE_RMI_PORT))));
    }
}
