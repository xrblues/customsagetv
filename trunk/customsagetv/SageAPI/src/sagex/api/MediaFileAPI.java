package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit Generated Date/Time: 05/10/08
 * 4:56 PM See Official Sage Documentation at <a
 * href='http://download.sage.tv/api/sage/api/MediaFileAPI.html'>MediaFileAPI</a>
 * This Generated API is not Affiliated with SageTV. It is user contributed.
 */
public class MediaFileAPI {
	/**
	 * Returns all of the MediaFile objects in the database.
	 * 
	 * Returns: a list of all of the MediaFile objects in the database
	 */
	public static Object[] GetMediaFiles() {
		return (Object[]) sagex.SageAPI.call("GetMediaFiles", (Object[]) null);
	}

	/**
	 * Returns all of the MediaFile objects in the database The content it
	 * references must also match one of the media types specified in the
	 * MediaMask. There's also an additional supported type of 'L' which
	 * indicates files that pass IsLibraryFile()
	 * 
	 * Returns: a list of all of the MediaFile objects in the database that
	 * match the mask Since: 6.4
	 */
	public static Object[] GetMediaFiles(java.lang.String MediaMask) {
		return (Object[]) sagex.SageAPI.call("GetMediaFiles", new Object[] { MediaMask });
	}

	/**
	 * Adds a new MediaFile to the database. This file will remain in the
	 * database until it is manually removed by the user or when the file no
	 * longer exists.
	 * 
	 * Parameters: File- the file path for the new MediaFile NamePrefix- the
	 * 'prefix' to prepend to the name of this media file for hierarchical
	 * purposes (i.e. the subdirectory that the file is in relative to the
	 * import root) Returns: the newly added MediaFile object
	 */
	public static Object AddMediaFile(java.io.File File, java.lang.String NamePrefix) {
		return (Object) sagex.SageAPI.call("AddMediaFile", new Object[] { File, NamePrefix });
	}

	/**
	 * Sets a link between a MediaFile object which represents a file(s) on disk
	 * and an Airing object which represents metadata about the content. This is
	 * a way to link content information with media.
	 * 
	 * Parameters: MediaFile- the MediaFile object to set the content
	 * information for Airing- the Airing object that should be the content
	 * metadata pointer for this MediaFile Returns: true if the operation
	 * succeeded, false otherwise; this operation will fail if the Airing is
	 * already linked to another MediaFile
	 */
	public static boolean SetMediaFileAiring(Object MediaFile, Object Airing) {
		return (Boolean) sagex.SageAPI.call("SetMediaFileAiring", new Object[] { MediaFile, Airing });
	}

	/**
	 * Sets a link between a MediaFile object which represents a file(s) on disk
	 * and a Show object which represents metadata about the content. This is a
	 * way to link content information with media. This will create a new Airing
	 * representing this Show and add it to the database. Then that new Airing
	 * is linked with this MediaFile (just like it is inSetMediaFileAiring()
	 * 
	 * 
	 * Parameters: MediaFile- the MediaFile object to set the content
	 * information for Show- the Show object that should be the content
	 * information for this MediaFile Returns: true if the operation succeeded,
	 * false otherwise; this operation will fail only if one of the arguments is
	 * null
	 */
	public static boolean SetMediaFileShow(Object MediaFile, Object Show) {
		return (Boolean) sagex.SageAPI.call("SetMediaFileShow", new Object[] { MediaFile, Show });
	}

	/**
	 * Returns the MediaFile from the database that corresponds to a specified
	 * file on disk
	 * 
	 * Parameters: FilePath- the file path to find the corresponding MediaFile
	 * for Returns: the MediaFile for the corresponding file path, or null if
	 * there is no corresponding MediaFile
	 */
	public static Object GetMediaFileForFilePath(java.io.File FilePath) {
		return (Object) sagex.SageAPI.call("GetMediaFileForFilePath", new Object[] { FilePath });
	}

	/**
	 * Returns true if the specified MediaFile is local to this system (i.e.
	 * doesn't need to be streamed from a server)
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if the
	 * specified MediaFile is local to this system (i.e. doesn't need to be
	 * streamed from a server), false otherwise
	 */
	public static boolean IsLocalFile(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsLocalFile", new Object[] { MediaFile });
	}

	/**
	 * Returns true if the specified MediaFile has been either imported using a
	 * library path or if this is a television recording that has had the 'Move
	 * to Library' operation performed on it.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if the
	 * specified MediaFile has been either imported using a library path or if
	 * this is a television recording that has had the 'Move to Library'
	 * operation performed on it; false otherwise
	 */
	public static boolean IsLibraryFile(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsLibraryFile", new Object[] { MediaFile });
	}

	/**
	 * Returns true if SageTV considers this MediaFile a 'complete' recording.
	 * The rules behind this are somewhat complex, but the intended purpose is
	 * that a 'complete' recording is one that should be presented in the list
	 * of recordings to a user.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if SageTV
	 * considers this MediaFile a 'complete' recording
	 */
	public static boolean IsCompleteRecording(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsCompleteRecording", new Object[] { MediaFile });
	}

	/**
	 * Returns true if this MediaFile represents DVD content. This can be either
	 * a DVD drive or a ripped DVD.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if this
	 * MediaFile represents DVD content, false otherwise
	 */
	public static boolean IsDVD(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsDVD", new Object[] { MediaFile });
	}

	/**
	 * Returns true if this MediaFile represents the physical DVD drive in the
	 * system. Use this MediaFile to playback DVDs from an optical drive.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if this
	 * MediaFile represents the physical DVD drive in the system, false
	 * otherwise
	 */
	public static boolean IsDVDDrive(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsDVDDrive", new Object[] { MediaFile });
	}

	/**
	 * Returns true if this MediaFile's content is audio only.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if this
	 * MediaFile's content is audio only, false otherwise
	 */
	public static boolean IsMusicFile(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsMusicFile", new Object[] { MediaFile });
	}

	/**
	 * Returns true if this MediaFile's content is an audio/video or video file
	 * (this will be false for DVD content)
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if this
	 * MediaFile's content is an audio/video or video file (this will be false
	 * for DVD content), false otherwise
	 */
	public static boolean IsVideoFile(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsVideoFile", new Object[] { MediaFile });
	}

	/**
	 * Returns true if this MediaFile's content represents a picture file
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if this
	 * MediaFile's content represents a picture file, false otherwise
	 */
	public static boolean IsPictureFile(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsPictureFile", new Object[] { MediaFile });
	}

	/**
	 * Returns true if this MediaFile represents recorded television content
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if this
	 * MediaFile represents recorded television content, false otherwise
	 */
	public static boolean IsTVFile(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsTVFile", new Object[] { MediaFile });
	}

	/**
	 * Returns the list of files that make up the specified MediaFile object. A
	 * MediaFile object can represent more than one physical file on disk. This
	 * occurs when a recording of a television show is not contiguous; this can
	 * happen for various reasons including the user changing the channel or
	 * restarting the system.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the list of files
	 * that make up this MediaFile object
	 */
	public static java.io.File[] GetSegmentFiles(Object MediaFile) {
		return (java.io.File[]) sagex.SageAPI.call("GetSegmentFiles", new Object[] { MediaFile });
	}

	/**
	 * Returns the title for the specified MediaFile object
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the title for the
	 * specified MediaFile object
	 */
	public static java.lang.String GetMediaTitle(Object MediaFile) {
		return (java.lang.String) sagex.SageAPI.call("GetMediaTitle", new Object[] { MediaFile });
	}

	/**
	 * Gets the directory that the files for this MediaFile are in.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the directory that
	 * the files for this MediaFile are in
	 */
	public static java.io.File GetParentDirectory(Object MediaFile) {
		return (java.io.File) sagex.SageAPI.call("GetParentDirectory", new Object[] { MediaFile });
	}

	/**
	 * Gets the total size in bytes of the files on disk that represent this
	 * MediaFile
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the total size in
	 * bytes of the files on disk that represent this MediaFile
	 */
	public static long GetSize(Object MediaFile) {
		return (Long) sagex.SageAPI.call("GetSize", new Object[] { MediaFile });
	}

	/**
	 * Returns the MetaImage object which represents the picture file for this
	 * MediaFile. If the specified MediaFile is not a picture file, then null is
	 * returned
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the MetaImage object
	 * which represents the picture file for this MediaFile. If the specified
	 * MediaFile is not a picture file, then null is returned
	 */
	public static Object GetFullImage(Object MediaFile) {
		return (Object) sagex.SageAPI.call("GetFullImage", new Object[] { MediaFile });
	}

	/**
	 * Gets the representative thumbnail image which should be used for iconic
	 * display of this MediaFile. For picture files, this will be a thumbnail
	 * image. For music files it will be the album art. For any other files
	 * it'll be the thumbnail for the file if one exists, otherwise it'll be the
	 * channel logo for the file. If none of those exist then null is returned.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the representative
	 * thumbnail image which should be used for iconic display of this MediaFile
	 */
	public static Object GetThumbnail(Object MediaFile) {
		return (Object) sagex.SageAPI.call("GetThumbnail", new Object[] { MediaFile });
	}

	/**
	 * Checks whether the passed thumbnail for the specified MediaFile is loaded
	 * into system memory or into the VRAM cache of the corresponding UI making
	 * the call.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if the
	 * thumbnail image for the specified MediaFile is loaded into system memory
	 * or the calling UI's VRAM, false otherwise Since: 6.1
	 */
	public static boolean IsThumbnailLoaded(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsThumbnailLoaded", new Object[] { MediaFile });
	}

	/**
	 * Returns true if this MediaFile object has a thumbnail for it that is
	 * unique to the content itself. This is true for any music file with album
	 * art or any other MediaFile that has another file on disk which contains
	 * the representative thumbnail.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if this
	 * MediaFile object has a thumbnail for it that is unique to the content
	 * itself, false otherwise
	 */
	public static boolean HasSpecificThumbnail(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("HasSpecificThumbnail", new Object[] { MediaFile });
	}

	/**
	 * Returns true if this MediaFile object has a thumbnail representation of
	 * it. If this is true, thenGetThumbnail() will not return null.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if this
	 * MediaFile object has a thumbnail representation of it, false otherwise
	 */
	public static boolean HasAnyThumbnail(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("HasAnyThumbnail", new Object[] { MediaFile });
	}

	/**
	 * Returns true if this MediaFile is currently in the process of recording.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if this
	 * MediaFile is currently in the process of recording, false otherwise
	 */
	public static boolean IsFileCurrentlyRecording(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("IsFileCurrentlyRecording", new Object[] { MediaFile });
	}

	/**
	 * Deletes the files that correspond to this MediaFile from disk and also
	 * deletes the MediaFile object from the database. NOTE: This actually
	 * delete the files from the disk. This has a slightly different effect on
	 * Intelligent Recording versus theDeleteFileWithoutPrejudice()
	 * 
	 * 
	 * Parameters: MediaFile- the MediaFile object to delete Returns: true if
	 * the deletion succeeded, false otherwise. A deletion can fail because the
	 * file is currently being recorded or watched or because the native
	 * filesystem is unable to delete the file.
	 */
	public static boolean DeleteFile(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("DeleteFile", new Object[] { MediaFile });
	}

	/**
	 * Deletes the files that correspond to this MediaFile from disk and also
	 * deletes the MediaFile object from the database. NOTE: This actually
	 * delete the files from the disk. This has a slightly different effect on
	 * Intelligent Recording versusDeleteFile() . DeleteFileWithoutPrejudice
	 * should be used when the file was incorrectly recorded or in other cases
	 * where this deletion decision should have no effect on intelligent
	 * recording.
	 * 
	 * Parameters: MediaFile- the MediaFile object to delete Returns: true if
	 * the deletion succeeded, false otherwise. A deletion can fail because the
	 * file is currently being recorded or watched or because the native
	 * filesystem is unable to delete the file.
	 */
	public static boolean DeleteFileWithoutPrejudice(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("DeleteFileWithoutPrejudice", new Object[] { MediaFile });
	}

	/**
	 * Returns the total duration of the content in this MediaFile
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the total duration
	 * in milliseconds of the content in the specified MediaFile
	 */
	public static long GetFileDuration(Object MediaFile) {
		return (Long) sagex.SageAPI.call("GetFileDuration", new Object[] { MediaFile });
	}

	/**
	 * Returns the starting time for the content in ths specified MediaFile.
	 * This corresponds to when the file's recording started or the timestamp on
	 * the file itself. See java.lang.System.currentTimeMillis() for information
	 * on the time units.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the starting time
	 * for the content in the specified MediaFile
	 */
	public static long GetFileStartTime(Object MediaFile) {
		return (Long) sagex.SageAPI.call("GetFileStartTime", new Object[] { MediaFile });
	}

	/**
	 * Returns the ending time for the content in ths specified MediaFile. This
	 * corresponds to when the file's recording ended or the timestamp on the
	 * file itself plus the file's duration. See
	 * java.lang.System.currentTimeMillis() for information on the time units.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the ending time for
	 * the content in the specified MediaFile
	 */
	public static long GetFileEndTime(Object MediaFile) {
		return (Long) sagex.SageAPI.call("GetFileEndTime", new Object[] { MediaFile });
	}

	/**
	 * Downloads the specified MediaFile from the SageTV server and saves it as
	 * the specified LocalFile. This call should only be made by SageTV Client.
	 * 
	 * Parameters: MediaFile- the MediaFile object to download a copy of
	 * LocalFile- the destination file to store the MediaFile as on the local
	 * filesystem
	 */
	public static void CopyToLocalFile(Object MediaFile, java.io.File LocalFile) {
		sagex.SageAPI.call("CopyToLocalFile", new Object[] { MediaFile, LocalFile });
	}

	/**
	 * Returns the duration in milliseconds for the specified segment number in
	 * this MediaFile.
	 * 
	 * Parameters: MediaFile- the MediaFile object SegmentNumber- the 0-based
	 * segment number to get the duration of Returns: the duration in
	 * milliseconds for the specified segment number in this MediaFile
	 */
	public static long GetDurationForSegment(Object MediaFile, int SegmentNumber) {
		return (Long) sagex.SageAPI.call("GetDurationForSegment", new Object[] { MediaFile, SegmentNumber });
	}

	/**
	 * Gets the ending time for a specified segment number in this MediaFile.
	 * 
	 * Parameters: MediaFile- the MediaFile object SegmentNumber- the 0-based
	 * segment number to get the end time of Returns: the ending time for a
	 * specified segment number in this MediaFile
	 */
	public static long GetEndForSegment(Object MediaFile, int SegmentNumber) {
		return (Long) sagex.SageAPI.call("GetEndForSegment", new Object[] { MediaFile, SegmentNumber });
	}

	/**
	 * Gets the starting time for a specified segment number in this MediaFile.
	 * 
	 * Parameters: MediaFile- the MediaFile object SegmentNumber- the 0-based
	 * segment number to get the start time of Returns: the starting time for a
	 * specified segment number in this MediaFile
	 */
	public static long GetStartForSegment(Object MediaFile, int SegmentNumber) {
		return (Long) sagex.SageAPI.call("GetStartForSegment", new Object[] { MediaFile, SegmentNumber });
	}

	/**
	 * Gets the file that represents the specified segment number in this
	 * MediaFile
	 * 
	 * Parameters: MediaFile- the MediaFile object SegmentNumber- the 0-based
	 * segment number to get the file for Returns: the file that represents the
	 * specified segment number in this MediaFile
	 */
	public static java.io.File GetFileForSegment(Object MediaFile, int SegmentNumber) {
		return (java.io.File) sagex.SageAPI.call("GetFileForSegment", new Object[] { MediaFile, SegmentNumber });
	}

	/**
	 * Returns the number of segments in ths specified MediaFile. Each segment
	 * corresponds to a different physical file on disk.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the number of
	 * segments in ths specified MediaFile
	 */
	public static int GetNumberOfSegments(Object MediaFile) {
		return (Integer) sagex.SageAPI.call("GetNumberOfSegments", new Object[] { MediaFile });
	}

	/**
	 * Returns a list of all of the start times of the segments in the specified
	 * MediaFile
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: a list of all of the
	 * start times of the segments in the specified MediaFile
	 */
	public static long[] GetStartTimesForSegments(Object MediaFile) {
		return (long[]) sagex.SageAPI.call("GetStartTimesForSegments", new Object[] { MediaFile });
	}

	/**
	 * Marks a MediaFile object as being 'Moved to Library' which means
	 * theIsLibraryFile() call will now return true. This can be used to help
	 * organize the recorded television files.
	 * 
	 * Parameters: MediaFile- the MediaFile ojbect
	 */
	public static void MoveFileToLibrary(Object MediaFile) {
		sagex.SageAPI.call("MoveFileToLibrary", new Object[] { MediaFile });
	}

	/**
	 * Un-marks a MediaFile object as being 'Moved to Library' which means
	 * theIsLibraryFile() call will no longer return true. This can only be used
	 * on recorded television files and has the opposite effect
	 * ofMoveFileToLibrary()
	 * 
	 * 
	 * Parameters: MediaFile- the MediaFile ojbect
	 */
	public static void MoveTVFileOutOfLibrary(Object MediaFile) {
		sagex.SageAPI.call("MoveTVFileOutOfLibrary", new Object[] { MediaFile });
	}

	/**
	 * Returns true if the specified object is a MediaFile object. No automatic
	 * type conversion will be performed on the argument.
	 * 
	 * Parameters: Object- the object to test to see if it is a MediaFile object
	 * Returns: true if the argument is a MediaFile object, false otherwise
	 */
	public static boolean IsMediaFileObject(java.lang.Object Object) {
		return (Boolean) sagex.SageAPI.call("IsMediaFileObject", new Object[] { Object });
	}

	/**
	 * Gets the Album object that corresponds to this MediaFile. This only
	 * returns a useful object if the argument is a music file.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the Album object
	 * that corresponds to this MediaFile
	 */
	public static Object GetAlbumForFile(Object MediaFile) {
		return (Object) sagex.SageAPI.call("GetAlbumForFile", new Object[] { MediaFile });
	}

	/**
	 * Gets the encoding that was used to record this file. This will only
	 * return something useful for recorded television files.
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the encoding that
	 * was used to record this file
	 */
	public static java.lang.String GetMediaFileEncoding(Object MediaFile) {
		return (java.lang.String) sagex.SageAPI.call("GetMediaFileEncoding", new Object[] { MediaFile });
	}

	/**
	 * Gets the Airing object that represents the content metadata for this
	 * MediaFile
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: the Airing object
	 * that represents the content metadata for this MediaFile
	 */
	public static Object GetMediaFileAiring(Object MediaFile) {
		return (Object) sagex.SageAPI.call("GetMediaFileAiring", new Object[] { MediaFile });
	}

	/**
	 * Returns the unique ID used to identify this MediaFile. Can get used later
	 * on a call toGetMediaFileForID()
	 * 
	 * 
	 * Parameters: MediaFile- the MediaFileobject Returns: the unique ID used to
	 * identify this MediaFile
	 */
	public static int GetMediaFileID(Object MediaFile) {
		return (Integer) sagex.SageAPI.call("GetMediaFileID", new Object[] { MediaFile });
	}

	/**
	 * Returns the MediaFile object that corresponds to the passed in ID. The ID
	 * should have been obtained from a call toGetMediaFileID()
	 * 
	 * 
	 * Parameters: id- the id of the MediaFile object to get Returns: the
	 * MediaFile object that corresponds to the passed in ID
	 */
	public static Object GetMediaFileForID(int id) {
		return (Object) sagex.SageAPI.call("GetMediaFileForID", new Object[] { id });
	}

	/**
	 * Returns a string that provides a description of this MediaFile's format,
	 * i.e. MPEG2-PS[MPEG2-Video/2.0Mbps 4:3 480i@30fps, MP2/192kbps@48kHz]
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: a string that
	 * provides a description of this MediaFile's format Since: 5.1
	 */
	public static java.lang.String GetMediaFileFormatDescription(Object MediaFile) {
		return (java.lang.String) sagex.SageAPI.call("GetMediaFileFormatDescription", new Object[] { MediaFile });
	}

	/**
	 * Performs a lossless rotation of the specified JPEG picture file (90, 180
	 * or 270 degrees). This will modify the file that is stored on disk.
	 * 
	 * Parameters: MediaFile- the MediaFile object Degrees- the number of
	 * degress to rotate the picture in the clockwise direction, can be a
	 * positive or negative value and must be a multiple of 90 Returns: true if
	 * the rotation was successful, false otherwise Since: 5.1
	 */
	public static boolean RotatePictureFile(Object MediaFile, int Degrees) {
		return (Boolean) sagex.SageAPI.call("RotatePictureFile", new Object[] { MediaFile, Degrees });
	}

	/**
	 * Performs a lossless flip of the specified JPEG picture file. This will
	 * modify the file that is stored on disk.
	 * 
	 * Parameters: MediaFile- the MediaFile object Horizontal- true if it should
	 * be flipped horizontally (i.e. around a vertical axis), false if it should
	 * be flipped vertically (i.e. around a horizontal axis) Returns: true if
	 * the flip was successful, false otherwise Since: 5.1
	 */
	public static boolean FlipPictureFile(Object MediaFile, boolean Horizontal) {
		return (Boolean) sagex.SageAPI.call("FlipPictureFile", new Object[] { MediaFile, Horizontal });
	}

	/**
	 * Returns true if the specified picture file can be autorotated and is
	 * currently not in that autorotated position
	 * 
	 * Parameters: MediaFile- the MediaFile object Returns: true if the
	 * specified picture file can be autorotated and is currently not in that
	 * autorotated position Since: 6.4
	 */
	public static boolean CanAutorotatePictureFile(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("CanAutorotatePictureFile", new Object[] { MediaFile });
	}

	/**
	 * Automatically rotates the specified picture file according to the
	 * orientation set in the EXIF data.
	 * 
	 * Parameters: MediaFile- the MediaFile object that represents the picture
	 * Returns: true if the automatic rotation succeeded, false otherwise Since:
	 * 6.4
	 */
	public static boolean AutorotatePictureFile(Object MediaFile) {
		return (Boolean) sagex.SageAPI.call("AutorotatePictureFile", new Object[] { MediaFile });
	}

	/**
	 * Regenerates the thumbnail associated with the specified picture file.
	 * Sometimes the rotation may be mis-aligned from the thumbnail and this
	 * allows a way to repair that.
	 * 
	 * Parameters: MediaFile- the MediaFile object Since: 6.4
	 */
	public static void RegeneratePictureThumbnail(Object MediaFile) {
		sagex.SageAPI.call("RegeneratePictureThumbnail", new Object[] { MediaFile });
	}

}
