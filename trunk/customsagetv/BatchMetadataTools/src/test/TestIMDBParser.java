package test;

import java.io.IOException;

import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.impl.imdb.IMDBSearchResultParser;
import org.xml.sax.SAXException;

import sagex.phoenix.fanart.MediaType;

public class TestIMDBParser {
    public static void main(String args[]) throws Exception {
        TestIMDBParser p = new TestIMDBParser();
        p.go(args[0], args[1]);
        System.out.println("\n\n**************** DOING IT AGAIN *******************\n\n");
        p.go(args[0], args[1]);
    }

    private void go(String url, String searchTitle) throws IOException, SAXException {
        SearchQuery query = new SearchQuery();
        query.setMediaType(MediaType.MOVIE);
        IMDBSearchResultParser parser = new IMDBSearchResultParser(query, url, searchTitle);
        parser.parse();
        TestUtils.dumpResults(parser.getResults());
    }

}
