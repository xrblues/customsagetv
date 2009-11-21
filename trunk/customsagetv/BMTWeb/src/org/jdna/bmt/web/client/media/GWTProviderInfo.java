package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.media.metadata.IProviderInfo;

public class GWTProviderInfo implements IProviderInfo, Serializable {
    private String description, iconUrl, id, name;
    
    public GWTProviderInfo() {
    }

    public GWTProviderInfo(IProviderInfo info) {
        this.description=info.getDescription();
        this.iconUrl=info.getIconUrl();
        this.id=info.getId();
        this.name=info.getName();
    }
    
    public String getDescription() {
        return description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
