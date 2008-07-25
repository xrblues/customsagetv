package test;

import java.io.IOException;

import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.impl.imdb.IMDBSearchResultParser;
import org.xml.sax.SAXException;


public class TestIMDBParser  {
	public static void main(String args[]) throws Exception {
		TestIMDBParser p = new TestIMDBParser();
		p.go(args[0]);
		System.out.println("\n\n**************** DOING IT AGAIN *******************\n\n");
		p.go(args[0]);
	}
	
	private void go(String arg) throws IOException, SAXException {
		IMDBSearchResultParser parser = new IMDBSearchResultParser(arg);
		parser.parse();
		dumpResults(parser.getResults());
	}
	
	protected void dumpResults(java.util.List<IVideoSearchResult> results) {
		System.out.println("Dumping Results");
		for (IVideoSearchResult r : results) {
			dump(r);
		}
	}

	protected void dump(IVideoSearchResult r) {
		System.out.printf("Title: %s\n", r.getTitle());
		System.out.printf(" Year: %s\n", r.getYear());
		System.out.printf("Match: %s\n------------------------------------\n", IVideoSearchResult.SEARCH_TYPE_NAMES[r.getResultType()]);
	}
}
