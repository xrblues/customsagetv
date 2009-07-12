package org.jdna.sage;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="Automatic Plugin Configuration", path = "bmt/plugin", description = "Configuration for Automatic Plugin")
public class PluginConfiguration extends GroupProxy {
    @AField(label="Enabled", description="Enable/Disable the Automatic Plugin (does not remove it)")
    private FieldProxy<Boolean> enabled = new FieldProxy<Boolean>(true);

    @AField(label="Overwrite Metadata", description="Overwrite Existing Metadata Configuration")
    private FieldProxy<Boolean> overwriteMetadata = new FieldProxy<Boolean>(false);

    @AField(label="Overwrite Fanart", description="Overwrite Existing Fanart")
    private FieldProxy<Boolean> overwriteFanart = new FieldProxy<Boolean>(false);
    
    @AField(label="Missing Media Items Workaround", description="Enable this if you notice that some of your media items are not showing up when you do a SageTV Referesh.  This is a workaround until the problem is solved.")
    private FieldProxy<Boolean> returnNullMetadata = new FieldProxy<Boolean>(false);

    public PluginConfiguration() {
        super();
        init();
    }

    public boolean getOverwriteMetadata() {
        return overwriteMetadata.get();
    }

    public void setOverwriteMetadata(boolean overwriteMetadata) {
        this.overwriteMetadata.set(overwriteMetadata);
    }

    public boolean getOverwriteFanart() {
        return overwriteFanart.get();
    }

    public void setOverwriteFanart(boolean overwriteFanart) {
        this.overwriteFanart.set(overwriteFanart);
    }

    public boolean getEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public boolean getReturnNullMetadata() {
        return returnNullMetadata.get();
    }

    public void setReturnNullMetadata(boolean returnNullMetadata) {
        this.returnNullMetadata.set(returnNullMetadata);
    }
}
