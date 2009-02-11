package org.jdna.media.metadata.impl.composite;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="Composite Metadata Provider", name = "compositeMetadataProviders", description = "Configuration for a Composite Metadata Provider", requiresKey = true)
public class CompositeMetadataConfiguration {
    @Field(label="Provider Id", key = true, description = "Composite Provider Unique ID")
    private String id;

    @Field(label="Name", description = "Provider Name")
    private String name;

    @Field(label="Description", description = "Provider Description")
    private String description;

    @Field(label="Icon Url", description = "Optional Icon Url for the provider")
    private String iconUrl;

    @Field(label="Search Provider Id", description = "Provider ID to use when searching")
    private String searchProviderId;

    @Field(label="Details Provider Id", description = "Provider ID to use when getting details")
    private String detailProviderId;

    @Field(label="Search Fields to Use in Details", description = "Semi-Colon separated fields to use from the search provider.  ie, if you want to use the Genre field from the search provider details, then put Genre in this setting")
    private String fieldsFromSearchProvider;

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

    public String getFieldsFromSearchProvider() {
        return fieldsFromSearchProvider;
    }

    public void setFieldsFromSearchProvider(String fieldsFromSearchProvider) {
        this.fieldsFromSearchProvider = fieldsFromSearchProvider;
    }
}
