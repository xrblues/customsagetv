package org.jdna.bmt.web.client.ui.prefs;

import java.io.Serializable;

public class RegexValidation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String regex;
    private String sampleData;
    private String results;
    private boolean valid;
    
    public RegexValidation() {
    }

    public RegexValidation(String regex, String sampleData, String results) {
        setRegex(regex);
        setSampleData(sampleData);
        setResults(results);
    }
    
    /**
     * @return the regex
     */
    public String getRegex() {
        return regex;
    }
    /**
     * @param regex the regex to set
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }
    /**
     * @return the sampleData
     */
    public String getSampleData() {
        return sampleData;
    }
    /**
     * @param sampleData the sampleData to set
     */
    public void setSampleData(String sampleData) {
        this.sampleData = sampleData;
    }
    /**
     * @return the results
     */
    public String getResults() {
        return results;
    }
    /**
     * @param results the results to set
     */
    public void setResults(String results) {
        this.results = results;
    }
    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }
    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
