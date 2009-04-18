package test;

import java.io.IOException;

import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.sage.media.SageMediaFile;
import org.jdna.sage.media.SageShowPeristence;
import org.jdna.util.LoggerConfiguration;

import sagex.SageAPI;
import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

public class TestSagePersistence {
    public static void main(String args[]) {
        LoggerConfiguration.configurePlugin();
        SageAPI.setProvider(SageAPI.getRemoteProvider());
        
        SageShowPeristence sp = new SageShowPeristence();
        SageMediaFile mf = new SageMediaFile("2812563");
        System.out.println("MediaTitle: " + mf.getTitle());
        
        Object sageMF = ((SageMediaFile)mf).getSageMediaFile();
        Object airing = MediaFileAPI.GetMediaFileAiring(sageMF);
        Object origShow  = AiringAPI.GetShow(airing);

        System.out.println("Misc: " + ShowAPI.GetShowMisc(origShow));

        MediaMetadata md = new MediaMetadata();
        md.set(MetadataKey.DISPLAY_TITLE, "Foo Far");
        md.set(MetadataKey.YEAR, "1992");
        md.addGenre("G1");
        md.addGenre("G2");
        md.set(MetadataKey.MEDIA_PROVIDER_DATA_ID, "pp:123459");

        CastMember cm = new CastMember(ICastMember.ACTOR);
        cm.setName("Billy Bob");
        cm.setPart("Watcher 1");
        md.addCastMember(cm);
        
        cm = new CastMember(ICastMember.ACTOR);
        cm.setName("Ronny Rae");
        cm.setPart("Watcher 2");
        md.addCastMember(cm);

        cm = new CastMember(ICastMember.DIRECTOR);
        cm.setName("Ditty Director");
        md.addCastMember(cm);

        cm = new CastMember(ICastMember.WRITER);
        cm.setName("Witty Writer");
        md.addCastMember(cm);
        
        System.out.println("Storing...");
        try {
            sp.storeMetaData(md, mf, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Storing Done...");
        
        System.out.println("Ratings: " + ShowAPI.GetShowExpandedRatings(origShow));
        System.out.println("Misc: " + ShowAPI.GetShowMisc(origShow));
        System.out.println("Done");
    }
}
