package org.jdna.media.metadata.impl.dvdproflocal;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="DVD Profiler", path = "bmt/dvdprofilerLocal", description = "Configuration for the Local DVD Profiler provider (ie, uses local DVD Profiler, not Urls)")
public class DVDProfilerLocalConfiguration extends GroupProxy {
    @AField(label="Cache/Index Dir", description = "Directory where the local dvd profiler data will be indexed", editor="dirChooser")
    private FieldProxy<String>  indexDir     = new FieldProxy<String>("cache/indexDVDProfLocal/");

    @AField(label="DVD Profiler Image Dir", description = "Local DVD Profiler image directory", editor="dirChooser")
    private FieldProxy<String>  imageDir = new FieldProxy<String>(null);

    @AField(label="DVD Profiler Xml", description = "Local DVD Profiler xml file", editor="fileChooser")
    private FieldProxy<String>  xmlFile = new FieldProxy<String>(null);

    @AField(label="DVD Profiler Xml last modified date/time", description = "Data/Time the xml file was modified as a long value.  Should not be set directly.")
    private FieldProxy<Long> xmlFileLastModified = new FieldProxy<Long>(0l);

    public DVDProfilerLocalConfiguration() {
        super();
        init();
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