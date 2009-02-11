package org.jdna.media.metadata.impl.dvdproflocal;

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
        this.xmlFile = xmlFile;
    }

}
