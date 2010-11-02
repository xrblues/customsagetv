package org.jdna.bmt.web.client.ui;

import java.util.ArrayList;
import java.util.List;

public class BatchOperations {
	private static BatchOperations instance = new BatchOperations();
	public static BatchOperations getInstance() {
		return instance;
	}

	private List<BatchOperation> operations = new ArrayList<BatchOperation>();

	public BatchOperations() {
		BatchOperation op;
		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.WatchedVisitor");
		op.setVisitorArg(true);
		op.setLabel("Set Watched");
		op.setStartMessage("Setting the video watched flag for videos");
		op.setBackground(false);
		op.setConfirm("Press OK to set all vides to Watched");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.WatchedVisitor");
		op.setVisitorArg(false);
		op.setLabel("Set Unwatched");
		op.setStartMessage("Setting the video unwatched flag for videos");
		op.setBackground(false);
		op.setConfirm("Press OK to set all vides to Unwatched");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.ArchiveVisitor");
		op.setVisitorArg(true);
		op.setLabel("Set Archived");
		op.setStartMessage("Archiving Videos");
		op.setBackground(false);
		op.setConfirm("Press OK to Archive Videos");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.ArchiveVisitor");
		op.setVisitorArg(false);
		op.setLabel("Set Unarchived");
		op.setStartMessage("Unarchiving Videos");
		op.setBackground(false);
		op.setConfirm("Press OK to Unarchive Videos");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.ImportAsRecordingVisitor");
		op.setVisitorArg(true);
		op.setLabel("Import as Recoding");
		op.setStartMessage("Importing videos as Recordings");
		op.setBackground(false);
		op.setConfirm("Press OK to Import the videos as Recordings");
		operations.add(op);
		
		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.ImportAsRecordingVisitor");
		op.setVisitorArg(false);
		op.setLabel("Un-import as Recoding");
		op.setStartMessage("Un-importing videos from Recordings");
		op.setBackground(false);
		op.setConfirm("Press OK to Un-import the videos from Recordings");
		operations.add(op);
		
		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.RemoveFanartFilesVisitor");
		op.setLabel("Remove Fanart Files");
		op.setStartMessage("Removing Fanart Files for media");
		op.setBackground(true);
		op.setConfirm("Press OK to remove Fanart Files");
		op.setCompleteMessage("Fanart Files have been removed for selected media");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.RemovePropertiesFileVisitor");
		op.setLabel("Remove .properties Files");
		op.setStartMessage("Removing .properties Files for media");
		op.setBackground(true);
		op.setConfirm("Press OK to remove .properties Files");
		op.setCompleteMessage(".properties Files have been removed for selected media");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.ClearCustomMetadataFieldsVisitor");
		op.setLabel("Clear custom metadata fields");
		op.setStartMessage("Clearing custom metadata fields");
		op.setBackground(true);
		op.setConfirm("Press OK to clear custom metadata fields");
		op.setCompleteMessage("Custom metadata fields have been cleared");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.MetadataPropertiesExportVisitor");
		op.setLabel("Export .properties");
		op.setStartMessage("Exporting .properties");
		op.setCompleteMessage("Properties have been exported");
		op.setBackground(true);
		op.setConfirm("Press OK to create .properties for every Video file in your collection (including TV, DVD, Bluray, etc)");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.FixGenresVisitor");
		op.setLabel("Fix Genres");
		op.setStartMessage("Fixing Genres");
		op.setCompleteMessage("Genres has been fixed");
		op.setBackground(true);
		op.setConfirm("Press OK to fix Genres.  This will replace all genres like 'Action/Adventure' with 2 genres 'Action' and 'Adventure'.  It will enforce that there never more than 2 genres per media item.");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.ExportSageTV7ThumbnailVisitor");
		op.setLabel("Export SageTV media thumbnail");
		op.setStartMessage("Exporting SageTV media thumbnails");
		op.setCompleteMessage("Thumbnails have been created");
		op.setBackground(true);
		op.setConfirm("Press OK to export a SageTV7 compatible thumbnail.  This will generate a .jpg in the same folder as the media item for each media file.  It will not overwrite existing thumbnails.");
		operations.add(op);
	}
	
	public List<BatchOperation> getBatchOperations() {
		return operations;
	}
}
