package org.jdna.media.util;

import org.jdna.media.metadata.SearchResultType;
import org.jdna.util.Similarity;

public class Scoring {
    private static  Scoring instance = new Scoring();
    public static Scoring getInstance() {
        return instance;
    }
    
    public SearchResultType getTypeForScore(float score) {
        if (score > 0.99) {
            return SearchResultType.EXACT;
        } else if (score > 0.9) {
            return SearchResultType.POPULAR;
        } else {
            return SearchResultType.PARTIAL;
        }
    }
    
    public SearchResultType getType(String s1, String s2) {
        return getTypeForScore((float) Similarity.getInstance().compareStrings(s1, s2));
    }
}
