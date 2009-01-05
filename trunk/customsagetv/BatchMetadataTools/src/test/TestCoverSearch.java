package test;

import java.util.List;

import org.jdna.media.metadata.ICoverResult;

public class TestCoverSearch {
    public static void main(String args[]) throws Exception {
        System.out.println("Looking for covers....");
        // DefaultCoversProvider p = new DefaultCoversProvider();
        // List<ICoverResult> results = p.findCovers(ICoverProvider.COVER_MOVIE,
        // "sonic");
        // dumpResults(results);
        System.out.println("Done Looking for covers.");
    }

    // decided to do it in javascript, so that we could use google

    public static void dumpResults(List<ICoverResult> results) {
        for (ICoverResult c : results) {
            System.out.printf(" IconUrl: %s", c.getIconUrl());
            System.out.printf("ImageUrl: %s", c.getImageUrl());
            System.out.printf("    Info: %s", c.getImageInfo());
            System.out.printf("   Score: %f", c.getScore());
            System.out.println("");
        }
    }
}
