package org.jdna.media.metadata;

import java.io.File;
import java.util.regex.Pattern;

import sagex.phoenix.fanart.MediaType;

public class FileMatcher {
    private MediaType mediaType = MediaType.MUSIC;
    private File file;
    private String title, year;
    private Pattern fileRegex;
    private ID metadata;
    private ID fanart;
    
    public FileMatcher() {
    }
    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }
    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }
    /**
     * @return the fileregex
     */
    public Pattern getFileRegex() {
        return fileRegex;
    }
    /**
     * @param fileregex the fileregex to set
     */
    public void setFileRegex(Pattern fileregex) {
        this.fileRegex = fileregex;
    }
    
    public void setFileRegex(String regex) {
        this.fileRegex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
    /**
     * @return the metadata
     */
    public ID getMetadata() {
        return metadata;
    }
    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(ID metadata) {
        this.metadata = metadata;
    }
    /**
     * @return the fanart
     */
    public ID getFanart() {
        return fanart;
    }
    /**
     * @param fanart the fanart to set
     */
    public void setFanart(ID fanart) {
        this.fanart = fanart;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the mediaType
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * @param mediaType the mediaType to set
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
}
