package test;

import java.util.List;

import sagex.SageAPI;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.api.metadata.ISageCastMember;
import sagex.api.metadata.ISageMetadataALL;
import sagex.api.metadata.SageMetadata;
import sagex.remote.rmi.RMISageAPI;
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
        
        
        SageAPI.setProvider(new RMISageAPI("seans-desktop",1098));
        System.out.println("OS: " + Global.GetOS());
        
        //Object mf = MediaFileAPI.GetMediaFileForID(131176);
        Object mf = MediaFileAPI.GetMediaFileForID(131179);
        
        System.out.println("Title: " + MediaFileAPI.GetMediaTitle(mf));
        
        ISageMetadataALL md = SageMetadata.create(mf, ISageMetadataALL.class);
        System.out.println("MD Title: " + md.getTitle());
        System.out.println("MD MediaTitle: " + md.getMediaTitle());
        System.out.println("MD FormatVideoCodec: " + md.getFormatVideoCodec());
        System.out.println("MD FormatVideoWidth: " + md.getFormatVideoWidth());
        System.out.println("MD MediType: " + md.getMediaType());
        System.out.println("MD Description: " + md.getDescription());
        System.out.println("MD External ID: " + md.getExternalID());
        List<ISageCastMember> list = md.getActors();
        for (ISageCastMember cm : list) {
            System.out.println("MD Actor: " + cm.getName());
        }
        
        
        System.out.println("SAGE Title: " + MediaFileAPI.GetMediaFileMetadata(mf, "Title"));
        System.out.println("SAGE Codec: " + MediaFileAPI.GetMediaFileMetadata(mf, "Format.Video.Codec"));
        //MediaFileAPI.SetMediaFileMetadata(mf, "Title", "Stupid Title");
        System.out.println("SAGE Title2: " + MediaFileAPI.GetMediaFileMetadata(mf, "Title"));
        //System.out.println("SAGE2 Title: " + SageTV.api("GetMediaFileMetadata", new Object[] {mf, "Title"}));
        
        
        //UIContext ctx = new UIContext("001d098ac46c");
        //SageAPI.setProvider(new SageAPIRemote("rmi://localhost:1098"));
        //System.out.println("CTX: " + Global.GetUIContextName());
        //System.out.println("CTX: " + Global.GetUIContextName(ctx));
        //System.out.println("CTX: " + SageTV.apiUI("001d098ac46c", "GetUIContextName", null));
    }
}
