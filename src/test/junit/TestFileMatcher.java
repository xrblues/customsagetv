package test.junit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.io.File;

import org.jdna.media.metadata.FileMatcher;
import org.jdna.media.metadata.FileMatcherManager;
import org.junit.BeforeClass;
import org.junit.Test;

import sagex.phoenix.fanart.MediaType;
import test.junit.lib.InitBMT;


public class TestFileMatcher {
    @BeforeClass
    public static void init() throws Exception {
        InitBMT.initBMT();
    }
    
    @Test
    public void testParser() {
        FileMatcherManager mgr = new FileMatcherManager(new File("src/test/junit/MediaTitles.xml"));
        //assertEquals(4, mgr.getFileMatchers().size());
        
        // should find this by name
        String file = "/home/movies/shared/FindingNemo.avi";
        FileMatcher match = mgr.getMatcher(file);
        assertNotNull(match);
        assertEquals("Finding Nemo", match.getTitle());
        assertEquals("2003", match.getYear());
        assertEquals("imdb", match.getMetadata().getName());
        assertEquals("tt0266543", match.getMetadata().getValue());
        assertEquals(MediaType.MOVIE, match.getMediaType());
        //assertEquals("themoviedb.org", match.getFanart().getName());
        //assertEquals("12", match.getFanart().getValue());
        
        // should find this by regex
        file = "/home/TV/Babylon 5/Season 1/show.avi";
        match = mgr.getMatcher(file);
        assertNotNull(match);
        assertEquals("Babylon 5", match.getTitle());
        assertEquals("1993", match.getYear());
        assertEquals("tvdb", match.getMetadata().getName());
        assertEquals("7072", match.getMetadata().getValue());
        assertEquals(MediaType.TV, match.getMediaType());
        assertNull(match.getFanart());
        
        // test someother regex ones...
        assertNull(mgr.getMatcher("/foo/"));
        assertNotNull(mgr.getMatcher("/home/TV/Babylon5/Season 1/show.avi"));
        assertNotNull(mgr.getMatcher("/home/TV/babylon5/Season 1/show.avi"));
        assertNotNull(mgr.getMatcher("/home/TV/babylon   5/Season 1/show.avi"));

        match = mgr.getMatcher("e:\\Television\\Battlestar Galactica\\Battlestar Galactica-e01s01-33.mkv");
        assertNotNull(match);
        assertEquals("tvdb", match.getMetadata().getName());
        assertEquals("73545", match.getMetadata().getValue());

        assertNotNull(mgr.getMatcher("e:\\Television\\Battlestar Galactica\\Battlestar Galactica-e01s01-33.mkv"));
        assertNotNull(mgr.getMatcher("e:\\Television\\The Prisoner\\The Prisoner-e01s01-33.mkv"));
    }
}
