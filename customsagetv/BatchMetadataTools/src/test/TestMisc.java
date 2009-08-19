package test;

import org.jdna.media.metadata.SearchQueryFactory;




public class TestMisc {
    public static void main(String args[]) throws Throwable {
        SearchQueryFactory.getInstance().createMovieQuery("Marley *& Me");
    }
}
