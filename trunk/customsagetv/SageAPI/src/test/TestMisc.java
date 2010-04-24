package test;

import sagex.api.Global;
import sagex.util.LogProvider;

public class TestMisc {
    public static void main(String args[]) throws Exception {
        LogProvider.useSystemOut();
        //System.out.println("Discovering Servers...");
        //SageAPI.discoverRemoteServers(5000);
        //Thread.sleep(1000);
        //for (Properties p: SageAPI.getKnownRemoteAPIProviders()) {
        //    System.out.println("Server: " + p.getProperty("server"));
        //}
        
        System.out.println("OS: " + Global.GetOS());
        
        //UIContext ctx = new UIContext("001d098ac46c");
        //SageAPI.setProvider(new SageAPIRemote("rmi://localhost:1098"));
        //System.out.println("CTX: " + Global.GetUIContextName());
        //System.out.println("CTX: " + Global.GetUIContextName(ctx));
        //System.out.println("CTX: " + SageTV.apiUI("001d098ac46c", "GetUIContextName", null));
    }
}
