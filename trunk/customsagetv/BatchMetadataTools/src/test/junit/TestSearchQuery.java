package test.junit;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static test.junit.lib.FilesTestCase.makeFile;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Field;
import org.junit.BeforeClass;
import org.junit.Test;

import sagex.SageAPI;
import sagex.api.MediaFileAPI;
import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.impl.FileResourceFactory;
import sagex.stub.MediaFileAPIProxy;
import sagex.stub.StubSageAPI;
import test.junit.lib.InitBMT;


public class TestSearchQuery {
    @BeforeClass
    public static void setup() throws Exception {
            InitBMT.initBMT();
    }
    
    @Test
    public void testCommonQuerries() {
        IMediaFile mf = (IMediaFile) FileResourceFactory.createResource(new File("G.I.Joe.Rice.Of.The.Cobra.2009.avi"));
        SearchQuery q = SearchQueryFactory.getInstance().createQuery(mf);
        System.out.println("RAW_TITLE: " + q.get(Field.RAW_TITLE));
        System.out.println("CLEAN_TITLE: " + q.get(Field.CLEAN_TITLE));
        
        mf = (IMediaFile) FileResourceFactory.createResource(new File("Sex in the city - cd1.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        System.out.println("RAW_TITLE: " + q.get(Field.RAW_TITLE));
        System.out.println("CLEAN_TITLE: " + q.get(Field.CLEAN_TITLE));
        assertEquals("Clean title is not matched!", q.get(Field.CLEAN_TITLE), "Sex in the city");
        
    }

    @Test
    public void testCreateQueryIMediaResource() {
        IMediaFile mf = (IMediaFile) FileResourceFactory.createResource(new File("Finding.Nemo.DVDRip.avi"));
        SearchQuery q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, MediaType.MOVIE, "Finding Nemo");
        
        mf = (IMediaFile) FileResourceFactory.createResource(new File("House S01E02.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, MediaType.TV, "House", "01", "02");

        mf = (IMediaFile) FileResourceFactory.createResource(new File("House S1E2.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, MediaType.TV, "House", "1", "2");

        mf = (IMediaFile) FileResourceFactory.createResource(new File("House-Doctors-00000000-0.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, MediaType.TV, "House", null, null);
        
        mf = (IMediaFile) FileResourceFactory.createResource(new File("House-00000000-0.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, MediaType.TV, "House", null, null);
        
        // test if the year is parsed
        mf = (IMediaFile) FileResourceFactory.createResource(new File("1984 (2009).avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertEquals("2009", q.get(Field.YEAR));
        
        // test for battlestar galactica
        mf = (IMediaFile) FileResourceFactory.createResource(new File("Battlestar Galactica-e01s01-33.mkv"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, MediaType.TV, "Battlestar Galactica", "01", "01");

        mf = (IMediaFile) FileResourceFactory.createResource(new File("The Prisoner-s01e01-Arrival.mkv"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, MediaType.TV, "The Prisoner", "01", "01");
        
        mf = (IMediaFile) FileResourceFactory.createResource(new File("\\DVDs\\Entourage Season 2 Disc 2\\VIDEO_TS"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, MediaType.TV, "Entourage", "2", "2");
    }
    
    @Test
    public void testCreateQuerySageMediaFile() {
        StubSageAPI provider = new StubSageAPI();
        SageAPI.setProvider(provider);
        Object mfObj = MediaFileAPI.AddMediaFile(makeFile("sage/TV/House S01E02.avi"), "TV");
        assertNotNull(mfObj);
        System.out.println(MediaFileAPIProxy.getMediaFile(mfObj).title);
        assertTrue("Not a TV File", MediaFileAPI.IsTVFile(mfObj));
        SearchQuery q = SearchQueryFactory.getInstance().createQuery(phoenix.api.GetMediaFile(mfObj));
        assertParts(q, MediaType.TV, "House", "01", "02");


        /// TODO fix search query to work with SageMediaFile
        /// TODO run find bugs
        
        mfObj = MediaFileAPI.AddMediaFile(makeFile("sage/TV/Leverage-TheTopHatJob-4836543-0.ts"), "TV");
        assertTrue("Not A TV File", MediaFileAPI.IsTVFile(mfObj));
        q = SearchQueryFactory.getInstance().createQuery(phoenix.api.GetMediaFile(mfObj));
        assertParts(q, MediaType.TV, "Leverage");
        assertEquals("The Top Hat Job", q.get(Field.EPISODE_TITLE));

    
        mfObj = MediaFileAPI.AddMediaFile(makeFile("sage/TV/House-s2Ep3-4836543-0.ts"), "TV");
        assertTrue("Not A TV File", MediaFileAPI.IsTVFile(mfObj));
        q = SearchQueryFactory.getInstance().createQuery(phoenix.api.GetMediaFile(mfObj));
        assertParts(q, MediaType.TV, "House", "2","3");
    }
    
    private void assertParts(SearchQuery q, MediaType type, String title) {
        assertEquals(type, q.getMediaType());
        assertEquals(title, q.get(Field.CLEAN_TITLE));
    }

    private void assertParts(SearchQuery q, MediaType type, String title, String season, String episode) {
        assertParts(q, type, title);
        assertEquals(season, q.get(Field.SEASON));
        if (!StringUtils.isEmpty(q.get(Field.DISC))) {
            assertEquals(episode, q.get(Field.DISC));
        } else {
            assertEquals(episode, q.get(Field.EPISODE));
        }
    }

}
