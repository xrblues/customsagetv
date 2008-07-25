package test;

import java.util.List;

import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.impl.dvdprof.DVDProfMetaDataProvider;
import org.jdna.metadataupdater.MetadataUpdater;

public class TestDVDProfiler {
	public static void main(String args[]) throws Exception {
		MetadataUpdater.initConfiguration();

		DVDProfMetaDataProvider prov = new DVDProfMetaDataProvider();
		List<IVideoSearchResult> results = prov.search(prov.SEARCH_TITLE, "Sharkboy");
		//List<IVideoSearchResult> results = prov.search(prov.SEARCH_TITLE, "Everyone's Hero");
		//List<IVideoSearchResult> results = prov.search(prov.SEARCH_TITLE, "8 Mile");
		for (IVideoSearchResult r : results) {
			System.out.println(r.toString());
		}
		
		if (results!=null && results.size()>0) {
			IVideoSearchResult res = results.get(0);
			IVideoMetaData md = res.getMetaData();
			System.out.printf("Title: %s\n", md.getTitle());
			System.out.printf("Year: %s\n", md.getYear());
			
			for (ICastMember cm : md.getDirectors()) {
				System.out.printf("Director: %s\n", cm.getName());
			}
			
			for (ICastMember cm : md.getWriters()) {
				System.out.printf("Writer: %s\n", cm.getName());
			}
			
			System.out.printf("Rating: %s\n", md.getMPAARating());
			System.out.printf("Release: %s\n", md.getReleaseDate());
			System.out.printf("Run Time: %s\n", md.getRuntime());
			System.out.printf("Aspect Ratio: %s\n", md.getAspectRatio());
			System.out.printf("Thumbnail: %s\n", md.getThumbnailUrl());
			System.out.printf("Plot: %s\n", md.getPlot());
			System.out.printf("Studio: %s\n", md.getCompany());
			
			for (String s : md.getGenres()) {
				System.out.printf("Genres: %s\n", s);
			}
			
			for (ICastMember cm : md.getActors()) {
				System.out.printf("Actor: %s -- %s\n", cm.getName(), cm.getPart());
			}
		}
		
	}
}
