package org.jdna.media.metadata.impl.dvdprof;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(name = "dvdprofiler", requiresKey = false, description = "Configuration for the DVD Profiler Provider that uses urls as it's datasource")
public class DVDProfilerConfiguration {
    @Field(description = "Comma separated list of DVD Profiler Profile urls to use when searching for movies")
    private String  profileUrls;

    @Field(description = "Set this to true to rebuild the indexed urls")
    private boolean forceRebuild = false;

    @Field(description = "Directory where the indexed DVD Profiler data will be stored")
    private String  indexDir     = "cache/index";

    public DVDProfilerConfiguration() {
    }

    public String getProfileUrls() {
        return profileUrls;
    }

    public void setProfileUrls(String profileUrls) {
        this.profileUrls = profileUrls;
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

}
