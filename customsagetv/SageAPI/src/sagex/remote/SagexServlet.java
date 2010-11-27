package sagex.remote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.plugin.impl.SagexConfiguration;
import sagex.remote.api.ApiHandler;
import sagex.remote.javarpc.JavaRPCHandler;
import sagex.remote.jsonrpc.JsonRPCHandler;
import sagex.remote.media.MediaHandler;
import sagex.remote.rmi.SageRMIServer;
import sagex.remote.xmlrpc.XMLRPCHandler;
import sagex.remote.xmlxbmc.XMLXBMCHandler;
import sagex.util.ILog;
import sagex.util.LogProvider;

public class SagexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public interface SageHandler {
        public void handleRequest(String args[], HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
    }

    private static ILog                     log          = LogProvider.getLogger(SagexServlet.class);
    private static boolean                  initialized  = false;
    private static Map<String, SageHandler> sageHandlers = new HashMap<String, SageHandler>();

    public SagexServlet() {
        log.info("Sage Remote API Servlet for Jetty is loaded.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Handling remote request: " + req.getPathInfo());
        try {
            // /command/arg1/arg2/.../
            // 0 -
            // 1 - command
            // 2 - arg1
            String args[] = req.getPathInfo().split("/");
            if (args == null || args.length < 2) {
                resp.sendError(404, "No Sage Handler Specified.");
                return;
            }

            SageHandler sh = sageHandlers.get(args[1]);
            if (sh == null) {
                resp.sendError(404, "Sage Handle: " + args[1] + " not found!");
                return;
            }
            sh.handleRequest(args, req, resp);
        } catch (Throwable t) {
            log.warn("Failed to process Sage Handler!", t);
            resp.sendError(500, "Sage Servlet Failed: " + t.getMessage());
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (!initialized) {
            initServices(config.getClass().getName());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * Initialize the Sage Remote Service Handlers
     * 
     * @param serverType
     *            - String containing 'jetty' for jetty, or null, for nielm
     */
    public static void initServices(String serverType) {
        initialized = true;

        log.info("Remote API Servlet initializing.");
        SagexConfiguration config = new SagexConfiguration();
        if (config.getBoolean(SagexConfiguration.PROP_ENABLE_HTTP, true)) {
            // register our known handlers
            sageHandlers.put(MediaHandler.SERVLET_PATH, new MediaHandler());
            
            // This API handler handles json, nielm, and xml
            sageHandlers.put(ApiHandler.SAGE_RPC_PATH, new ApiHandler());

            log.info("Registered Handlers.");

            // check if the RMI server is running... it may already be running
            if (config.getBoolean(SagexConfiguration.PROP_ENABLE_RMI, true) && !SageRMIServer.getInstance().isRunning()) {
                SageRMIServer.getInstance().startServer();
            }
        } else {
            log.info("Sagex Rest Services are disabled.");
        }
    }
}
