package org.jdna.media.metadata.impl.mymovies;

import java.io.File;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="MyMovies", name = "mymovies", requiresKey = false, description = "Configuration for MyMovies metadata provider")
public class MyMoviesConfiguration {
    @Field(label="Cache/Index Dir", description = "Directory where the MyMovies metadata will be indexed")
    private String  indexDir     = "cache/indexMyMovies/";

    @Field(label="MyMovies Xml", description = "MyMovies xml file")
    private String  xmlFile;

    @Field(label="MyMovies Xml last modified date/time", description = "Data/Time the xml file was modified as a long value.  Should not be set directly.")
    private long xmlFileLastModified;

    public MyMoviesConfiguration() {
    }

    public String getIndexDir() {
        return indexDir;
    }

    public void setIndexDir(String indexDir) {
        this.indexDir = indexDir;
    }

    public String getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(String xmlFile) {
        File f = new File(xmlFile);
        if (!f.exists()) {
            throw new RuntimeException("MyMovies Xml does not exist: " + xmlFile);
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
