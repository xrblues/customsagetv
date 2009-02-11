package test;

import java.io.File;

import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.util.FileNameUtils;

public class TestFilenameScrpaers {
    public static void main(String[] args) {
        String filename = "smb://mediaserver/fileserver/Media/Videos/VideoCollection/TV/30%20Rock/Season%202/30.Rock.S02E09%20-%20Episode%20209.avi";
        FileNameUtils futils = new FileNameUtils(new File("scrapers/xbmc/tvfilenames/"));
        SearchQuery q = futils.createSearchQuery(filename);
        System.out.println("Query: " + q);

        filename = "/30.Rock.S02E09%20-%20Episode%20209.avi";
        q = futils.createSearchQuery(filename);
        System.out.println("Query: " + q);
    }
}
