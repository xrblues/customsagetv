package org.jdna.util;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * String Similarity taken from: http://www.catalysoft.com/articles/StrikeAMatch.html
 * 
 * @author seans
 *
 */
public class Similarity {
    private static Similarity instance = new Similarity();
    private static final Logger  log               = Logger.getLogger(Similarity.class);
    
    /** @return an array of adjacent letter pairs contained in the input string */
    private String[] letterPairs(String str) {

        int numPairs = str.length()-1;
        //address an issue where str is ""
        if(numPairs < 0)
        	numPairs = 0;
        
        String[] pairs = new String[numPairs];

        for (int i=0; i<numPairs; i++) {

            pairs[i] = str.substring(i,i+2);

        }

        return pairs;
    }

    /** @return an ArrayList of 2-character Strings. */
    private ArrayList wordLetterPairs(String str) {

        ArrayList allPairs = new ArrayList();

        // Tokenize the string and put the tokens/words into an array

        String[] words = str.split("\\s");

        // For each word

        for (int w=0; w < words.length; w++) {

            // Find the pairs of characters

            String[] pairsInWord = letterPairs(words[w]);

            for (int p=0; p < pairsInWord.length; p++) {

                allPairs.add(pairsInWord[p]);

            }

        }

        return allPairs;
    }
    
    /** @return lexical similarity value in the range [0,1] */
    public double compareStrings(String str1, String str2) {
    	try {
	        ArrayList pairs1 = wordLetterPairs(str1.toUpperCase());
	        ArrayList pairs2 = wordLetterPairs(str2.toUpperCase());
	
	        int intersection = 0;
	
	        int union = pairs1.size() + pairs2.size();
	
	        for (int i=0; i<pairs1.size(); i++) {
	
	            Object pair1=pairs1.get(i);
	
	            for(int j=0; j<pairs2.size(); j++) {
	
	                Object pair2=pairs2.get(j);
	
	                if (pair1.equals(pair2)) {
	
	                    intersection++;
	
	                    pairs2.remove(j);
	
	                    break;
	
	                }
	
	            }
	
	        }
	
	        return (2.0*intersection)/union;
    	} catch (Exception e){
    		log.debug("Exception in compareStrings str1 = " + str1 + " str12 = " + str2);
    		return 0.0;
    	}
    }

    public static Similarity getInstance() {
        return instance;
    }
}
