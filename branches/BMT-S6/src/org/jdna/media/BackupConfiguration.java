package org.jdna.media;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="Backup Configuration", path = "bmt/backup", description = "Configuration backuing up the Wiz.bin")
public class BackupConfiguration extends GroupProxy {
    @AField(label="Backup before full WebUI scan", description = "Backup Wiz.bin before doing a full WebUI scan")
    private FieldProxy<Boolean>  bacupBeforeScan = new FieldProxy<Boolean>(true);

    @AField(label="Backup Folder", description = "Backup folder", editor="dirChooser")
    private FieldProxy<String>  backupFolder   = new FieldProxy<String>("Backups/WIZ/");

    public BackupConfiguration() {
        super();
        init();
    }

    /**
     * @return the bacupBeforeScan
     */
    public boolean getBacupBeforeScan() {
        return bacupBeforeScan.getBoolean();
    }

    /**
     * @param bacupBeforeScan the bacupBeforeScan to set
     */
    public void setBacupBeforeScan(boolean bacupBeforeScan) {
        this.bacupBeforeScan.set(bacupBeforeScan);
    }

    /**
     * @return the backupFolder
     */
    public String getBackupFolder() {
        return backupFolder.getString();
    }

    /**
     * @param backupFolder the backupFolder to set
     */
    public void setBackupFolder(String backupFolder) {
        this.backupFolder.set(backupFolder);
    }
}
