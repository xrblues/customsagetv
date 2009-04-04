package test;

import org.jdna.util.Similarity;

public class TestSimilarity {
    public static void main(String args[]) {
        similar("dexter", "Dexter");
        similar("dexter", "Dexters");
        similar("dexter", "Dexter's Lab");
        similar("dexter", "A Dexter Tale");
        similar("dexter", "dexter 2009");
        similar("dexter", "dexter 11");
        similar("dexter 1", "dexter");
        similar("Batman Begins", "Batman Begins 2");
    }

    private static void similar(String s1, String s2) {
        System.out.println("   S1: " + s1);
        System.out.println("   S2: " + s2);
        System.out.println("Score: " + Similarity.getInstance().compareStrings(s1, s2) + "\n");
        System.out.println("Float: " + (float)Similarity.getInstance().compareStrings(s1, s2) + "\n");
    }
}
