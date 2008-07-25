package test;

import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.SearchException;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;

public class TestIMDBProvider extends TestIMDBParser {
	public static void main(String args[]) {
		TestIMDBProvider p = new TestIMDBProvider();
		p.go(args[0]);
	}
	
	public void go(String arg) {
		IMDBMetaDataProvider finder = new IMDBMetaDataProvider();
		try {
			dumpResults(finder.search(IVideoMetaDataProvider.SEARCH_TITLE, arg));
		} catch (SearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
