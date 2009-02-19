package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 17/02/09 7:36 PM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/Global.html'>Global</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public class Global {
/**
Re-evaluates and redraws all UI elements on the current menu
 */
public static void Refresh () {
    sagex.SageAPI.call("Refresh", (Object[])null);
}

/**
Finds the Widget on the current menu who's name matches the argument and then re-evaluates and redraws all UI elements, and their children for this Widget

Parameters:
WidgetName- the name that a UI component's Widget must match, if null or zero length, then this is the same as callingRefresh()
 */
public static void RefreshArea (java.lang.String WidgetName) {
    sagex.SageAPI.call("RefreshArea", new Object[] {WidgetName});
}

/**
Redraws all UI elements on the current menu
 */
public static void Repaint () {
    sagex.SageAPI.call("Repaint", (Object[])null);
}

/**
Finds the Widget on the current menu who's name matches the argument and then redraws all UI elements, and their children for this Widget

Parameters:
WidgetName- the name that a UI component's Widget must match, if null or zero length, then this is the same as callingRepaint()
 */
public static void RepaintArea (java.lang.String WidgetName) {
    sagex.SageAPI.call("RepaintArea", new Object[] {WidgetName});
}

/**
Sets the variable with the specified name to the specified value. This variable will 
 exist for the lifetime of the current Menu. When the next Menu transition occurs, 
 all values in the static context will be copied to the highest level context for the new Menu. 
 The static context is then cleared. This is the way you can pass an Object from one menu to 
 another, an example is showing the detailed info for an Airing by adding the Airing to the 
 static context and then transitioning to a Menu that will display the details for that Airing.

Parameters:
Name- the name to use for this 'static context' variable
Value- the value to set this 'static context' variable to
 */
public static java.lang.Object AddStaticContext (java.lang.String Name, java.lang.Object Value) {
   return (java.lang.Object) sagex.SageAPI.call("AddStaticContext", new Object[] {Name,Value});
}

/**
Sets the variable with the specified name to the specified value. This variable 
 will exist for the lifetime of the SageTV application and is always in scope for all expression evaluation. This is a 'global variable'.

Parameters:
Name- the name to use for this 'global context' variable
Value- the value to set this 'global context' variable to
 */
public static java.lang.Object AddGlobalContext (java.lang.String Name, java.lang.Object Value) {
   return (java.lang.Object) sagex.SageAPI.call("AddGlobalContext", new Object[] {Name,Value});
}

/**
Returns whether or not there are unresolved scheduling conflicts

Returns:
true if there are unresolved scheduling conflicts, false otherwise
 */
public static boolean AreThereUnresolvedConflicts () {
   return (Boolean) sagex.SageAPI.call("AreThereUnresolvedConflicts", (Object[])null);
}

/**
Returns true if SageTV is in the Sleep state.

Returns:
true if SageTV is in the Sleep state
 */
public static boolean IsAsleep () {
   return (Boolean) sagex.SageAPI.call("IsAsleep", (Object[])null);
}

/**
Gets the total diskspace available for television recording by SageTV. This is the unused space in the video directories.

Returns:
the total diskspace available for television recording by SageTV (in bytes)
 */
public static long GetTotalDiskspaceAvailable () {
   return (Long) sagex.SageAPI.call("GetTotalDiskspaceAvailable", (Object[])null);
}

/**
Returns the total duration of all of the content in the media library

Returns:
the total duration of all of the content in the media library in milliseconds
 */
public static long GetTotalLibraryDuration () {
   return (Long) sagex.SageAPI.call("GetTotalLibraryDuration", (Object[])null);
}

/**
Returns the total duration of all of the recorded television content

Returns:
the total duration of all of the recorded television content in milliseconds
 */
public static long GetTotalVideoDuration () {
   return (Long) sagex.SageAPI.call("GetTotalVideoDuration", (Object[])null);
}

/**
Returns the total number of bytes on disk used by the content in the imported video, picturees and music libraries

Returns:
the total number of bytes on disk used by the content in the imported video, picturees and music libraries
 */
public static long GetUsedLibraryDiskspace () {
   return (Long) sagex.SageAPI.call("GetUsedLibraryDiskspace", (Object[])null);
}

/**
Returns the total number of bytes on disk used by recorded television content

Returns:
the total number of bytes on disk used by recorded television content
 */
public static long GetUsedVideoDiskspace () {
   return (Long) sagex.SageAPI.call("GetUsedVideoDiskspace", (Object[])null);
}

/**
Returns true if two Airings represent the same Show. This means that they both are the same content (i.e. one is a repeat of the other)
 Just because two Airings use the same Show object (in terms of object reference) doesn't mean this call will return true.

Parameters:
Airing1- one of the two Airing objects to compare
Airing2- the other Airing object to compare
Returns:
true if two Airings represent the same Show, false otherwise
 */
public static boolean AreAiringsSameShow (Object Airing1, Object Airing2) {
   return (Boolean) sagex.SageAPI.call("AreAiringsSameShow", new Object[] {Airing1,Airing2});
}

/**
Returns the last time that SageTV did an EPG update. The returned value uses the same units as java.lang.System.currentTimeMillis()

Returns:
the last time that SageTV did an EPG update
 */
public static long GetLastEPGDownloadTime () {
   return (Long) sagex.SageAPI.call("GetLastEPGDownloadTime", (Object[])null);
}

/**
Gets an Image object that represents the specified name. This is normally used for Channel Logos.

Parameters:
LogoName- the name to use to lookup the logo image
Returns:
the image object that corresponds to the specified name if one exists, the null image otherwise
 */
public static Object GetLogo (java.lang.String LogoName) {
   return (Object) sagex.SageAPI.call("GetLogo", new Object[] {LogoName});
}

/**
The amount of time in milliseconds until SageTV will perform an EPG update again. Zero if no update is planned.

Returns:
the amount of time in milliseconds until SageTV will perform an EPG update again. Zero if no update is planned
 */
public static long GetTimeUntilNextEPGDownload () {
   return (Long) sagex.SageAPI.call("GetTimeUntilNextEPGDownload", (Object[])null);
}

/**
Returns the name of all the EPG lineups in the system

Returns:
a list of all the names of the EPG lineups in the system
 */
public static java.lang.String[] GetAllLineups () {
   return (java.lang.String[]) sagex.SageAPI.call("GetAllLineups", (Object[])null);
}

/**
Returns true if the channel download has been completed on the specified lineup

Parameters:
Lineup- the name of the Lineup
Returns:
true if the channel download has been completed on the specified lineup, false otherwise
 */
public static boolean IsChannelDownloadComplete (java.lang.String Lineup) {
   return (Boolean) sagex.SageAPI.call("IsChannelDownloadComplete", new Object[] {Lineup});
}

/**
Gets a list of all the possible United States local broadcast markets from the EPG server

Returns:
a list of all the possible United States local broadcast markets from the EPG server
 */
public static java.lang.String[] GetLocalMarketsFromEPGServer () {
   return (java.lang.String[]) sagex.SageAPI.call("GetLocalMarketsFromEPGServer", (Object[])null);
}

/**
Gets a list from the EPG server of all the possible EPG Lineups that are available in a given zip code

Parameters:
ZipCode- the zip code to search for EPG lineups in
Returns:
a list from the EPG server of all the possible EPG lineups in the specified zip code
 */
public static java.lang.String[] GetLineupsForZipCodeFromEPGServer (java.lang.String ZipCode) {
   return (java.lang.String[]) sagex.SageAPI.call("GetLineupsForZipCodeFromEPGServer", new Object[] {ZipCode});
}

/**
Returns a list of all of the files that SageTV is currently recording

Returns:
a list of all of the files that SageTV is currently recording
 */
public static Object[] GetCurrentlyRecordingMediaFiles () {
   return (Object[]) sagex.SageAPI.call("GetCurrentlyRecordingMediaFiles", (Object[])null);
}

/**
Gets a list of all of the Airings that SageTV would record if Intelligent Recording was enabled.
 This does not include Manual Recordings or Favorites.

Returns:
a list of all of the Airings that SageTV would record if Intelligent Recording was enabled.
Since:
4.1
 */
public static Object[] GetSuggestedIntelligentRecordings () {
   return (Object[]) sagex.SageAPI.call("GetSuggestedIntelligentRecordings", (Object[])null);
}

/**
Gets a list of all of the Airings that SageTV is planning to record in the future

Returns:
a list of all of the Airings that SageTV is planning to record in the future
 */
public static Object[] GetScheduledRecordings () {
   return (Object[]) sagex.SageAPI.call("GetScheduledRecordings", (Object[])null);
}

/**
Gets a list of all of the Airings that SageTV is planning to record in the future on the specified CaptureDevice

Parameters:
CaptureDevice- the name of a CaptureDevice for SageTV to get the scheduled recordings for
Returns:
a list of all of the Airings that SageTV is planning to record in the future on the specified CaptureDevice
 */
public static Object[] GetScheduledRecordingsForDevice (java.lang.String CaptureDevice) {
   return (Object[]) sagex.SageAPI.call("GetScheduledRecordingsForDevice", new Object[] {CaptureDevice});
}

/**
Gets a list of all of the Airings that SageTV is planning on recording during the specified time span

Parameters:
StartTime- the starting time to get all of the scheduled recordings for
StopTime- the ending time to get all of the scheduled recordings for
Returns:
a list of all of the Airings that SageTV is planning on recording during the specified start-stop time window
 */
public static Object[] GetScheduledRecordingsForTime (long StartTime, long StopTime) {
   return (Object[]) sagex.SageAPI.call("GetScheduledRecordingsForTime", new Object[] {StartTime,StopTime});
}

/**
Gets a list of all of the Airings that SageTV is planning on recording during the specified time span on a specified CaptureDevice

Parameters:
CaptureDevice- the name of a CaptureDevice for SageTV to get the scheduled recordings for
StartTime- the starting time to get all of the scheduled recordings for
StopTime- the ending time to get all of the scheduled recordings for
Returns:
a list of all of the Airings that SageTV is planning on recording during the specified start-stop time window on the specified CaptureDevice
 */
public static Object[] GetScheduledRecordingsForDeviceForTime (java.lang.String CaptureDevice, long StartTime, long StopTime) {
   return (Object[]) sagex.SageAPI.call("GetScheduledRecordingsForDeviceForTime", new Object[] {CaptureDevice,StartTime,StopTime});
}

/**
Gets a list of all of the Airings that have been watched within the specified amount of time

Parameters:
DurationToLookBack- tha amount of time in milliseconds that should be searched for watched Airings
Returns:
a list of all of the Airings that have been watched within the specified amount of time
 */
public static Object[] GetRecentlyWatched (long DurationToLookBack) {
   return (Object[]) sagex.SageAPI.call("GetRecentlyWatched", new Object[] {DurationToLookBack});
}

/**
Tells SageTV to run a library import scan now. This will scan all of the library import directories for new content.

Parameters:
WaitUntilDone- if this parameter is true then this call will not return until SageTV has finished the import scan, otherwise it returns immediately
 */
public static void RunLibraryImportScan (boolean WaitUntilDone) {
    sagex.SageAPI.call("RunLibraryImportScan", new Object[] {WaitUntilDone});
}

/**
Causes the SageTV application to terminate. If this is called from the non-primary UI session then it will terminate the UI session it is called from.
 */
public static void Exit () {
    sagex.SageAPI.call("Exit", (Object[])null);
}

/**
Causes the corresponding Sage Command to be executed just like it would be if the user performed it. 
 These are always done asynchronously with the exception of "Back" and "Forward" since those effect 
 the current UI and may be used to override a menu transition.

Parameters:
Command- the name of the Sage Command to execute
 */
public static void SageCommand (java.lang.String Command) {
    sagex.SageAPI.call("SageCommand", new Object[] {Command});
}

/**
Removes all of the lineups from SageTV's configuration that are no longer in use by a CaptureDevice

Returns:
true if any lineups were removed, false otherwise
 */
public static boolean RemoveUnusedLineups () {
   return (Boolean) sagex.SageAPI.call("RemoveUnusedLineups", (Object[])null);
}

/**
Returns the time that the SageTV application was instantiated.

Returns:
the time that the SageTV application was instantiated
 */
public static long GetApplicationLaunchTime () {
   return (Long) sagex.SageAPI.call("GetApplicationLaunchTime", (Object[])null);
}

/**
Copies all variables from the currently focused UI element's variable context to the current context 
 of the calling Action. Any variables that are in a common parent hierarchy are not copied as part of this.

Returns:
true if there was a focused element and it's context was copied, false if there was no focused element found
 */
public static boolean GetFocusContext () {
   return (Boolean) sagex.SageAPI.call("GetFocusContext", (Object[])null);
}

/**
Spawns a new thread of execution that will be used for further processing of Widget chain. This is analagous to 'forking' a thread
 to continue execution of a widget chain in parallel to the current system execution.
 */
public static void Fork () {
    sagex.SageAPI.call("Fork", (Object[])null);
}

/**
Causes SageTV to instruct the specified tuning plugin to send a command

Parameters:
TuningPlugin- the name of the tuning plugin that should send the command
TuningPluginPort- the name of the port the specified tuning plugin is on
RemoteName- the name of the 'Remote Control' that should be used to send the command
CommandName- the name of the command to be sent
RepeatFactor- the 'repeat factor' to use for sending the infrared command, 2 is the default
 */
public static void TransmitCommandUsingInfraredTuningPlugin (java.lang.String TuningPlugin, int TuningPluginPort, java.lang.String RemoteName, java.lang.String CommandName, int RepeatFactor) {
    sagex.SageAPI.call("TransmitCommandUsingInfraredTuningPlugin", new Object[] {TuningPlugin,TuningPluginPort,RemoteName,CommandName,RepeatFactor});
}

/**
Prints out a message to SageTV's debug log

Parameters:
DebugString- the string to print out
 */
public static void DebugLog (java.lang.String DebugString) {
    sagex.SageAPI.call("DebugLog", new Object[] {DebugString});
}

/**
Closes the last OptionsMenu that was shown and continues execution of the Action 
 chain that spawned that OptionsMenu at the sibling after this OptionsMenu. The 
 feature of continuing execution can be used to prompt the user with a question 
 through an OptionsMenu before continuing on execution of an Action chain.  
 An example is confirming something they just did before it actually gets done.
 */
public static void CloseOptionsMenu () {
    sagex.SageAPI.call("CloseOptionsMenu", (Object[])null);
}

/**
Gets the names of all of the SageTV commands that are available in the system

Returns:
a list of the names of all of the SageTV commands that are available in the system
 */
public static java.util.Vector GetSageCommandNames () {
   return (java.util.Vector) sagex.SageAPI.call("GetSageCommandNames", (Object[])null);
}

/**
Applies a service level to a given lineup. This is specific to the EPG data source that is being used.

Parameters:
Lineup- the name of the EPG lineup to modify
ServiceLevel- the service level to apply to the lineup
 */
public static void ApplyServiceLevelToLineup (java.lang.String Lineup, int ServiceLevel) {
    sagex.SageAPI.call("ApplyServiceLevelToLineup", new Object[] {Lineup,ServiceLevel});
}

/**
Searches all UI elements in the current menu until it finds one that has a variable with the specified name 
 matching the specified value. Once it finds this UI element, it gives it the focus.  If scrolling of a table 
 or panel is necessary to make it visible, that scrolling will occur.

Parameters:
Name- the name of the variable to match
Value- the value of the variable to match on
Returns:
true if the focus was set
 */
public static boolean SetFocusForVariable (java.lang.String Name, java.lang.Object Value) {
   return (Boolean) sagex.SageAPI.call("SetFocusForVariable", new Object[] {Name,Value});
}

/**
Searches the tables in the current menu for a cell that matches the passed in name/value pair; 
 if found it will ensure that it is currently visible in the UI at the specified visual index of the table. 
 The name should match the name of the TableComponent-Cell. For example if the TableComponent with a Cell 
 subtype had a name of "File" and listed all of the MediaFiles; then calling 
 EnsureVisibilityForVariable("File", MyMediaFile, 1) would cause the table to scroll so that the second row 
 showed the cell whose File value corresponded to MyMediaFile.  This does NOT change the focus, 
 if you wish to scroll a table and shift the focus then use SetFocusForVariable

Parameters:
Name- the name of the variable to match on
Value- the value of the variable to match
DisplayIndex- 0-based value signifying which visually displayed row or column should show the corresonding cell
Returns:
true if there was a matching variable found
 */
public static boolean EnsureVisibilityForVariable (java.lang.String Name, java.lang.Object Value, int DisplayIndex) {
   return (Boolean) sagex.SageAPI.call("EnsureVisibilityForVariable", new Object[] {Name,Value,DisplayIndex});
}

/**
Should only be used in the Action chain from a Listener Widget. Normally SageTV will stop processing Listeners 
 for an event once the first one is reached. If you use PassiveListen() in the action chain for a 
 Listener then SageTV will not stop processing at the current Listener.
 */
public static void PassiveListen () {
    sagex.SageAPI.call("PassiveListen", (Object[])null);
}

/**
Returns all of the Airings that the user has requested to record that SageTV will not be recording. This would be due to
 scheduling conflicts.

Parameters:
OnlyUnresolved- if true then only unresolved scheduling conflicts will be returned, if false then all conflicts will be returned
Returns:
the list of Airings that will not be recorded due to scheduling conflicts
 */
public static java.util.Vector GetAiringsThatWontBeRecorded (boolean OnlyUnresolved) {
   return (java.util.Vector) sagex.SageAPI.call("GetAiringsThatWontBeRecorded", new Object[] {OnlyUnresolved});
}

/**
Returns true if this is an instance of SageTV Client

Returns:
true if this is an instance of SageTV Client, false otherwise
 */
public static boolean IsClient () {
   return (Boolean) sagex.SageAPI.call("IsClient", (Object[])null);
}

/**
Returns true if this UI is being remoted onto another device such as a media extender. This also returns
 true for Placeshifter clients.

Returns:
true if this UI is being remoted onto another device such as a media extender, false otherwise
Since:
4.1
 */
public static boolean IsRemoteUI () {
   return (Boolean) sagex.SageAPI.call("IsRemoteUI", (Object[])null);
}

/**
Returns true if this UI is being run in a desktop environment. It may or may not be remoted. This is
 intended to distinguish Placeshifter clients from Media Extenders since both return true for IsRemoteUI().

Returns:
true if this UI is being run in a desktop environment, false otherwise
Since:
4.1.10
 */
public static boolean IsDesktopUI () {
   return (Boolean) sagex.SageAPI.call("IsDesktopUI", (Object[])null);
}

/**
Returns true if this UI is being run on the same system as the SageTV Server it's connected to.
 Also returns true for standalone SageTV applications. This indicates the UI should have the ability
 to configure all 'server' based options. For SageTVClient, you must be connected to the loopback address
 for this to return true.

Returns:
true if this is the main UI for a SageTV Server
Since:
5.0.5
 */
public static boolean IsServerUI () {
   return (Boolean) sagex.SageAPI.call("IsServerUI", (Object[])null);
}

/**
Returns a list of all the clients that are currently connected to this server.

Returns:
a list of all the clients that are currently connected to this server.
 */
public static java.lang.String[] GetConnectedClients () {
   return (java.lang.String[]) sagex.SageAPI.call("GetConnectedClients", (Object[])null);
}

/**
Returns a list of the names of the different UI contexts that are available. One will be the local context
 which is the UI for the SageTV app (which doesn't exist in service mode). The other contexts can be UIs that
 are for remote devices such as media extenders. This context name can then be passed back in using
 the sage.SageTV.apiUI(String uiContextName, String methodName, Object[] args) call in order to execute an API call
 from Java within a specific UI context.  For media extenders, these names generally correspond to the MAC address
 of the media extender.

Returns:
a list of the UI context names available
Since:
4.1
 */
public static java.lang.String[] GetUIContextNames () {
   return (java.lang.String[]) sagex.SageAPI.call("GetUIContextNames", (Object[])null);
}

/**
Returns the name of the UI context that makes the API call. SeeGetUIContextNames()
for more information.

Returns:
the UI context name that made this API call, null if there is no UI context
Since:
5.1
 */
public static java.lang.String GetUIContextName () {
   return (java.lang.String) sagex.SageAPI.call("GetUIContextName", (Object[])null);
}

/**
Returns the version string for this connected remote client

Returns:
the version string for this connected remote client, or the empty string if it's undefined or not a remote client
Since:
6.4
 */
public static java.lang.String GetRemoteClientVersion () {
   return (java.lang.String) sagex.SageAPI.call("GetRemoteClientVersion", (Object[])null);
}

/**
Returns the type of client that is connected on this remote interface

Returns:
the type of client that is connected on this remote interface; this can be one of "SD Media Extender", "HD Media Extender", "Placeshifter" or "Local"
Since:
6.4
 */
public static java.lang.String GetRemoteUIType () {
   return (java.lang.String) sagex.SageAPI.call("GetRemoteUIType", (Object[])null);
}

/**
Creates a new time based recording for SageTV.

Parameters:
Channel- the Channel object that this recording should be performed on
StartTime- the time the recording should begin
StopTime- the time the recording should end
Recurrence- the name of the recurrence to use; this can be either Once, Daily or Weekly 
               (or a localized version of one of those); OR it can be a combination of any of the following strings to indicate specified 
                days: Su, Mo, Tu, We, Th, Fr, Sa (for example, MoTuWe to do Mondays, Tuesdays and Wednesdays)
Returns:
true if the creation of the timed recording succeeded, otherwise a localized error string is returned
 */
public static java.lang.Object CreateTimedRecording (Object Channel, long StartTime, long StopTime, java.lang.String Recurrence) {
   return (java.lang.Object) sagex.SageAPI.call("CreateTimedRecording", new Object[] {Channel,StartTime,StopTime,Recurrence});
}

/**
Returns whether or not SageTV is in full screen mode

Returns:
true if SageTV is in full screen mode, false otherwise
 */
public static boolean IsFullScreen () {
   return (Boolean) sagex.SageAPI.call("IsFullScreen", (Object[])null);
}

/**
Sets SageTV to be in full or non-full screen mode

Parameters:
FullScreen- true if SageTV should be put into full screen mode, false if it should be put into windowed mode
 */
public static void SetFullScreen (boolean FullScreen) {
    sagex.SageAPI.call("SetFullScreen", new Object[] {FullScreen});
}

/**
Gets the hostname of the SageTV server if this is a client, otherwise it returns the name of the host SageTV is running on

Returns:
the hostname of the SageTV server if this is a client, otherwise it returns the name of the host SageTV is running on
 */
public static java.lang.String GetServerAddress () {
   return (java.lang.String) sagex.SageAPI.call("GetServerAddress", (Object[])null);
}

/**
Gets the name of the operating system that is being used.

Returns:
the name of the operating system that is being used
 */
public static java.lang.String GetOS () {
   return (java.lang.String) sagex.SageAPI.call("GetOS", (Object[])null);
}

/**
Returns true if SageTV is currently running on a Windows operating system.

Returns:
true if SageTV is currently running on a Windows operating system, false otherwise
 */
public static boolean IsWindowsOS () {
   return (Boolean) sagex.SageAPI.call("IsWindowsOS", (Object[])null);
}

/**
Returns true if SageTV is currently running on a Linux operating system.

Returns:
true if SageTV is currently running on a Linux operating system, false otherwise
Since:
6.0.20
 */
public static boolean IsLinuxOS () {
   return (Boolean) sagex.SageAPI.call("IsLinuxOS", (Object[])null);
}

/**
Returns true if SageTV is currently running on a Macintosh operating system.

Returns:
true if SageTV is currently running on a Macintosh operating system, false otherwise
Since:
6.0.20
 */
public static boolean IsMacOS () {
   return (Boolean) sagex.SageAPI.call("IsMacOS", (Object[])null);
}

/**
Instructs the DVD burning engine inside SageTV to start prepping the specified Playlist to be burned to a DVD, and then perform the actual burn.
 NOTE: Currently this is only supported on the Linux operating system

Parameters:
BurnList- the Playlist of all of the video files to burn to the DVD
Returns:
true if the burn process was successfully started, otherwise a localized error message string is returned
 */
public static java.lang.Object DVDBurnTheBurnList (Object BurnList) {
   return (java.lang.Object) sagex.SageAPI.call("DVDBurnTheBurnList", new Object[] {BurnList});
}

/**
Cancels a previous request that was made to perform DVD burning.
 */
public static void DVDCancelBurn () {
    sagex.SageAPI.call("DVDCancelBurn", (Object[])null);
}

/**
Gets the current status of a previously invoked DVD burning process.

Returns:
true if the DVD burning process is completed and was a success, "Error" if it completed and was a failure, 
          otherwise a localized status message indicating progress is returned
 */
public static java.lang.Object DVDGetCurrentBurnStatus () {
   return (java.lang.Object) sagex.SageAPI.call("DVDGetCurrentBurnStatus", (Object[])null);
}

/**
Instructs the CD burning engine inside SageTV to start prepping the specified Playlist to be burned to a CD, and then perform the actual burn.
 NOTE: Currently this is only supported on the Linux operating system

Parameters:
BurnList- the Playlist of all of the music files to burn to the CD
Returns:
true if the burn process was successfully started, otherwise a localized error message string is returned
 */
public static java.lang.Object CDBurnTheBurnList (Object BurnList) {
   return (java.lang.Object) sagex.SageAPI.call("CDBurnTheBurnList", new Object[] {BurnList});
}

/**
Cancels a previous request that was made to perform CD burning.
 */
public static void CDCancelBurn () {
    sagex.SageAPI.call("CDCancelBurn", (Object[])null);
}

/**
Gets the current status of a previously invoked CD burning process.

Returns:
true if the CD burning process is completed and was a success, "Error" if it completed and was a failure, 
          otherwise a localized status message indicating progress is returned
 */
public static java.lang.Object CDGetCurrentBurnStatus () {
   return (java.lang.Object) sagex.SageAPI.call("CDGetCurrentBurnStatus", (Object[])null);
}

/**
Instructs the CD ripping engine to rip the contents of a CD and encode it in MP3 format and then store it in the music library.
 NOTE: This is currently only supported on the Linux operating system

Parameters:
LibraryDir- the directory that the ripped files should be stored in, if the space on this disk is managed by SageTV it will make room for the files that are to be ripped
BitrateKbps- the bitrate to use for the audio encoding in kilobits per second
Returns:
true if the ripping process was successfully started, otherwise a localized error message string is returned
 */
public static java.lang.Object CDRipToLibrary (java.io.File LibraryDir, java.lang.String BitrateKbps) {
   return (java.lang.Object) sagex.SageAPI.call("CDRipToLibrary", new Object[] {LibraryDir,BitrateKbps});
}

/**
Cancels a previous request that was made to perform CD ripping
 */
public static void CDCancelRip () {
    sagex.SageAPI.call("CDCancelRip", (Object[])null);
}

/**
Gets the current status of a previously invoked CD ripping process.

Returns:
true if the CD ripping process is completed and was a success, "Error" if it completed and was a failure, 
          otherwise a localized status message indicating progress is returned
 */
public static java.lang.Object CDGetCurrentRipStatus () {
   return (java.lang.Object) sagex.SageAPI.call("CDGetCurrentRipStatus", (Object[])null);
}

/**
Instructs the file transfer engine to copy the specified file(s) from the source directory to the destination directory.
 If there is no filename specified then the contents of the directory are copied recursively. If the destination directory
 is within the path of SageTV managed diskspace then the appropriate free space will be cleared on the disk in order for the file copy to succeed.
 This also works for uploading files from a SageTV client to a SageTV server.

Parameters:
Filename- the name of the file in the SourceDirectory to copy, or null if the whole directory should be copied
SourceDirectory- the source directory for the file copy (smb:// paths are OK)
DestDirectory- the destination directory for the file copy
Returns:
true if the copy process was successfully started, false if the file copy is unable to be performed
 */
public static java.lang.Object StartFileCopy (java.lang.String Filename, java.lang.String SourceDirectory, java.io.File DestDirectory) {
   return (java.lang.Object) sagex.SageAPI.call("StartFileCopy", new Object[] {Filename,SourceDirectory,DestDirectory});
}

/**
Cancels a previous request that was made to perform a file copy
 */
public static void CancelFileCopy () {
    sagex.SageAPI.call("CancelFileCopy", (Object[])null);
}

/**
Gets the current status of a previously invoked file copy process.

Returns:
true if the file copy process is completed and was a success, "Error" will be the prefix if it was a failure, 
          otherwise a localized status message indicating progress is returned
 */
public static java.lang.Object GetFileCopyStatus () {
   return (java.lang.Object) sagex.SageAPI.call("GetFileCopyStatus", (Object[])null);
}

/**
Instructs the file transfer engine to download the specified file from the server to the local destination file. You may also
 download from remote http:// or ftp:// addresses; in that case just specify the URL in the ServerAddress argument and leave sourceFile as null.
 When downloading from http or ftp addresses, the target will be the server's filesystem for remote clients; otherwise it is the local filesystem.
 When smb:// URLs are specified; they will be access from the server's network for remote clients, otherwise the source will be from the local network.
 smb:// URLs target download will be the local filesystem.

Parameters:
ServerAddress- the address of the SageTV server to download from, or null if you're using SageTVClient and you want to download from the server you're connected to, or a valid smb, http or ftp URL
SourceFile- the file path on the server you want to download
DestFile- the destination file for the file download
Returns:
true if the copy process was successfully started, false if the file doesn't exist on the server or it couldn't be contacted
 */
public static boolean StartFileDownload (java.lang.String ServerAddress, java.lang.String SourceFile, java.io.File DestFile) {
   return (Boolean) sagex.SageAPI.call("StartFileDownload", new Object[] {ServerAddress,SourceFile,DestFile});
}

/**
Instructs the file transfer engine to download the specified file from the server to the local destination file. You may also
 download from remote http:// or ftp:// addresses; in that case just specify the URL in the ServerAddress argument and leave sourceFile as null.
 When downloading from http or ftp addresses, the target will be the server's filesystem for remote clients; otherwise it is the local filesystem.
 When smb:// URLs are specified; they will be access from the server's network for remote clients, otherwise the source will be from the local network.
 smb:// URLs target download will be the local filesystem.
 The 'Circular' version of this API call will write to a temporary circular file; this is designed for systems with limited storage capacity.
 This version of the API call may not be used by SageTVClient (if it is; then it will internally switch to the non-circular file method)

Parameters:
ServerAddress- the address of the SageTV server to download from, or null if you're using SageTVClient and you want to download from the server you're connected to, or a valid smb, http or ftp URL
SourceFile- the file path on the server you want to download
DestFile- the destination file for the file download
Returns:
true if the copy process was successfully started, false if the file doesn't exist on the server or it couldn't be contacted
Since:
6.4
 */
public static boolean StartCircularFileDownload (java.lang.String ServerAddress, java.lang.String SourceFile, java.io.File DestFile) {
   return (Boolean) sagex.SageAPI.call("StartCircularFileDownload", new Object[] {ServerAddress,SourceFile,DestFile});
}

/**
Cancels a previous request that was made to perform a file download
 */
public static void CancelFileDownload () {
    sagex.SageAPI.call("CancelFileDownload", (Object[])null);
}

/**
Gets the current status of a previously invoked file download process.

Returns:
true if the file download process is completed and was a success, "Error" will be the prefix if it was a failure, 
          otherwise a localized status message indicating progress is returned
 */
public static java.lang.Object GetFileDownloadStatus () {
   return (java.lang.Object) sagex.SageAPI.call("GetFileDownloadStatus", (Object[])null);
}

/**
This is used to enable/disable encryption on the event channel for the SageTV MiniClient. The MiniClient is used
 for the media extenders and for placeshifting. When using a non-local MiniClient connection, or if local MiniClient
 connections are configured to require authentication; the event channel between the two will start off in an encrypted
 mode so that a password can be exchanged.  This API call can then be used with a false argument to disable encryption
 of the event channel in order to increase performance. It can also be used to re-enable encryption at a later time if
 sensitive information is going to be transmitted again.

Parameters:
EnableEncryption- true if the MiniClient event channel should start encrypting events, false if it should stop
Returns:
true if the MiniClient supports encryption and the operation should succeed, the connection will terminate if it fails
Since:
4.1.7
 */
public static boolean SetRemoteEventEncryptionEnabled (boolean EnableEncryption) {
   return (Boolean) sagex.SageAPI.call("SetRemoteEventEncryptionEnabled", new Object[] {EnableEncryption});
}

/**
This is a Windows only API call which tells SageTV to unload and then reload any system hooks it has installed.
 This allows reconfiguration of parameters in the UI and then realization of those changes on the fly.

Since:
4.1.13
 */
public static void ReloadSystemHooks () {
    sagex.SageAPI.call("ReloadSystemHooks", (Object[])null);
}

/**
This API call can be used to tell SageTV to do an update with the Locator server right now.
 It's useful for when you change external network configuration stuff like ports or IPs.

Since:
4.1.13
 */
public static void UpdateLocatorServer () {
    sagex.SageAPI.call("UpdateLocatorServer", (Object[])null);
}

/**
Returns the width in pixels of the user interface for the calling SageTV UI context.

Returns:
the width in pixels of the user interface for the calling SageTV UI context
Since:
5.1
 */
public static int GetFullUIWidth () {
   return (Integer) sagex.SageAPI.call("GetFullUIWidth", (Object[])null);
}

/**
Returns the height in pixels of the user interface for the calling SageTV UI context.

Returns:
the height in pixels of the user interface for the calling SageTV UI context
Since:
5.1
 */
public static int GetFullUIHeight () {
   return (Integer) sagex.SageAPI.call("GetFullUIHeight", (Object[])null);
}

/**
Returns the width in pixels of the current display resolution set

Returns:
the width in pixels of the current display resolution set
Since:
5.1
 */
public static int GetDisplayResolutionWidth () {
   return (Integer) sagex.SageAPI.call("GetDisplayResolutionWidth", (Object[])null);
}

/**
Returns the height in pixels of the current display resolution set

Returns:
the height in pixels of the current display resolution set
Since:
5.1
 */
public static int GetDisplayResolutionHeight () {
   return (Integer) sagex.SageAPI.call("GetDisplayResolutionHeight", (Object[])null);
}

/**
Returns a string describing the current display resolution set

Returns:
a string describing the current display resolution set
Since:
6.3
 */
public static java.lang.String GetDisplayResolution () {
   return (java.lang.String) sagex.SageAPI.call("GetDisplayResolution", (Object[])null);
}

/**
Returns a list of the possible display resolutions. This is currently only used
 on media extender devices that have adjustable output resolution

Returns:
an array of the display resolution names that can be used, null if this change is not supported
Since:
6.0
 */
public static java.lang.String[] GetDisplayResolutionOptions () {
   return (java.lang.String[]) sagex.SageAPI.call("GetDisplayResolutionOptions", (Object[])null);
}

/**
Returns a list of the preferred display resolutions. This is currently only used
 on media extender devices that have adjustable output resolution. This information is obtained from the HDMI/DVI connector

Returns:
a list of the preferred display resolutions
Since:
6.3
 */
public static java.lang.String[] GetPreferredDisplayResolutions () {
   return (java.lang.String[]) sagex.SageAPI.call("GetPreferredDisplayResolutions", (Object[])null);
}

/**
Sets the current output display resolution. This is currently only used
 on media extender devices that have adjustable output resolution.

Parameters:
Resolution- this must be a value fromGetDisplayResolutionOptions()
and should be the new desired output resolution
Since:
6.0
 */
public static void SetDisplayResolution (java.lang.String Resolution) {
    sagex.SageAPI.call("SetDisplayResolution", new Object[] {Resolution});
}

/**
Returns a list of the SageTV servers on the network. Each item will be "name;IP address"

Parameters:
Timeout- the timeout for the discovery process in milliseconds
Returns:
an array of the SageTV servers on the network. Each item will be "name;IP address"
Since:
6.3
 */
public static java.lang.String[] DiscoverSageTVServers (long Timeout) {
   return (java.lang.String[]) sagex.SageAPI.call("DiscoverSageTVServers", new Object[] {Timeout});
}

/**
Gets a java.awt.Panel which can be used for embedding Java UI elements directly into SageTV

Returns:
a java.awt.Panel object which can be sized, made visible and have children added to it
 */
public static java.awt.Panel GetEmbeddedPanel () {
   return (java.awt.Panel) sagex.SageAPI.call("GetEmbeddedPanel", (Object[])null);
}

/**
Implemented on Linux only
 */
public static java.lang.String GetAvailableUpdate () {
   return (java.lang.String) sagex.SageAPI.call("GetAvailableUpdate", (Object[])null);
}

/**
Implemented on Linux only
 */
public static void DeployAvailableUpdate (java.lang.String AvailableUpdate) {
    sagex.SageAPI.call("DeployAvailableUpdate", new Object[] {AvailableUpdate});
}

/**
The single lineup for tvtv.

Parameters:
Languages- language, this parameter is currently ignored.
Returns:
the standard tvtv provider id, { "601" }
 */
public static java.lang.String[] GetLineupsForTvtv (java.lang.String Languages) {
   return (java.lang.String[]) sagex.SageAPI.call("GetLineupsForTvtv", new Object[] {Languages});
}

/**
User selected host/language.

Returns:
the tvtv host,lang; default is "www.tvtv.de,de"
 */
public static java.lang.String TvtvGetHost () {
   return (java.lang.String) sagex.SageAPI.call("TvtvGetHost", (Object[])null);
}

/**
Valid host/language list from www.tvtv.de, for SageTV.
 User should select one.

Returns:
all valid host/language
 */
public static java.lang.String[] TvtvGetHostList () {
   return (java.lang.String[]) sagex.SageAPI.call("TvtvGetHostList", (Object[])null);
}

/**
Deprecated. 

Launch default browser to tvtvHost.
 User can create an account, and then proceed to activate a Trial.
 */
public static void TvtvCreateNewAccount () {
    sagex.SageAPI.call("TvtvCreateNewAccount", (Object[])null);
}

/**
Returns:
username
 */
public static java.lang.String TvtvGetUser () {
   return (java.lang.String) sagex.SageAPI.call("TvtvGetUser", (Object[])null);
}

/**
Establish user configuration.

Parameters:
Username- (as used during TvtvCreateNewAccount)
Password- (stored in encrypted form)
Host- (one of TvtvGetHostList)
 */
public static void TvtvConfigureUser (java.lang.String Username, java.lang.String Password, java.lang.String Host) {
    sagex.SageAPI.call("TvtvConfigureUser", new Object[] {Username,Password,Host});
}

/**
Returns:
account status:
   OK
   INVALID:USER (invalid username or password)
   INVALID:TVTV (an unknown TvTv error)
   ERROR (communication error, network error)
 */
public static java.lang.String TvtvGetAccountStatus () {
   return (java.lang.String) sagex.SageAPI.call("TvtvGetAccountStatus", (Object[])null);
}

/**
Returns:
a message about account status
 */
public static java.lang.String TvtvGetAccountStatusMessage () {
   return (java.lang.String) sagex.SageAPI.call("TvtvGetAccountStatusMessage", (Object[])null);
}

/**
Gather TvTv user messages.

Parameters:
serial- null (to access the first message) OR serial value from last invocation
Returns:
String[] { serial, date, subject, message }
 */
public static java.lang.String[] TvtvMessage (java.lang.String serial) {
   return (java.lang.String[]) sagex.SageAPI.call("TvtvMessage", new Object[] {serial});
}

/**
Launch a browser in response to user click on TvTv logo.
 */
public static void TvtvLogoLink () {
    sagex.SageAPI.call("TvtvLogoLink", (Object[])null);
}

/**
TvtvCreateSpecificAccount.

Since:
4.1
 */
public static void TvtvCreateSpecificAccount () {
    sagex.SageAPI.call("TvtvCreateSpecificAccount", (Object[])null);
}

/**
Deprecated. 

Activate TvTv trial, if available, for the current user configuration.
 */
public static void TvtvActivateTrial () {
    sagex.SageAPI.call("TvtvActivateTrial", (Object[])null);
}

/**
Begin downloading stations.

Parameters:
CaptureDeviceInput-
 */
public static void TvtvDownloadStations (java.lang.String CaptureDeviceInput) {
    sagex.SageAPI.call("TvtvDownloadStations", new Object[] {CaptureDeviceInput});
}

/**
Deprecated. Use TvtvConfigureUser
 */
public static boolean TvtvConfigureInput (java.lang.String CaptureDeviceInput, java.lang.String Username, java.lang.String Password) {
   return (Boolean) sagex.SageAPI.call("TvtvConfigureInput", new Object[] {CaptureDeviceInput,Username,Password});
}




/** Convenience method for setting this thread's UI Context
@see sagex.SageAPI.setUIConext()*/
public static void setUIContext(String context) {
   sagex.SageAPI.setUIContext(context);
}
}
