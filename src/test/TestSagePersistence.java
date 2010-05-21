package test;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaMetadataPersistence;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.PersistenceOptions;

import sagex.api.AiringAPI;
import sagex.api.ChannelAPI;
import sagex.api.Database;
import sagex.api.MediaFileAPI;
import sagex.phoenix.vfs.sage.SageMediaFile;
import sagex.phoenix.vfs.util.PathUtils;

public class TestSagePersistence {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        
        // heroes tv show
        MediaMetadataPersistence p = new MediaMetadataPersistence();
        Object sagemf = MediaFileAPI.GetMediaFileForID(376856);
        Object air = MediaFileAPI.GetMediaFileAiring(sagemf);
        Object chan = AiringAPI.GetChannel(air);
        System.out.println("Current Channel: " + ChannelAPI.GetStationID(chan));
        
        SageMediaFile mf = new SageMediaFile(null, sagemf);
        System.out.println("Show: " + mf.getTitle());
        System.out.println("File" + PathUtils.getFirstFile(mf) );
        IMediaMetadata md = p.loadMetaData(mf);
        PersistenceOptions opts= new PersistenceOptions();
        
        opts.setImportAsTV(true);
        opts.setCreateDefaultSTVThumbnail(false);
        opts.setCreateProperties(false);
        opts.setOverwriteFanart(false);
        opts.setTouchingFiles(false);
        opts.setUpdateWizBin(true);
        opts.setUseTitleMasks(true);
        
        MetadataAPI.normalizeMetadata(mf, md, opts);
        System.out.println(MetadataAPI.getMediaTitle(md));
        System.out.println(MetadataAPI.getDisplayTitle(md));
        
        Object channel = findChannel(MetadataAPI.getMediaTitle(md));
        if (channel == null || ChannelAPI.GetStationID(channel)==0) {
            System.out.println("No Channel for: " + MetadataAPI.getMediaTitle(md));
        } else {
            System.out.println("Channel Name: " + ChannelAPI.GetChannelName(channel));
            System.out.println("Channel Description: " + ChannelAPI.GetChannelDescription(channel));
            System.out.println("Channel Id: " + ChannelAPI.GetChannelNumber(channel));
            System.out.println("Channel Id: " + ChannelAPI.GetStationID(channel));
        }
        
        try {
            p.storeMetaData(md, mf, opts);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                
        
    }
    
    
    private static Object findChannel(String getShowTitle) {
        Object airings[] = Database.SearchByTitle(getShowTitle, "T");
        if (airings != null && airings.length>0) {
            for (int i=0;i<airings.length;i++) {
                Object chan = AiringAPI.GetChannel(airings[i]);
                if (chan!=null && ChannelAPI.GetStationID(chan)>0) {
                    System.out.println("AiringTitle: " + AiringAPI.GetAiringTitle(airings[i]));
                    System.out.println("AiringDet: " + AiringAPI.GetExtraAiringDetails(airings[i]));
                    return chan;
                }
            }
        }
        return null;
    }

}
