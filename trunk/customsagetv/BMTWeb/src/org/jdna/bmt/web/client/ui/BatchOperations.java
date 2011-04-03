package org.jdna.bmt.web.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		op.setLabel("Import as Recording");
		op.setStartMessage("Importing videos as Recordings");
		op.setBackground(false);
		op.setConfirm("Press OK to Import the videos as Recordings");
		operations.add(op);
		
		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.ImportAsRecordingVisitor");
		op.setVisitorArg(false);
		op.setLabel("Un-import as Recording");
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
		op.setConfirm("Press OK to fix Genres.  This will replace all genres like 'Action/Adventure' with 2 genres 'Action' and 'Adventure'");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.ExportSageTV7ThumbnailVisitor");
		op.setLabel("Export SageTV media thumbnail");
		op.setStartMessage("Exporting SageTV media thumbnails");
		op.setCompleteMessage("Thumbnails have been created");
		op.setBackground(true);
		op.setConfirm("Press OK to export a SageTV7 compatible thumbnail.  This will generate a .jpg in the same folder as the media item for each media file.  It will not overwrite existing thumbnails.");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.FixTitlesVisitor");
		op.setVisitorArg(true);
		op.setLabel("Add Relative Path to Titles");
		op.setStartMessage("Adding Relative path to titles");
		op.setCompleteMessage("Titles have been updated.  You may need to refresh the browser.");
		op.setBackground(true);
		op.setConfirm("Press OK to update the titles for ALL non Sage Recordings so that they include a relative path in the title.  Sage documents that Title field, for videos should have a relative path, but this appears to cause issues when grouping TV shows that are NOT a sage recording.");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.FixTitlesVisitor");
		op.setVisitorArg(false);
		op.setLabel("Remove Relative Path from Titles");
		op.setStartMessage("Removing Relative path from titles");
		op.setCompleteMessage("Titles have been updated.  You may need to refresh the browser.");
		op.setBackground(true);
		op.setConfirm("Press OK to update the titles for ALL non Sage Recordings so that they no longer include a relative path in the title.  Sage documents that Title field, for videos (not recordings) should have a relative path, but this appears to cause issues when grouping TV shows that are NOT a sage recording.");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.vfs.visitors.TVSeriesVisitor");
		op.setLabel("Associate TV Series Information");
		op.setStartMessage("Attempting to locate TVSeries information for TV files.  This may take awhile.");
		op.setCompleteMessage("TV Series Scan is now complete");
		op.setBackground(true);
		op.setConfirm("Press OK to locate TV Series information for your TV Files.  This process will search both Recordings and TV files and attempt to associate a TV Series Info to each file.  In the event that we can't find a TV Series Info in the SageTV Series Information, then we will attempt to locate and add TV Series Info from TVDB");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.metadata.fixes.FixParentalRatingsVisitor");
		op.setLabel("Fix Parental Ratings");
		op.setStartMessage("Fixes Parental Ratings");
		op.setCompleteMessage("Parental Ratings have been fixed");
		op.setBackground(true);
		op.setConfirm("Press OK to fix parental ratings for your video and tv files.");
		operations.add(op);

		op = new BatchOperation();
		op.setVisitorClass("sagex.phoenix.metadata.fixes.FixTVYearVisitor");
		op.setLabel("Fix Year for TV Shows");
		op.setStartMessage("Clearing Year information for TV shows");
		op.setCompleteMessage("Year has been removed from TV Shows");
		op.setBackground(true);
		op.setConfirm("Press OK to remove the Year from your TV Shows.  The Year is not valid for TV shows, and having a year causes the Year to show in the Title.  Note: This does not change titles that actually have a Year in the title itself, it simply removes the Year field from the metadata.");
		operations.add(op);
		
		Collections.sort(operations, new Comparator<BatchOperation>() {
			@Override
			public int compare(BatchOperation o1, BatchOperation o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
	
	}
	
	public List<BatchOperation> getBatchOperations() {
		return operations;
	}
}
