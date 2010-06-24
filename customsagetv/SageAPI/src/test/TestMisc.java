package test;

import java.util.Arrays;
import java.util.List;

import sagex.api.Database;

public class TestMisc {
	public static class Episode {
		public long getDateRecorded() {
			return 0;
		}
	}
	
    public static List<Episode> sortByDateRecorded(List<Episode> episodes, boolean descending) {
        Episode Results[] = (Episode[])Database.SortLexical(episodes, descending, "tmiranda_mob_Episode_getDateRecorded");

        if (Results==null) {
            System.out.println("sortByDateRecorded: null Results.");
            return null;
        }

        return Arrays.asList((Episode[])Results);
    }
}
