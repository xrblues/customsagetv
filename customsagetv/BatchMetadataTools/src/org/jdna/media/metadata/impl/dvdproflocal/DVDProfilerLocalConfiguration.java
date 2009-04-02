package org.jdna.media.metadata.impl.dvdproflocal;

import java.io.File;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="DVD Profiler", name = "dvdprofilerLocal", requiresKey = false, description = "Configuration for the Local DVD Profiler provider (ie, uses local DVD Profiler, not Urls)")
public class DVDProfilerLocalConfiguration {
    @Field(label="Cache/Index Dir", description = "Directory where the local dvd profiler data will be indexed")
    private String  indexDir     = "cache/indexDVDProfLocal/";

    @Field(label="DVD Profiler Image Dir", description = "Local DVD Profiler image directory")
    private String  imageDir;

    @Field(label="DVD Profiler Xml", description = "Local DVD Profiler xml file")
    private String  xmlFile;

    @Field(label="DVD Profiler Xml last modified date/time", description = "Data/Time the xml file was modified as a long value.  Should not be set directly.")
    private long xmlFileLastModified;

    public DVDProfilerLocalConfiguration() {
    }

    public String getIndexDir() {
        return indexDir;
    }

    public void setIndexDir(String indexDir) {
        this.indexDir = indexDir;
    }

    public String getImageDir() {
        return imageDir;
    }

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    public String getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(String xmlFile) {
        File f = new File(xmlFile);
        if (!f.exists()) {
            throw new RuntimeException("DVD Profiler Xml does not exist: " + xmlFile);
        }
        
        this.xmlFile = xmlFile;
    }

    public long getXmlFileLastModified() {
        return xmlFileLastModified;
    }

    public void setXmlFileLastModified(long xmlFileLastModified) {
        this.xmlFileLastModified = xmlFileLastModified;
    }

}
