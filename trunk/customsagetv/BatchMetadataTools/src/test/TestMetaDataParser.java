package test;

import java.io.IOException;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.impl.imdb.IMDBMovieMetaDataParser;
import org.xml.sax.SAXException;

public class TestMetaDataParser extends TestIMDBParser {

	public static void main(String args[]) {
		TestMetaDataParser p = new TestMetaDataParser();
		p.go(args[0]);
	}
	
	public void go(String arg) {
		IMDBMovieMetaDataParser p =new IMDBMovieMetaDataParser(arg);
		try {
			p.parse();
			IMediaMetadata md = p.getMetatData();
			TestUtils.dumpMetaData(md);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
