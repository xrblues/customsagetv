package org.jdna.media.metadata.impl.dvdprof;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="Remote DVD Profiler", name = "dvdprofiler", requiresKey = false, description = "Configuration for the DVD Profiler Provider that uses urls as it's datasource")
public class DVDProfilerConfiguration {
    @Field(label="DVD Profiler Urls", description = "Comma separated list of DVD Profiler Profile urls to use when searching for movies")
    private String  profileUrls;

    @Field(label="Cache/Index Dir", description = "Directory where the indexed DVD Profiler data will be stored")
    private String  indexDir     = "cache/index";

    public DVDProfilerConfiguration() {
    }

    public String getProfileUrls() {
        return profileUrls;
    }

    public void setProfileUrls(String profileUrls) {
        this.profileUrls = profileUrls;
    }

    public String getIndexDir() {
        return indexDir;
    }

    public void setIndexDir(String indexDir) {
        this.indexDir = indexDir;
    }

}
