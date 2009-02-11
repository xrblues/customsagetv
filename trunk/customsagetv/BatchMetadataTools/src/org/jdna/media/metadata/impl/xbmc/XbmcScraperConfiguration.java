package org.jdna.media.metadata.impl.xbmc;

import java.util.HashMap;
import java.util.Map;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="Xmbc Scraper Settings", requiresKey=true, name="xbmcScraper", description="Configuration Element for Xmbc Scraper")
public class XbmcScraperConfiguration {
    @Field(label="Scraper Id", key=true, description="Xbmc Scraper Id")
    private String id;

    @Field(label="Scraper Settings", map=true, description="Xbmc Name Value Pair Settings")
    private Map<String, String> settings = new HashMap();

    public XbmcScraperConfiguration() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}
