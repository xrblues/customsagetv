package org.jdna.bmt.web.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface Msgs extends Messages {
    @DefaultMessage("No Editor For: {0}")
    String noEditor(String type);

    @DefaultMessage("Unable to save preferences.")
    String failedToSavePreferences();

    @DefaultMessage("Nothing to save.")
    String nothingToSave();

    @DefaultMessage("Preferences are saved.")
    String preferencesSaved();

    @DefaultMessage("Nothing Found for {0}")
    String nothingFoundFor(String value);

    @DefaultMessage("Failed to start scan")
    String failedToStartScan();
 
    @DefaultMessage("Failed to get Files.")
    String failedToGetFiles();

    @DefaultMessage("Failed to browse media folder {0}")
    String failedToBrowseFolder(String title);

    @DefaultMessage("Loading Folder {0}")
    String loadingFolder(String title);

    @DefaultMessage("Back to {0}")
    String backTo(String title);

    @DefaultMessage("Loading Source {0}")
    String browsingSource(String source);

    @DefaultMessage("Unable to browse media source {0}")
    String failedToBrowseSource(String label);

    @DefaultMessage("Unable to get media sources")
    String failedToGetSources();

    @DefaultMessage("There are no sources configured.  You can configure sources in the vfs.xml file.")
    String noSourcesConfigured();

    @DefaultMessage("The following source, {0}, is not valid")
    String invalidBrowseSource(String id);

    @DefaultMessage("No items configured for {0}")
    String factoryNotConfigured(String factoryId);

    @DefaultMessage("Unable to get items for {0}")
    String failedToGetFactoryInfo(String label);

    @DefaultMessage("Unable to scan folder {0}")
    String failedToScan(String title);

    @DefaultMessage("Scan ({0})")
    String scanLabel(String title);

    @DefaultMessage("Cancelled ({0} : {1})")
    String cancelledWithStatus(int successCount, int failedCount);

    @DefaultMessage("Complete ({0} : {1})")
    String completeWithStatus(int successCount, int failedCount);

    @DefaultMessage("Created File <b>{0}</b>.<br/><br/>Please send it to <a target=\"_newtab\" href=\"mailto:metadatatools@gmail.com\">metadatatools@gmail.com</a>")
    String supportFileCreated(String result);

    @DefaultMessage("This process will <i>delete</i> .properties files from your video import directories.<br/><br/>.properties files are created in bmt/SageTV so that SageTV can import the metadata.  .properties may also serve as a backup of your metadata.<br/><br/>")
    String cleanProperties();

    @DefaultMessage("Removed <b>{0}</b> files")
    String removedProperties(Integer result);

    @DefaultMessage("Creating support zip file...")
    String creatingSupportFile();

    @DefaultMessage("Cleaning property files...")
    String cleaningProperties();

    @DefaultMessage("{0} - {1}")
    String scanStatus(String status, int d);

    @DefaultMessage("You are about to backup your Wiz.bin database.  Press OK to backup, Cancel to abort.")
    String backupWizBin();

    @DefaultMessage("Unable to get backup list")
    String unableToGetBackupList();

    @DefaultMessage("No media items for folder {0}")
    String nothingToShowFor(String title);

    @DefaultMessage("Configuration ({0}) has been reloaded")
	String configurationReloaded(String id);

    @DefaultMessage("Configuration ({0}) failed to reload")
	String configurationFailedToReload(String id);
}
