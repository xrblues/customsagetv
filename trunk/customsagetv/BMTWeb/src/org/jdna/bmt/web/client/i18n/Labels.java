package org.jdna.bmt.web.client.i18n;

import com.google.gwt.i18n.client.Constants;

public interface Labels extends Constants {

    @DefaultStringValue("Scan for new media")
    String scanNewMedia();

    @DefaultStringValue("Reindex entire media library")
    String reindexAll();

    @DefaultStringValue("Refresh SageTV Media Library")
    String scanDialogTitle();

    @DefaultStringValue("Status")
    String status();

    @DefaultStringValue("Configure")
    String configure();

    @DefaultStringValue("Scan")
    String scan();

    @DefaultStringValue("Browse")
    String browse();

    @DefaultStringValue("Refresh")
    String refreshLibrary();

    @DefaultStringValue("Batch Metadata Tools")
    String appTitle();

    @DefaultStringValue("Phoenix")
    String statusPhoenix();

    @DefaultStringValue("Phoenix Status Information")
    String statusPhoenixDesc();

    @DefaultStringValue("Metadata Tools")
    String metadataTools();

    @DefaultStringValue("Metadata Tools Status Information")
    String metadataToolsDesc();

    @DefaultStringValue("SageTV")
    String sagetv();

    @DefaultStringValue("SageTV Status Information")
    String sagetvDesc();

    @DefaultStringValue("List Roots")
    String listRoots();

    @DefaultStringValue("Sage Home")
    String sageHome();

    @DefaultStringValue("Ok")
    String ok();

    @DefaultStringValue("Cancel")
    String cancel();

    @DefaultStringValue("Down")
    String down();

    @DefaultStringValue("Remove")
    String remove();

    @DefaultStringValue("Recordings")
    String recordings();

    @DefaultStringValue("DVDs")
    String dvds();

    @DefaultStringValue("Videos")
    String videos();

    @DefaultStringValue("File System")
    String filesystem();

    @DefaultStringValue("Genre")
    String genres();

    @DefaultStringValue("Views")
    String views();

    @DefaultStringValue("Sources")
    String sources();

    @DefaultStringValue("Groups")
    String groups();

    @DefaultStringValue("Filters")
    String filters();

    @DefaultStringValue("Sorts")
    String sorts();

    @DefaultStringValue("Scans")
    String scans();

    @DefaultStringValue("JARs")
    String jars();

    @DefaultStringValue("Installed JARs")
    String jarsDesc();

    @DefaultStringValue("Tools")
    String toolMenu();

    @DefaultStringValue("This dialog will create a zip file containing your log files, sage properties, comments, and even a virtual layout of your import folders that can be used to testing.  Simply check off the options that you want to include.")
    String supportHeaderText();

    @DefaultStringValue("Comment")
    String comment();

    @DefaultStringValue("Include Logs?")
    String includeLogs();

    @DefaultStringValue("Include Sage Properties?")
    String includeProperties();

    @DefaultStringValue("Include Sage Import Filenames?")
    String includeImports();

    @DefaultStringValue("Create Request")
    String createSupportRequest();

    @DefaultStringValue("Success")
    String success();

    @DefaultStringValue("<b>Remove Property Files</b>")
    String deleteProperties();

    @DefaultStringValue("Backup Wiz.bin")
    String backupWizBin();

    @DefaultStringValue("Backup History")
    String backupHistory();

    @DefaultStringValue("No Backup Files")
    String nobackupfiles();

    @DefaultStringValue("Help")
	String help();

    @DefaultStringValue("Batch Operations")
	String batchOperations();

    @DefaultStringValue("Add New MediaTitle Matcher")
	String addMediaTitlesDialog();
}
