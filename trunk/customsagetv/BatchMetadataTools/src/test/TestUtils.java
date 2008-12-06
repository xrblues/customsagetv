package test;

import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaSearchResult;

public class TestUtils {
	public static void dumpResults(java.util.List<IMediaSearchResult> results) {
		System.out.println("Begin Dumping Results");
		for (IMediaSearchResult r : results) {
			dumpResult(r);
		}
		System.out.println("End Dumping Results");
	}

	public static void dumpResult(IMediaSearchResult r) {
		System.out.printf("ResultClass: %s\n", r.getClass().getName());
		System.out.printf("      Title: %s\n", r.getTitle());
		System.out.printf("       Year: %s\n", r.getYear());
		System.out.printf("      Match: %s\n------------------------------------\n", IMediaSearchResult.SEARCH_TYPE_NAMES[r.getResultType()]);
	}

	
	public static void dumpMetaData(IMediaMetadata md) {
		System.out.println("MetaData Begin");
		System.out.printf("     Title: %s\n", md.getTitle());
		System.out.printf("      Year: %s\n", md.getYear());
		System.out.printf("     Thumb: %s\n", md.getThumbnailUrl());
		System.out.printf("UserRating: %s\n", md.getUserRating());
		dumpCastMember(md.getDirectors());
		dumpCastMember(md.getWriters());
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
		dumpCastMember(md.getActors());
		System.out.println("MetaData End");
	}
	
	public static void dumpCastMember(ICastMember[] members) {
		for (ICastMember cm : members) {
			if (cm.getType()==ICastMember.ACTOR) {
				System.out.printf("Actor: %s as %s\n", cm.getName(), cm.getPart());
			} else if (cm.getType()==ICastMember.DIRECTOR) {
				System.out.printf("Director: %s\n", cm.getName());
			} else if (cm.getType()==ICastMember.WRITER) {
				System.out.printf("Writer: %s\n", cm.getName());
			} else {
				System.out.printf("Other: %s doing \n", cm.getName(), cm.getPart());
			}
		}
	}

}
