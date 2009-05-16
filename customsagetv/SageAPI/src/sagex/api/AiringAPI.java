package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 16/05/09 10:05 AM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/AiringAPI.html'>AiringAPI</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public class AiringAPI {
/**
Sets the name for this recording. For Timed Recordings this will effect the title & associated attributes.
 For ManualRecordings this will not have any side effects at all.

Parameters:
Airing- the ManualRecord to set the name for
Name- the name to set
 */
public static void SetRecordingName (Object Airing, java.lang.String Name) {
   sagex.SageAPI.call("SetRecordingName", new Object[] {Airing,Name});
}

/**
Gets the name for this recording that was set viaSetRecordingName(Airing, String)


Parameters:
Airing- the ManualRecord to get the name for
Returns:
the name of the ManualRecord or the empty string if the argument was not a manual record
 */
public static java.lang.String GetRecordingName (Object Airing) {
  Object o = sagex.SageAPI.call("GetRecordingName", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Gets the Channel that this Airing is on

Parameters:
Airing- the Airing object
Returns:
the Channel that this Airing is on
 */
public static Object GetChannel (Object Airing) {
  Object o = sagex.SageAPI.call("GetChannel", new Object[] {Airing});
  if (o!=null) return (Object) o;
  return null;
}

/**
Gets the name of the Channel that this Airing is on

Parameters:
Airing- the Airing object
Returns:
the name of the Channel that this Airing is on
 */
public static java.lang.String GetAiringChannelName (Object Airing) {
  Object o = sagex.SageAPI.call("GetAiringChannelName", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Gets the channel number that this Airing is on

Parameters:
Airing- the Airing object
Returns:
the channel number that this Airing is on
 */
public static java.lang.String GetAiringChannelNumber (Object Airing) {
  Object o = sagex.SageAPI.call("GetAiringChannelNumber", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Gets the duration of this Airing in milliseconds

Parameters:
Airing- the Airing object
Returns:
the duration of this Airing in milliseconds
 */
public static long GetAiringDuration (Object Airing) {
  Object o = sagex.SageAPI.call("GetAiringDuration", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Gets the start time of this Airing. The time is in Java time units, which are milliseconds since Jan 1, 1970 GMT

Parameters:
Airing- the Airing object
Returns:
the start time of this Airing
 */
public static long GetAiringStartTime (Object Airing) {
  Object o = sagex.SageAPI.call("GetAiringStartTime", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Gets the end time of this Airing. The time is in Java time units, which are milliseconds since Jan 1, 1970 GMT

Parameters:
Airing- the Airing object
Returns:
the end time of this Airing
 */
public static long GetAiringEndTime (Object Airing) {
  Object o = sagex.SageAPI.call("GetAiringEndTime", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Gets the Show object for this Airing which describes it in further detail (Show contains the title, actors, category, description, etc.)

Parameters:
Airing- the Airing object
Returns:
the Show object for this Airing
 */
public static Object GetShow (Object Airing) {
  Object o = sagex.SageAPI.call("GetShow", new Object[] {Airing});
  if (o!=null) return (Object) o;
  return null;
}

/**
Gets the list of the field values which correspond to parental ratings control for this Airing

Parameters:
Airing- the Airing object
Returns:
the list of the field values which correspond to parental ratings control for this Airing
 */
public static java.lang.String[] GetAiringRatings (Object Airing) {
  return (java.lang.String[]) sagex.SageAPI.call("GetAiringRatings", new Object[] {Airing});
}

/**
Get the start time for an airing accounting for any adjustments made due to Manual Recording stop/start time adjustments or adjustments due to favorite padding.

Parameters:
Airing- the Airing object
Returns:
the scheduling end time of the Airing
 */
public static long GetScheduleStartTime (Object Airing) {
  Object o = sagex.SageAPI.call("GetScheduleStartTime", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Get the end time for an airing accounting for any adjustments made due to Manual Recording 
 stop/start time adjustments or adjustments due to favorite padding.

Parameters:
Airing- the Airing object
Returns:
the scheduling end time of the Airing
 */
public static long GetScheduleEndTime (Object Airing) {
  Object o = sagex.SageAPI.call("GetScheduleEndTime", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Get the duration for an airing accounting for any adjustments made due to Manual 
 Recording stop/start time adjustments or adjustments due to favorite padding.

Parameters:
Airing- the Airing object
Returns:
the scheduling duration of the Airing
 */
public static long GetScheduleDuration (Object Airing) {
  Object o = sagex.SageAPI.call("GetScheduleDuration", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
If this Airing is a time-based recording this will get a description of the recurrence frequency for its recording recurrence

Parameters:
Airing- the Airing object
Returns:
a description of the recurrence frequency for this Airing's recording recurrence, or the empty string if this is not recurring time-based recording
 */
public static java.lang.String GetScheduleRecordingRecurrence (Object Airing) {
  Object o = sagex.SageAPI.call("GetScheduleRecordingRecurrence", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns a lengthy string which is suitable for displaying information about this Airing. This contains nearly all the details of the Airing & its Show

Parameters:
Airing- the Airing object
Returns:
a lengthy string which is suitable for displaying information about this Airing
 */
public static java.lang.String PrintAiringLong (Object Airing) {
  Object o = sagex.SageAPI.call("PrintAiringLong", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns a string which is suitable for displaying information about this Airing. This contains the Airing's channel & a short time string as well as the title & episode name or a short description

Parameters:
Airing- the Airing object
Returns:
a string which is suitable for displaying information about this Airing
 */
public static java.lang.String PrintAiringMedium (Object Airing) {
  Object o = sagex.SageAPI.call("PrintAiringMedium", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns a brief string which is suitable for displaying information about this Airing. This contains the Airing's channel & a short time string as well as the title

Parameters:
Airing- the Airing object
Returns:
a brief string which is suitable for displaying information about this Airing
 */
public static java.lang.String PrintAiringShort (Object Airing) {
  Object o = sagex.SageAPI.call("PrintAiringShort", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Gets the title of this Airing. This will be the same as the title of the Airing's Show

Parameters:
Airing- the Airing object
Returns:
the title of this Airing
 */
public static java.lang.String GetAiringTitle (Object Airing) {
  Object o = sagex.SageAPI.call("GetAiringTitle", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns true if this Airing's content has been completely watched already. This may also return true if this Airing itself was not watched; but an Airing with the same content (as determined by SageTV's AI) was watched

Parameters:
Airing- the Airing object
Returns:
true if this Airing's content has beeen watched completely before
 */
public static boolean IsWatched (Object Airing) {
  Object o = sagex.SageAPI.call("IsWatched", new Object[] {Airing});
  if (o!=null) return (Boolean) o;
  return false;
}

/**
Gets the duration of time of this Airing that has been watched already. This time is relative to the Airing itself; not real time.

Parameters:
Airing- the Airing object
Returns:
the duration of time of this Airing that has been watched already in milliseconds
 */
public static long GetWatchedDuration (Object Airing) {
  Object o = sagex.SageAPI.call("GetWatchedDuration", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Gets the time the user started watching this Airing. This time is relative to the Airing itself; not real time.

Parameters:
Airing- the Airing object
Returns:
the time the user started watching this Airing
 */
public static long GetWatchedStartTime (Object Airing) {
  Object o = sagex.SageAPI.call("GetWatchedStartTime", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Gets the time the user finished watching this Airing. This time is relative to the Airing itself; not real time.

Parameters:
Airing- the Airing object
Returns:
the time the user finished watching this Airing
 */
public static long GetWatchedEndTime (Object Airing) {
  Object o = sagex.SageAPI.call("GetWatchedEndTime", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Gets the time the user started watching this Airing, in real time.

Parameters:
Airing- the Airing object
Returns:
the time the user started watching this Airing in real time
Since:
6.4
 */
public static long GetRealWatchedStartTime (Object Airing) {
  Object o = sagex.SageAPI.call("GetRealWatchedStartTime", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Gets the time the user finished watching this Airing, in real time.

Parameters:
Airing- the Airing object
Returns:
the time the user finished watching this Airing
Since:
6.4
 */
public static long GetRealWatchedEndTime (Object Airing) {
  Object o = sagex.SageAPI.call("GetRealWatchedEndTime", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Sets the watched flag for this Airing to true as if the user watched the show from start to finish

Parameters:
Airing- the Airing object
 */
public static void SetWatched (Object Airing) {
   sagex.SageAPI.call("SetWatched", new Object[] {Airing});
}

/**
Clears the watched information for this Airing completely.

Parameters:
Airing- the Airing object
 */
public static void ClearWatched (Object Airing) {
   sagex.SageAPI.call("ClearWatched", new Object[] {Airing});
}

/**
Gets the time that viewing should resume from for this Airing if it is selected to view

Parameters:
Airing- the Airing object
 */
public static long GetLatestWatchedTime (Object Airing) {
  Object o = sagex.SageAPI.call("GetLatestWatchedTime", new Object[] {Airing});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Returns true if this Airing has been completely watched already. This is different thenIsWatched()


Parameters:
Airing- the Airing object
Returns:
true if this Airing has beeen watched completely
 */
public static boolean IsWatchedCompletely (Object Airing) {
  Object o = sagex.SageAPI.call("IsWatchedCompletely", new Object[] {Airing});
  if (o!=null) return (Boolean) o;
  return false;
}

/**
Returns true if this Airing has been set as content the user "Doesn't Like"

Parameters:
Airing- the Airing object
Returns:
true if this Airing has been set as content the user "Doesn't Like"
 */
public static boolean IsDontLike (Object Airing) {
  Object o = sagex.SageAPI.call("IsDontLike", new Object[] {Airing});
  if (o!=null) return (Boolean) o;
  return false;
}

/**
Called to indicate that the content in this Airing is "Not Liked" by the user

Parameters:
Airing- the Airing object
 */
public static void SetDontLike (Object Airing) {
   sagex.SageAPI.call("SetDontLike", new Object[] {Airing});
}

/**
Called to cancel the indication that the content in this Airing is "Not Liked" by the user

Parameters:
Airing- the Airing object
 */
public static void ClearDontLike (Object Airing) {
   sagex.SageAPI.call("ClearDontLike", new Object[] {Airing});
}

/**
Returns true if this Airing has been selected by the user to manually recordRecord()


Parameters:
Airing- the Airing object
Returns:
true if this Airing has been selected by the user to manually recordRecord()
 */
public static boolean IsManualRecord (Object Airing) {
  Object o = sagex.SageAPI.call("IsManualRecord", new Object[] {Airing});
  if (o!=null) return (Boolean) o;
  return false;
}

/**
Returns true if this Airing has NOT been selected by the user to manually recordRecord()
and
 is also NOT a Favorite (i.e. IsFavorite and IsManualRecord both return false)

Parameters:
Airing- the Airing object
Returns:
true if this Airing is not a ManualRecord or a Favorite
Since:
6.2
 */
public static boolean IsNotManualOrFavorite (Object Airing) {
  Object o = sagex.SageAPI.call("IsNotManualOrFavorite", new Object[] {Airing});
  if (o!=null) return (Boolean) o;
  return false;
}

/**
Returns true if this Airing is in HDTV

Parameters:
Airing- the Airing object
Returns:
true if this Airing is in HDTV, false otherwise
 */
public static boolean IsAiringHDTV (Object Airing) {
  Object o = sagex.SageAPI.call("IsAiringHDTV", new Object[] {Airing});
  if (o!=null) return (Boolean) o;
  return false;
}

/**
Returns the track number for the Airing if it's from a Music Album. With music; each song (file) corresponds to an airing.

Parameters:
Airing- the Airing object
Returns:
the track number for the Airing if it's from a Music Album, 0 otherwise
 */
public static int GetTrackNumber (Object Airing) {
  Object o = sagex.SageAPI.call("GetTrackNumber", new Object[] {Airing});
  if (o!=null) return (Integer) o;
  return 0;
}

/**
Returns the recording quality that this Airing has been specifically set to record at. This is only valid for user selected manual recordingsRecord()


Parameters:
Airing- the Airing object
Returns:
the recording quality name that this Airing has been specifically set to record at; if no quality has been set it returns the empty string
 */
public static java.lang.String GetRecordingQuality (Object Airing) {
  Object o = sagex.SageAPI.call("GetRecordingQuality", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Sets the recording quality for this Airing if it has been selected by the user as a manual record

Parameters:
Airing- the Airing object
Quality- the name of the recording quality
 */
public static void SetRecordingQuality (Object Airing, java.lang.String Quality) {
   sagex.SageAPI.call("SetRecordingQuality", new Object[] {Airing,Quality});
}

/**
Returns true if this Airing matches one of the Favorites the user has setup

Parameters:
Airing- the Airing object
Returns:
true if this Airing matches one of the Favorites the user has setup
 */
public static boolean IsFavorite (Object Airing) {
  Object o = sagex.SageAPI.call("IsFavorite", new Object[] {Airing});
  if (o!=null) return (Boolean) o;
  return false;
}

/**
Modifies or creates a time-based recording that is associated with this Airing. This is also a type of Manual Record.

Parameters:
Airing- the Airing object
StartTime- the time the recording of this Airing should start
StopTime- the time the recording of this Airing should stop
Returns:
true if the call succeeds, otherwise a localized error message is returned
 */
public static java.lang.Object SetRecordingTimes (Object Airing, long StartTime, long StopTime) {
  Object o = sagex.SageAPI.call("SetRecordingTimes", new Object[] {Airing,StartTime,StopTime});
  if (o!=null) return (java.lang.Object) o;
  return null;
}

/**
Specifies that this Airing should be recorded. This is a Manul Recording.

Parameters:
Airing- the Airing object
Returns:
true if the call succeeds, otherwise a localized error message is returned
 */
public static java.lang.Object Record (Object Airing) {
  Object o = sagex.SageAPI.call("Record", new Object[] {Airing});
  if (o!=null) return (java.lang.Object) o;
  return null;
}

/**
Cancels a recording that was previously set with a call toRecord()
orSetRecordingTimes()


Parameters:
Airing- the Airing object
 */
public static void CancelRecord (Object Airing) {
   sagex.SageAPI.call("CancelRecord", new Object[] {Airing});
}

/**
Returns true if the argument is an Airing object. Automatic type conversion is NOT done in this call.

Parameters:
Airing- the object to test
Returns:
true if the argument is an Airing object
 */
public static boolean IsAiringObject (Object Airing) {
  Object o = sagex.SageAPI.call("IsAiringObject", new Object[] {Airing});
  if (o!=null) return (Boolean) o;
  return false;
}

/**
Gets the parental rating information associated with this Airing. This is information such as TVY, TVPG, TVMA, etc.

Parameters:
Airing- the Airing object
Returns:
the parental rating information associated with this Airing
 */
public static java.lang.String GetParentalRating (Object Airing) {
  Object o = sagex.SageAPI.call("GetParentalRating", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Gets miscellaneous information about this Airing. This includes thing such as "Part 1 of 2", "CC", "HDTV", "Series Premiere", etc.

Parameters:
Airing- the Airing object
Returns:
miscellaneous information about this Airing
 */
public static java.lang.String GetExtraAiringDetails (Object Airing) {
  Object o = sagex.SageAPI.call("GetExtraAiringDetails", new Object[] {Airing});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns the unique ID used to identify this Airing. Can get used later on a call toGetAiringForID()


Parameters:
Airing- the Airing object
Returns:
the unique ID used to identify this Airing
 */
public static int GetAiringID (Object Airing) {
  Object o = sagex.SageAPI.call("GetAiringID", new Object[] {Airing});
  if (o!=null) return (Integer) o;
  return 0;
}

/**
Returns the Airing object that corresponds to the passed in ID. The ID should have been obtained from a call toGetAiringID()


Parameters:
AiringID- the Airing id
Returns:
the Airing object that corresponds to the passed in ID
 */
public static Object GetAiringForID (int AiringID) {
  Object o = sagex.SageAPI.call("GetAiringForID", new Object[] {AiringID});
  if (o!=null) return (Object) o;
  return null;
}

/**
Adds a new Airing object to the database. This call should be used with caution.

Parameters:
ShowExternalID- a GUID which uniquely identifies the Show that correlates with this Airing, this Show should already have been added
StationID- the GUID which uniquely identifies a "Station" (sort of like a Channel)
StartTime- the time at which the new Airing starts
Duration- the duration of the new Airing in milliseconds
Returns:
the newly added Airing
 */
public static Object AddAiring (java.lang.String ShowExternalID, int StationID, long StartTime, long Duration) {
  Object o = sagex.SageAPI.call("AddAiring", new Object[] {ShowExternalID,StationID,StartTime,Duration});
  if (o!=null) return (Object) o;
  return null;
}

/**
Adds a new Airing object to the database. This call should be used with caution. (it has more details you can specify than the
 standard AddAiring API call)

Parameters:
ShowExternalID- a GUID which uniquely identifies the Show that correlates with this Airing, this Show should already have been added
StationID- the GUID which uniquely identifies a "Station" (sort of like a Channel)
StartTime- the time at which the new Airing starts
Duration- the duration of the new Airing in milliseconds
PartNumber- for music files, the track number; for TV shows if it is a multipart show this is the part number, otherwise this should be 0
TotalParts- for multipart TV shows, this is the total number of parts otherwise this should be zero; for music files it should be zero
ParentalRating- the parental rating for the show, should be a localized value from "TVY", "TVY7", "TVG", "TVPG", "TV14", "TVM" or the empty string
HDTV- true if it's an HDTV airing, false otherwise
Stereo- true if it's a stereo recording, false otherwise
ClosedCaptioning- true if the airing has closed captioning, false otherwise
SAP- true if the Airing has a Secondary Audio Program (SAP), false otherwise
Subtitled- true if the Airing is subtitled, false otherwise
PremierFinale- should be the empty string or a localized value from the list "Premier", "Channel Premier", "Season Premier", "Series Premier", "Season Finale", "Series Finale"
Returns:
the newly added Airing
Since:
5.0
 */
public static Object AddAiringDetailed (java.lang.String ShowExternalID, int StationID, long StartTime, long Duration, int PartNumber, int TotalParts, java.lang.String ParentalRating, boolean HDTV, boolean Stereo, boolean ClosedCaptioning, boolean SAP, boolean Subtitled, java.lang.String PremierFinale) {
  Object o = sagex.SageAPI.call("AddAiringDetailed", new Object[] {ShowExternalID,StationID,StartTime,Duration,PartNumber,TotalParts,ParentalRating,HDTV,Stereo,ClosedCaptioning,SAP,Subtitled,PremierFinale});
  if (o!=null) return (Object) o;
  return null;
}

/**
Gets the MediaFile object which corresponds to this Airing object

Parameters:
Airing- the Airing object
Returns:
the MediaFile object which corresponds to this Airing object, or null if it has no associated MediaFile
 */
public static Object GetMediaFileForAiring (Object Airing) {
  Object o = sagex.SageAPI.call("GetMediaFileForAiring", new Object[] {Airing});
  if (o!=null) return (Object) o;
  return null;
}

/**
Returns the Airing on the same Channel that is on immediately after the passed in Airing

Parameters:
Airing- the Airing object
Returns:
the Airing on the same Channel that is on immediately after the passed in Airing
 */
public static Object GetAiringOnAfter (Object Airing) {
  Object o = sagex.SageAPI.call("GetAiringOnAfter", new Object[] {Airing});
  if (o!=null) return (Object) o;
  return null;
}

/**
Returns the Airing on the same Channel that is on immediately before the passed in Airing

Parameters:
Airing- the Airing object
Returns:
the Airing on the same Channel that is on immediately before the passed in Airing
 */
public static Object GetAiringOnBefore (Object Airing) {
  Object o = sagex.SageAPI.call("GetAiringOnBefore", new Object[] {Airing});
  if (o!=null) return (Object) o;
  return null;
}




/** Convenience method for setting this thread's UI Context
@see sagex.SageAPI.setUIConext()*/
public static void setUIContext(String context) {
   sagex.SageAPI.setUIContext(context);
}
}
