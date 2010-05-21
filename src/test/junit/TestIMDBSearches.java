package test.junit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;
import test.junit.lib.InitBMT;


public class TestIMDBSearches {
    @BeforeClass
    public static void init() {
        try {
            InitBMT.initBMT();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testIMDBSearch1() {
        IMDBMetaDataProvider prov = new IMDBMetaDataProvider();
        SearchQuery query = new SearchQuery(MediaType.MOVIE, "2012");
        query.set(Field.YEAR, "2009");
        query.set(Field.QUERY, query.get(Field.RAW_TITLE));
        try {
            List<IMetadataSearchResult> results = prov.search(query);
            for (IMetadataSearchResult r : results) {
                System.out.println("result: " + r.getTitle() + "; " + r.getYear() + "; " + r.getScore());
            }
            
            IMetadataSearchResult res = MediaMetadataFactory.getInstance().getBestResultForQuery(results, query);
            assertNotNull("QueryFailed: " + query, res);
        } catch (Exception e) {
            e.printStackTrace();
            fail("IMDB Search Failed!");
        }
        
    }

    @Test
    public void testIMDBSearch2() {
        IMDBMetaDataProvider prov = new IMDBMetaDataProvider();
        SearchQuery query = new SearchQuery(MediaType.MOVIE, "G I Joe Rise Of The Cobra");
        query.set(Field.YEAR, "2009");
        query.set(Field.QUERY, query.get(Field.RAW_TITLE));
        try {
            List<IMetadataSearchResult> results = prov.search(query);
            for (IMetadataSearchResult r : results) {
                System.out.println("result: " + r.getTitle() + "; " + r.getYear() + "; " + r.getScore());
            }
            
            IMetadataSearchResult res = MediaMetadataFactory.getInstance().getBestResultForQuery(results, query);
            assertNotNull("QueryFailed: " + query, res);
        } catch (Exception e) {
            e.printStackTrace();
            fail("IMDB Search Failed!");
        }
    }

    @Test
    public void testIMDBSearch3() {
        IMDBMetaDataProvider prov = new IMDBMetaDataProvider();
        SearchQuery query = new SearchQuery(MediaType.MOVIE, "Sex and the city");
        query.set(Field.QUERY, query.get(Field.RAW_TITLE));
        try {
            List<IMetadataSearchResult> results = prov.search(query);
            for (IMetadataSearchResult r : results) {
                System.out.println("result: " + r.getTitle() + "; " + r.getYear() + "; " + r.getScore());
            }
            
            IMetadataSearchResult res = MediaMetadataFactory.getInstance().getBestResultForQuery(results, query);
            assertNotNull("QueryFailed: " + query, res);
        } catch (Exception e) {
            e.printStackTrace();
            fail("IMDB Search Failed!");
        }
    }

    @Test
    public void testIMDBSearch4() {
        IMDBMetaDataProvider prov = new IMDBMetaDataProvider();
        SearchQuery query = new SearchQuery(MediaType.MOVIE, "9");
        query.set(Field.YEAR,"2009");
        query.set(Field.QUERY, query.get(Field.RAW_TITLE));
        try {
            List<IMetadataSearchResult> results = prov.search(query);
            for (IMetadataSearchResult r : results) {
                System.out.println("result: " + r.getTitle() + "; " + r.getYear() + "; " + r.getScore());
            }
            
            IMetadataSearchResult res = MediaMetadataFactory.getInstance().getBestResultForQuery(results, query);
            assertNotNull("QueryFailed: " + query, res);
        } catch (Exception e) {
            e.printStackTrace();
            fail("IMDB Search Failed!");
        }
    }
}
