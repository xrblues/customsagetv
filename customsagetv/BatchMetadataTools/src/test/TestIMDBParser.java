package test;

import java.io.IOException;

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
		TestUtils.dumpResults(parser.getResults());
	}
	
}
