package org.jdna.media.metadata.impl.dvdproflocal;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(name = "dvdprofilerLocal", requiresKey = false, description = "Configuration for the Local DVD Profiler provider (ie, uses local DVD Profiler, not Urls)")
public class DVDProfilerLocalConfiguration {
    @Field(description = "Set this to true to force the local dvd profiler index to be rebuilt")
    private boolean forceRebuild = false;

    @Field(description = "Directory where the local dvd profiler data will be indexed")
    private String  indexDir     = "cache/indexDVDProfLocal/";

    @Field(description = "Local DVD Profiler image directory")
    private String  imageDir;

    @Field(description = "Local DVD Profiler xml file")
    private String  xmlFile;

    public DVDProfilerLocalConfiguration() {
    }

    public boolean isForceRebuild() {
        return forceRebuild;
    }

    public void setForceRebuild(boolean forceRebuild) {
        this.forceRebuild = forceRebuild;
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
