package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 23/04/09 7:39 AM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/TranscodeAPI.html'>TranscodeAPI</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public class TranscodeAPI {
/**
Gets the names of the different transcode formats

Returns:
a list of the names of the different transcode formats
Since:
5.1
 */
public static java.lang.String[] GetTranscodeFormats () {
   return (java.lang.String[]) sagex.SageAPI.call("GetTranscodeFormats", (Object[])null);
}

/**
Gets the format details for the specified format name

Parameters:
FormatName- the name of the transcode format to get the parameter details for
Returns:
the full detail string that describes the specified transcode format
Since:
5.1
 */
public static java.lang.String GetTranscodeFormatDetails (java.lang.String FormatName) {
   return (java.lang.String) sagex.SageAPI.call("GetTranscodeFormatDetails", new Object[] {FormatName});
}

/**
Adds the specified transcode format to the list of available formats

Parameters:
FormatName- the name of the new transcode format
FormatDetails- the detailed property string for the new format
Since:
5.1
 */
public static java.lang.String AddTranscodeFormat (java.lang.String FormatName, java.lang.String FormatDetails) {
   return (java.lang.String) sagex.SageAPI.call("AddTranscodeFormat", new Object[] {FormatName,FormatDetails});
}

/**
Removed the specified transcode format to the list of available formats

Parameters:
FormatName- the name of the transcode format to remove
Since:
5.1
 */
public static java.lang.String RemoveTranscodeFormat (java.lang.String FormatName) {
   return (java.lang.String) sagex.SageAPI.call("RemoveTranscodeFormat", new Object[] {FormatName});
}

/**
Adds the specified job to the transcoder's queue. Returns a Job ID# for future reference of it.

Parameters:
SourceMediaFile- the source file that is to be transcoded, if it consists of multiple segments, all segments will be transcoded
FormatName- the name of the transcode format to use for this conversion
DestinationFile- the target file path for the conversion or null if SageTV should automatically determine the filename of the target files, if a directory is given then SageTV auto-generates the filename in that directory
DeleteSourceAfterTranscode- if true then the source media files are deleted when the transcoding is done, if false the source files are kept
Returns:
the job ID number to reference this transcode job
Since:
5.1
 */
public static int AddTranscodeJob (Object SourceMediaFile, java.lang.String FormatName, java.io.File DestinationFile, boolean DeleteSourceAfterTranscode) {
   return (Integer) sagex.SageAPI.call("AddTranscodeJob", new Object[] {SourceMediaFile,FormatName,DestinationFile,DeleteSourceAfterTranscode});
}

/**
Adds the specified job to the transcoder's queue. Returns a Job ID# for future reference of it. This allows specification of the
 start time and duration for the media which allows extracting a 'clip' from a file.

Parameters:
SourceMediaFile- the source file that is to be transcoded, if it consists of multiple segments, all segments will be transcoded
FormatName- the name of the transcode format to use for this conversion
DestinationFile- the target file path for the conversion or null if SageTV should automatically determine the filename of the target files, if a directory is given then SageTV auto-generates the filename in that directory
DeleteSourceAfterTranscode- if true then the source media files are deleted when the transcoding is done, if false the source files are kept
ClipTimeStart- specifies the time in the file in seconds that the clip starts at (this number is relative to the beginning of the actual file)
ClipDuration- specifies the duration of the clip in seconds to extract from the file (0 to convert until the end of the file)
Returns:
the job ID number to reference this transcode job
Since:
5.1
 */
public static int AddTranscodeJob (Object SourceMediaFile, java.lang.String FormatName, java.io.File DestinationFile, boolean DeleteSourceAfterTranscode, long ClipTimeStart, long ClipDuration) {
   return (Integer) sagex.SageAPI.call("AddTranscodeJob", new Object[] {SourceMediaFile,FormatName,DestinationFile,DeleteSourceAfterTranscode,ClipTimeStart,ClipDuration});
}

/**
Gets the status of the specified transcoding job

Parameters:
JobID- the Job ID of the transcoding job to get the status of
Returns:
the status information for the specified transcoding job, will be one of: COMPLETED, TRANSCODING, WAITING TO START, or FAILED
Since:
5.1
 */
public static java.lang.String GetTranscodeJobStatus (int JobID) {
   return (java.lang.String) sagex.SageAPI.call("GetTranscodeJobStatus", new Object[] {JobID});
}

/**
Cancels the specified transcoding ob

Parameters:
JobID- the Job ID of the transcoding job to cancel
Returns:
true if the job exists and was cancelled, false otherwise
Since:
5.1
 */
public static boolean CancelTranscodeJob (int JobID) {
   return (Boolean) sagex.SageAPI.call("CancelTranscodeJob", new Object[] {JobID});
}

/**
Gets the source file of the specified transcoding job

Parameters:
JobID- the Job ID of the transcoding job to get the source file for
Returns:
the source file of the specified transcoding job
Since:
5.1
 */
public static Object GetTranscodeJobSourceFile (int JobID) {
   return (Object) sagex.SageAPI.call("GetTranscodeJobSourceFile", new Object[] {JobID});
}

/**
Gets the destination file of the specified transcoding job

Parameters:
JobID- the Job ID of the transcoding job to get the destination file for
Returns:
the destination file of the specified transcoding job, or null if no destination file was specified
Since:
5.1
 */
public static java.io.File GetTranscodeJobDestFile (int JobID) {
   return (java.io.File) sagex.SageAPI.call("GetTranscodeJobDestFile", new Object[] {JobID});
}

/**
Returns whether or not the specified transcoding job retains the original source file

Parameters:
JobID- the Job ID of the transcoding job to get the destination file for
Returns:
true if the specified transcoding job keeps its original file when done, false otherwise
Since:
5.1
 */
public static boolean GetTranscodeJobShouldKeepOriginal (int JobID) {
   return (Boolean) sagex.SageAPI.call("GetTranscodeJobShouldKeepOriginal", new Object[] {JobID});
}

/**
Returns the clip start time for the specified transcode job

Parameters:
JobID- the Job ID of the transcoding job to get the destination file for
Returns:
the clip start time for the specified transcode job, 0 if the start time is unspecified
Since:
5.1
 */
public static long GetTranscodeJobClipStart (int JobID) {
   return (Long) sagex.SageAPI.call("GetTranscodeJobClipStart", new Object[] {JobID});
}

/**
Returns the clip duration for the specified transcode job

Parameters:
JobID- the Job ID of the transcoding job to get the destination file for
Returns:
the clip duration for the specified transcode job, 0 if the entire file will be trancoded
Since:
5.1
 */
public static long GetTranscodeJobClipDuration (int JobID) {
   return (Long) sagex.SageAPI.call("GetTranscodeJobClipDuration", new Object[] {JobID});
}

/**
Gets the target format of the specified transcoding job

Parameters:
JobID- the Job ID of the transcoding job to get the target format file for
Returns:
the target format of the specified transcoding job
Since:
5.1
 */
public static java.lang.String GetTranscodeJobFormat (int JobID) {
   return (java.lang.String) sagex.SageAPI.call("GetTranscodeJobFormat", new Object[] {JobID});
}

/**
Removes all of the completed transcode jobs from the transcoder queue

Since:
5.1
 */
public static void ClearTranscodedJobs () {
    sagex.SageAPI.call("ClearTranscodedJobs", (Object[])null);
}

/**
Returns a list of the job IDs for all the current jobs in the transcode queue.

Returns:
the list of job IDs for all the current jobs in the transcode queue
Since:
5.1
 */
public static java.lang.Integer[] GetTranscodeJobs () {
   return (java.lang.Integer[]) sagex.SageAPI.call("GetTranscodeJobs", (Object[])null);
}

/**
Returns true if the specified MediaFile can be transcoded, false otherwise. Transcoding may be restricted
 by certain formats and also by DRM.

Parameters:
MediaFile- the MediaFile object
Returns:
true if the specified MediaFile can be transcoded, false otherwise
Since:
5.1
 */
public static boolean CanFileBeTranscoded (Object MediaFile) {
   return (Boolean) sagex.SageAPI.call("CanFileBeTranscoded", new Object[] {MediaFile});
}

/**
Gets the percent complete (between 0 and 1 as a float) for a transcode job

Parameters:
JobID- the Job ID of the transcoding job to get the percent complete of
Returns:
the percent complete for the specified transcoding job
Since:
5.1
 */
public static float GetTranscodeJobCompletePercent (int JobID) {
   return (Float) sagex.SageAPI.call("GetTranscodeJobCompletePercent", new Object[] {JobID});
}




/** Convenience method for setting this thread's UI Context
@see sagex.SageAPI.setUIConext()*/
public static void setUIContext(String context) {
   sagex.SageAPI.setUIContext(context);
}
}
