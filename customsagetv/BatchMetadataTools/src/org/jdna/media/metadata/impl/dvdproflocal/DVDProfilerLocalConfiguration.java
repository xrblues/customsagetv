package org.jdna.media.metadata.impl.dvdproflocal;

import org.jdna.configuration.Field;
import org.jdna.configuration.FieldProxy;
import org.jdna.configuration.Group;
import org.jdna.configuration.GroupProxy;

@Group(label="DVD Profiler", path = "bmt/dvdprofilerLocal", description = "Configuration for the Local DVD Profiler provider (ie, uses local DVD Profiler, not Urls)")
public class DVDProfilerLocalConfiguration extends GroupProxy {
    @Field(label="Cache/Index Dir", description = "Directory where the local dvd profiler data will be indexed")
    private FieldProxy<String>  indexDir     = new FieldProxy<String>("cache/indexDVDProfLocal/");

    @Field(label="DVD Profiler Image Dir", description = "Local DVD Profiler image directory")
    private FieldProxy<String>  imageDir = new FieldProxy<String>(null);

    @Field(label="DVD Profiler Xml", description = "Local DVD Profiler xml file")
    private FieldProxy<String>  xmlFile = new FieldProxy<String>(null);

    @Field(label="DVD Profiler Xml last modified date/time", description = "Data/Time the xml file was modified as a long value.  Should not be set directly.")
    private FieldProxy<Long> xmlFileLastModified = new FieldProxy<Long>(0l);

    public DVDProfilerLocalConfiguration() {
        super();
        init(this);
    }

    public String getIndexDir() {
        return indexDir.getString();
    }

    public void setIndexDir(String indexDir) {
        this.indexDir.set(indexDir);
    }

    public String getImageDir() {
        return imageDir.getString();
    }

    public void setImageDir(String imageDir) {
        this.imageDir.set(imageDir);
    }

    public String getXmlFile() {
        return xmlFile.getString();
    }

    public void setXmlFile(String xmlFile) {
        this.xmlFile.set(xmlFile);
    }

    public long getXmlFileLastModified() {
        return xmlFileLastModified.getLong();
    }

    public void setXmlFileLastModified(long xmlFileLastModified) {
        this.xmlFileLastModified .set(xmlFileLastModified);
    }
}