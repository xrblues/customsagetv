package test.junit;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static test.junit.lib.FilesTestCase.makeFile;

import java.io.File;

import org.jdna.media.FileMediaFile;
import org.jdna.media.IMediaFile;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.sage.media.SageMediaFile;
import org.junit.Test;

import sagex.SageAPI;
import sagex.api.MediaFileAPI;
import sagex.stub.MediaFileAPIProxy;
import sagex.stub.StubSageAPI;


public class TestSearchQuery {

    @Test
    public void testCreateQueryIMediaResource() {
        IMediaFile mf = new FileMediaFile(new File("Finding.Nemo.DVDRip.avi"));
        SearchQuery q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, SearchQuery.Type.MOVIE, "Finding Nemo");
        
        mf = new FileMediaFile(new File("House S01E02.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, SearchQuery.Type.TV, "House", "01", "02");

        mf = new FileMediaFile(new File("House S1E2.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, SearchQuery.Type.TV, "House", "1", "2");

        mf = new FileMediaFile(new File("House-Doctors-00000000-0.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, SearchQuery.Type.TV, "House", null, null);
        
        mf = new FileMediaFile(new File("House-00000000-0.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertParts(q, SearchQuery.Type.TV, "House", null, null);
        
        // test if the year is parsed
        mf = new FileMediaFile(new File("1984 (2009).avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertEquals("2009", q.get(Field.YEAR));

        mf = new FileMediaFile(new File("Movies/2004/In the Deep.avi"));
        q = SearchQueryFactory.getInstance().createQuery(mf);
        assertEquals("2004", q.get(Field.YEAR));
    }
    
    @Test
    public void testCreateQuerySageMediaFile() {
        StubSageAPI provider = new StubSageAPI();
        SageAPI.setProvider(provider);
        Object mfObj = MediaFileAPI.AddMediaFile(makeFile("sage/TV/House S01E02.avi"), "TV");
        assertNotNull(mfObj);
        System.out.println(MediaFileAPIProxy.getMediaFile(mfObj).title);
        assertTrue("Not a TV File", MediaFileAPI.IsTVFile(mfObj));
        SearchQuery q = SearchQueryFactory.getInstance().createQuery(new SageMediaFile(mfObj));
        assertParts(q, SearchQuery.Type.TV, "House", "01", "02");


        /// TODO fix search query to work with SageMediaFile
        /// TODO run find bugs
        
        mfObj = MediaFileAPI.AddMediaFile(makeFile("sage/TV/Leverage-TheTopHatJob-4836543-0.ts"), "TV");
        assertTrue("Not A TV File", MediaFileAPI.IsTVFile(mfObj));
        q = SearchQueryFactory.getInstance().createQuery(new SageMediaFile(mfObj));
        assertParts(q, SearchQuery.Type.TV, "Leverage");
        assertEquals("The Top Hat Job", q.get(Field.EPISODE_TITLE));
    }
    
    private void assertParts(SearchQuery q, SearchQuery.Type type, String title) {
        assertEquals(type, q.getType());
        assertEquals(title, q.get(Field.TITLE));
    }

    private void assertParts(SearchQuery q, SearchQuery.Type type, String title, String season, String episode) {
        assertParts(q, type, title);
        assertEquals(season, q.get(Field.SEASON));
        assertEquals(episode, q.get(Field.EPISODE));
    }

}
