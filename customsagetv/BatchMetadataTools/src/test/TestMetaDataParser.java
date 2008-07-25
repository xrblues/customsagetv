package test;

import java.io.IOException;

import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IVideoMetaData;
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
			IVideoMetaData md = p.getMetatData();
			dumpMetaData(md);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void dumpMetaData(IVideoMetaData md) {
		System.out.println("MetaData Begin");
		System.out.printf("     Title: %s\n", md.getTitle());
		System.out.printf("      Year: %s\n", md.getYear());
		System.out.printf("     Thumb: %s\n", md.getThumbnailUrl());
		System.out.printf("UserRating: %s\n", md.getUserRating());
		for (ICastMember cm : md.getDirectors()) {
			System.out.printf("Director: %s (%s) (%s)\n", cm.getName(), cm.getType(), cm.getProviderDataUrl());
		}
		for (ICastMember cm : md.getWriters()) {
			System.out.printf(" Writer: %s (%s) (%s)\n", cm.getName(), cm.getType(), cm.getProviderDataUrl());
		}
		System.out.printf("ReleaseDate: %s\n", md.getReleaseDate());
		for (String s : md.getGenres()) {
			System.out.printf("Genre: %s\n", s);
		}
		System.out.printf("       Plot: %s\n", md.getPlot());
		System.out.printf("MPAA Rating: %s\n", md.getMPAARating());
		System.out.printf("    Runtime: %s\n", md.getRuntime());
		System.out.printf("Aspect Ratio: %s\n", md.getAspectRatio());
		System.out.printf("    Company: %s\n", md.getCompany());
		System.out.printf("Cast:\n");
		for (ICastMember cm : md.getActors()) {
			System.out.printf("Actor: %s as %s (%s) (%s)\n", cm.getName(), cm.getPart(), cm.getType(), cm.getProviderDataUrl());
		}
		System.out.println("MetaData End");
	}
}
