package test;

import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaResource;
import org.jdna.media.util.BackgroundMetadataUpdater;
import org.jdna.metadataupdater.MetadataUpdater;
import org.jdna.sage.media.SageMediaFolder;
import org.jdna.util.LoggerConfiguration;

import bmt.BMTMetadataSupport;

import sagex.SageAPI;
import sagex.api.Database;
import sagex.api.MediaFileAPI;

public class TestSageMediaFile {
    public static void main(String[] args) throws Exception {
        LoggerConfiguration.configurePlugin();
        MetadataUpdater.initConfiguration();
        SageAPI.setProvider(SageAPI.getRemoteProvider());
        
        Object media[] = Database.SearchByText("willy", "V");
        if (media==null) {
            System.out.println("Nothing Found");
        } else {
            System.out.println("Lising results...");
            for (Object o : media) {
                System.out.println(MediaFileAPI.GetMediaTitle(o));
                System.out.println("Movie: " + MediaFileAPI.IsVideoFile(o));
            }
            
            SageMediaFolder folder = new SageMediaFolder(media);
            System.out.println("SageFolder: " + folder.getName());
            for (IMediaResource r : folder.members()) {
                System.out.println("Sage Content Type: " + r.getContentType());
                System.out.println("Sage Title: " + r.getTitle());
            }
            System.out.println("Done");
            
            ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setCentralFanartFolder("testing/Fanart");
            BMTMetadataSupport bmt = new BMTMetadataSupport();
            bmt.startMetadataScan(null, media);
           
            System.out.println("Scan Started");
            while (BackgroundMetadataUpdater.isRunning()) {
                System.out.println("Scanning Progress: " + BackgroundMetadataUpdater.getCompleted());
                Thread.currentThread().sleep(1000);
            }
            System.out.println("Scan Complete");
        }
    }
}
