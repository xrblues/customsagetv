package test.junit;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.jdna.media.FileMediaFile;
import org.jdna.media.IMediaFile;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Field;
import org.junit.Test;

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
