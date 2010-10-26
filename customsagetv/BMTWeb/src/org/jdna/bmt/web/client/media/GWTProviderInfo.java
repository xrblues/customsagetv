package org.jdna.bmt.web.client.media;

import java.io.Serializable;
import java.util.List;

import sagex.phoenix.metadata.IMetadataProviderInfo;
import sagex.phoenix.metadata.MediaType;

public class GWTProviderInfo implements IMetadataProviderInfo, Serializable {
	private static final long serialVersionUID = 1L;

	private String description, iconUrl, id, name;

	private List<MediaType> searchTypes;

	private String fanartId;
	
	private boolean userDefault = false;
    
    public GWTProviderInfo() {
    }

    public GWTProviderInfo(IMetadataProviderInfo info) {
        this.description=info.getDescription();
        this.iconUrl=info.getIconUrl();
        this.id=info.getId();
        this.name=info.getName();
        this.fanartId=info.getFanartProviderId();
        this.searchTypes = info.getSupportedSearchTypes();
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

	@Override
	public String getFanartProviderId() {
		return fanartId;
	}

	@Override
	public List<MediaType> getSupportedSearchTypes() {
		return searchTypes;
	}

	@Override
	public void setFanartProviderId(String fanartProvider) {
	}

	public void setUserDefault(boolean userDefault) {
		this.userDefault = userDefault;
	}

	public boolean isUserDefault() {
		return userDefault;
	}
}
