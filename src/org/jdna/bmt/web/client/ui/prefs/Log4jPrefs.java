package org.jdna.bmt.web.client.ui.prefs;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

public class Log4jPrefs implements Serializable {
    private Property<String> Level = new Property<String>("error");
    private Property<String> file = new Property<String>(null);
    private Property<String> pattern = new Property<String>(null);
    private Property<String> phoenixLevel = new Property<String>("warn");
    private Property<String> bmtLevel = new Property<String>("warn");
    
    public Log4jPrefs() {
    }

    public Property<String> getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level.set(level);
    }

    public Property<String> getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file.set(file);
    }

    public Property<String> getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern.set(pattern);
    }

    public Property<String> getPhoenixLevel() {
        return phoenixLevel;
    }

    public void setPhoenixLevel(String phoenixLevel) {
        this.phoenixLevel.set( phoenixLevel);
    }

    public Property<String> getBmtLevel() {
        return bmtLevel;
    }

    public void setBmtLevel(String bmtLevel) {
        this.bmtLevel.set(bmtLevel);
    }
}
