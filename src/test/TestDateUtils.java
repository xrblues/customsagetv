package test;

import org.apache.log4j.BasicConfigurator;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.MetadataUtil;

public class TestDateUtils {
    public static void main(String args[]) {
        BasicConfigurator.configure();
        MediaMetadata md = new MediaMetadata();
        MetadataUtil.setReleaseDateFromFormattedDate(md, "1999-11-21", "yyyy-MM-dd");
        System.out.println("Date: " + md.get(MetadataKey.RELEASE_DATE));
    }
}
