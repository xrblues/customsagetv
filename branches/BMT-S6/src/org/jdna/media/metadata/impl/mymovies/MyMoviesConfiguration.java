package org.jdna.media.metadata.impl.mymovies;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="MyMovies", path = "bmt/mymovies", description = "Configuration for MyMovies metadata provider")
public class MyMoviesConfiguration extends GroupProxy {
    @AField(label="Cache/Index Dir", description = "Directory where the MyMovies metadata will be indexed", editor="dirChooser")
    private FieldProxy<String> indexDir     = new FieldProxy<String>("cache/indexMyMovies/");

    @AField(label="MyMovies Xml", description = "MyMovies xml file", editor="fileChooser")
    private FieldProxy<String>  xmlFile = new FieldProxy<String>(null);

    @AField(label="MyMovies Xml last modified date/time", description = "Data/Time the xml file was modified as a long value.  Should not be set directly.")
    private FieldProxy<Long> xmlFileLastModified = new FieldProxy<Long>(0l);

    public MyMoviesConfiguration() {
        super();
        init();
    }

    public String getIndexDir() {
        return indexDir.getString();
    }

    public void setIndexDir(String indexDir) {
        this.indexDir.set(indexDir);
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
        this.xmlFileLastModified.set(xmlFileLastModified);
    }
}
