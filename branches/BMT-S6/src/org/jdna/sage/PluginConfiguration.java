package org.jdna.sage;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="Automatic Plugin Configuration", path = "bmt/plugin", description = "Configuration for Automatic Plugin")
public class PluginConfiguration extends GroupProxy {
    @AField(label="Enabled", description="Enable/Disable the Automatic Plugin (does not remove it)")
    private FieldProxy<Boolean> enabled = new FieldProxy<Boolean>(true);

    @AField(label="Record Failures as System Message", description="Write a SystemMessage for any failed media items")
    private FieldProxy<Boolean> useSystemMessagesForFailed = new FieldProxy<Boolean>(true);

    @AField(label="Supported MediaResource File Types", description="Comma Separated list of Media Resource Types that the Automatic scanner will support; TV, DVD, BLURAY, ANY_VIDEO, MUSIC, VIDEO")
    private FieldProxy<String> supportedMediaTypes = new FieldProxy<String>("ANY_VIDEO");

    @AField(label="Create Default STV Thumbnail", description="If true, then create a default STV thumbnail")
    private FieldProxy<Boolean> createDefaultSTVThumbnail = new FieldProxy<Boolean>(Boolean.FALSE);

    @AField(label="Create .properties files", description="If true, then create a .properties file for each media file")
    private FieldProxy<Boolean> createProperties = new FieldProxy<Boolean>(Boolean.TRUE);

    public PluginConfiguration() {
        super();
        init();
    }

    public boolean getEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    /**
     * @return the useSystemMessagesForFailed
     */
    public boolean getUseSystemMessagesForFailed() {
        return useSystemMessagesForFailed.get();
    }

    /**
     * @param useSystemMessagesForFailed the useSystemMessagesForFailed to set
     */
    public void setUseSystemMessagesForFailed(boolean useSystemMessagesForFailed) {
        this.useSystemMessagesForFailed.set(useSystemMessagesForFailed);
    }

    public void setSupportedMediaTypes(String supportedMediaTypes) {
        this.supportedMediaTypes.set(supportedMediaTypes);
    }

    public String getSupportedMediaTypes() {
        return supportedMediaTypes.get();
    }

    /**
     * @return the createDefaultSTVThumbnail
     */
    public boolean getCreateDefaultSTVThumbnail() {
        return createDefaultSTVThumbnail.get();
    }

    /**
     * @param createDefaultSTVThumbnail the createDefaultSTVThumbnail to set
     */
    
    public void setCreateDefaultSTVThumbnail(boolean createDefaultSTVThumbnail) {
        this.createDefaultSTVThumbnail.set(createDefaultSTVThumbnail);
    }

    /**
     * @return the createProperties
     */
    public boolean getCreateProperties() {
        return createProperties.getBoolean();
    }

    /**
     * @param createProperties the createProperties to set
     */
    public void setCreateProperties(boolean createProperties) {
        this.createProperties.set(createProperties);
    }
}
