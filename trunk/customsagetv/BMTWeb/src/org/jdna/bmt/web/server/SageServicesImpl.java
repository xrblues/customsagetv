package org.jdna.bmt.web.server;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.ui.app.SageService;

import sagex.api.Configuration;
import sagex.api.Global;

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
}
