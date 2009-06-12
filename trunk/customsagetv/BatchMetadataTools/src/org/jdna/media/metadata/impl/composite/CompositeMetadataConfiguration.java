package org.jdna.media.metadata.impl.composite;

import sagex.phoenix.configuration.proxy.AField;

public class CompositeMetadataConfiguration {
    @AField(label="Provider Id", description = "Composite Provider Unique ID")
    private String id;

    @AField(label="Name", description = "Provider Name")
    private String name;

    @AField(label="Description", description = "Provider Description")
    private String description;

    @AField(label="Icon Url", description = "Optional Icon Url for the provider")
    private String iconUrl;

    @AField(label="Search Provider Id", description = "Provider ID to use when searching")
    private String searchProviderId;

    @AField(label="Details Provider Id", description = "Provider ID to use when getting details")
    private String detailProviderId;

    @AField(label="Composite Mode", description = "1 - Use Search searchProvider then detailProvider; 2 - Use detailsProvider then searchProvider")
    private int compositeMode=2;

    public CompositeMetadataConfiguration() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getSearchProviderId() {
        return searchProviderId;
    }

    public void setSearchProviderId(String searchProviderId) {
        this.searchProviderId = searchProviderId;
    }

    public String getDetailProviderId() {
        return detailProviderId;
    }

    public void setDetailProviderId(String detailProviderId) {
        this.detailProviderId = detailProviderId;
    }

    public int getCompositeMode() {
        return compositeMode;
    }

    public void setCompositeDetailsMode(int compositeDetailsMode) {
        this.compositeMode = compositeDetailsMode;
    }
}
