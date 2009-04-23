package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 21/04/09 11:27 PM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/MediaPlayerAPI.html'>MediaPlayerAPI</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public class MediaPlayerAPI {
/**
Returns true if the MediaPlayer is fully loaded. This means it has the meta information for the
 file loaded as well as the native media player.

Returns:
true if the MediaPlayer is fully loaded, false otherwise
 */
public static boolean IsMediaPlayerFullyLoaded () {
   return (Boolean) sagex.SageAPI.call("IsMediaPlayerFullyLoaded", (Object[])null);
}

/**
Returns true if the MediaPlayer is loading. This is true from the point that a Watch() API call
 is made until the point that the native media player is loaded or there is a failure loading the file.

Returns:
true if the MediaPlayer is loading, false otherwise
 */
public static boolean IsMediaPlayerLoading () {
   return (Boolean) sagex.SageAPI.call("IsMediaPlayerLoading", (Object[])null);
}

/**
Sets the playback rate of the MediaPlayer to be twice the current playback rate. Not supported
 on all platforms or with all media formats.
 */
public static void PlayFaster () {
    sagex.SageAPI.call("PlayFaster", (Object[])null);
}

/**
Performs a time seek forward in the MediaPlayer. The amount of time skipped will be equivalent
 to the value of the property videoframe/ff_time in milliseconds. (the default is 10 seconds)
 */
public static void SkipForward () {
    sagex.SageAPI.call("SkipForward", (Object[])null);
}

/**
Performs a time seek forward in the MediaPlayer. The amount of time skipped will be equivalent
 to the value of the property videoframe/ff_time2 in milliseconds. (the default is 2 1/2 minutes)
 */
public static void SkipForward2 () {
    sagex.SageAPI.call("SkipForward2", (Object[])null);
}

/**
Performs a time seek in the MediaPlayer to the specified time. This time is relative to the
 start time of the metadata for the MediaFile unless a DVD is being played back. In the case of a DVD
 the time is absolute.

Parameters:
Time- the time to seek the MediaPlayer to in milliseconds
 */
public static void Seek (long Time) {
    sagex.SageAPI.call("Seek", new Object[] {Time});
}

/**
Pauses playback in the MediaPlayer. If the MediaPlayer is currently paused this will perform a frame step.
 */
public static void Pause () {
    sagex.SageAPI.call("Pause", (Object[])null);
}

/**
Resumes playback in the MediaPlayer. If the MediaPlayer is playing at a speed other than x1, the playback speed will be
 reset to x1.
 */
public static void Play () {
    sagex.SageAPI.call("Play", (Object[])null);
}

/**
Sets the playback rate of the MediaPlayer to be half the current playback rate. Not supported
 on all platforms or with all media formats.
 */
public static void PlaySlower () {
    sagex.SageAPI.call("PlaySlower", (Object[])null);
}

/**
Pauses playback of the MediaPlayer if it is currently playing or resumes playback of the MediaPlayer
 if it is currently paused.
 */
public static void PlayPause () {
    sagex.SageAPI.call("PlayPause", (Object[])null);
}

/**
Performs a time seek backwards in the MediaPlayer. The amount of time skipped will be equivalent
 to the value of the property videoframe/rew_time in milliseconds. (the default is 10 seconds)
 */
public static void SkipBackwards () {
    sagex.SageAPI.call("SkipBackwards", (Object[])null);
}

/**
Performs a time seek backwards in the MediaPlayer. The amount of time skipped will be equivalent
 to the value of the property videoframe/rew_time2 in milliseconds. (the default is 2 1/2 minutes)
 */
public static void SkipBackwards2 () {
    sagex.SageAPI.call("SkipBackwards2", (Object[])null);
}

/**
Returns the current playback rate as a floating point number. 1.0 is normal speed forward playback.
 Negative numbers indicate reverse playback.

Returns:
the current playback rate of the MediaPlayer
 */
public static float GetPlaybackRate () {
   return (Float) sagex.SageAPI.call("GetPlaybackRate", (Object[])null);
}

/**
Sets the playback rate of the MediaPlayer to the specified value. 1.0 is normal speed forward playback.
 Negative numbers indicate reverse playback. Not all values are supported on all platforms or for all formats.

Parameters:
PlaybackRate- the playback rate to set the MediaPlayer to
 */
public static void SetPlaybackRate (float PlaybackRate) {
    sagex.SageAPI.call("SetPlaybackRate", new Object[] {PlaybackRate});
}

/**
Increases the volume in the MediaPlayer. This may also effect the 'system' volume depending upon the
 configuration of SageTV.
 */
public static void VolumeUp () {
    sagex.SageAPI.call("VolumeUp", (Object[])null);
}

/**
Decreases the volume in the MediaPlayer. This may also effect the 'system' volume depending upon the
 configuration of SageTV.
 */
public static void VolumeDown () {
    sagex.SageAPI.call("VolumeDown", (Object[])null);
}

/**
Returns the current volume level of the MediaPlayer. If no MediaPlayer is loaded this will return
 the system volume.

Returns:
the current volume level of the MediaPlayer; if no MediaPlayer is loaded this will return
         the system volume. The value will be between 0.0 and 1.0
 */
public static float GetVolume () {
   return (Float) sagex.SageAPI.call("GetVolume", (Object[])null);
}

/**
Performs a logical channel up in the MediaPlayer. This only has effect if the content that is
 currently being viewed has the concept of channels, tracks, chapters, etc.
 */
public static void ChannelUp () {
    sagex.SageAPI.call("ChannelUp", (Object[])null);
}

/**
Performs a logical channel down in the MediaPlayer. This only has effect if the content that is
 currently being viewed has the concept of channels, tracks, chapters, etc.
 */
public static void ChannelDown () {
    sagex.SageAPI.call("ChannelDown", (Object[])null);
}

/**
Performs a logical channel set in the MediaPlayer. This only has effect if the content that is
 currently being viewed has the concept of channels, tracks, chapters, etc.

Parameters:
ChannelNumber- the new channel/track/chapter to playback
 */
public static void ChannelSet (java.lang.String ChannelNumber) {
    sagex.SageAPI.call("ChannelSet", new Object[] {ChannelNumber});
}

/**
Adjusts the volume in the MediaPlayer be the specified amount. The overall volume for the
 player is between 0.0 and 1.0. This may also effect the 'system' volume depending upon the
 configuration of SageTV.

Parameters:
Amount- the amount to adjust the volume by
 */
public static void VolumeAdjust (float Amount) {
    sagex.SageAPI.call("VolumeAdjust", new Object[] {Amount});
}

/**
Explicitly sets the volume in the MediaPlayer to be the specified amount. This should be between 0.0 and 1.0
 This may also effect the 'system' volume depending upon the configuration of SageTV.

Parameters:
Amount- the level to set the volume to
 */
public static void SetVolume (float Amount) {
    sagex.SageAPI.call("SetVolume", new Object[] {Amount});
}

/**
Instructs SageTV to playback the specified media content. The argument can be either an
 Airing, a MediaFile or a file path.  For Airings, it can correspond to a MediaFile (which has the
 same effect as just calling this with the MediaFile itself) or it can correspond to a live
 television Airing. For live TV airings, the appropriate work will be done to tune, record and start
 playback of the requested content.  For MediaFiles or file paths, this will simply playback the specified content.

Parameters:
Content- the Airing, MediaFile or file path to being playback of
Returns:
true if the request was successful, a localized error message otherwise
 */
public static java.lang.Object Watch (java.lang.Object Content) {
   return (java.lang.Object) sagex.SageAPI.call("Watch", new Object[] {Content});
}

/**
Instructs SageTV to begin playback of content streamed from the specified CaptureDeviceInput. The content
 may or may not be buffered to a file first depending upon the PauseBufferSize parameter as well as the
 capabilities of the capture hardware and the network configuration.  NOTE: This is NOT the same as jumping
 to live when playing back buffered TV content; to do that simply call Seek(Time())

Parameters:
CaptureDeviceInput- the capture input to playback content directly from
PauseBufferSize- the size in bytes of the buffer SageTV should use to buffer the content for playback, this will
         also allow pausing of this stream upto the size of the PauseBuffer; use 0 to request no buffering
         (although SageTV may still decide to use buffering if it deems it necessary)
Returns:
true if the request was successful, a localized error message otherwise
 */
public static java.lang.Object WatchLive (java.lang.String CaptureDeviceInput, long PauseBufferSize) {
   return (java.lang.Object) sagex.SageAPI.call("WatchLive", new Object[] {CaptureDeviceInput,PauseBufferSize});
}

/**
Instructs SageTV to playback the specified file path that's local to this client

Parameters:
file- path to playback
Returns:
true if the request was successful, a localized error message otherwise
Since:
6.4
 */
public static java.lang.Object WatchLocalFile (java.io.File file) {
   return (java.lang.Object) sagex.SageAPI.call("WatchLocalFile", new Object[] {file});
}

/**
Starts playback of the specified Playlist. The MediaPlayer will playback everything in the Playlist
 sequentially until it is done.

Parameters:
Playlist- the Playlist to being playback of
 */
public static void StartPlaylist (Object Playlist) {
    sagex.SageAPI.call("StartPlaylist", new Object[] {Playlist});
}

/**
Starts playback of the specified Playlist. The MediaPlayer will playback everything in the Playlist
 sequentially until it is done. Playback will begin at the item at the specified by the passed in index.

Parameters:
Playlist- the Playlist to being playback of
StartIndex- the index in the playlist to start playing at (1-based index)
 */
public static void StartPlaylistAt (Object Playlist, int StartIndex) {
    sagex.SageAPI.call("StartPlaylistAt", new Object[] {Playlist,StartIndex});
}

/**
Closes the file that is currently loaded by the MediaPlayer and waits for the MediaPlayer to
 completely free all of its resources before returning.
 */
public static void CloseAndWaitUntilClosed () {
    sagex.SageAPI.call("CloseAndWaitUntilClosed", (Object[])null);
}

/**
Returns true if the MediaPlayer is currently in a muted state. This will not affect the system volume.

Returns:
true if the MediaPlayer is muted, false otherwise
 */
public static boolean IsMuted () {
   return (Boolean) sagex.SageAPI.call("IsMuted", (Object[])null);
}

/**
Sets the mute state for the MediaPlayer. This does not affect the system volume.

Parameters:
Muted- true if the MediaPlayer should be muted, false otherwise
 */
public static void SetMute (boolean Muted) {
    sagex.SageAPI.call("SetMute", new Object[] {Muted});
}

/**
Returns the title of the content that is currently loaded by the MediaPlayer.

Returns:
the title of the content that is currently loaded by the MediaPlayer
 */
public static java.lang.String GetCurrentMediaTitle () {
   return (java.lang.String) sagex.SageAPI.call("GetCurrentMediaTitle", (Object[])null);
}

/**
Gets the current playback time of the MediaPlayer. For DVD content this time will return a value appropriate for
 a current time display (starting at zero). For all other content types, this value will be the time in java.lang.System.currentTimeMillis() units
 and is relative to the start time of the Airing metadata which represents the currently loaded file. So for a current time display you
 should subtract the airing start time of the current media file from the returned value.

Returns:
the current playback time of the MediaPlayer in milliseconds
 */
public static long GetMediaTime () {
   return (Long) sagex.SageAPI.call("GetMediaTime", (Object[])null);
}

/**
Returns the duration of the currently loaded MediaFile in milliseconds.

Returns:
the duration of the currently loaded MediaFile in milliseconds
 */
public static long GetMediaDuration () {
   return (Long) sagex.SageAPI.call("GetMediaDuration", (Object[])null);
}

/**
Returns the MediaFile object that is currently loaded (or loading) by the MediaPlayer

Returns:
the MediaFile object that is currently loaded (or loading) by the MediaPlayer
 */
public static Object GetCurrentMediaFile () {
   return (Object) sagex.SageAPI.call("GetCurrentMediaFile", (Object[])null);
}

/**
Returns true if the MediaPlayer currently has a file that is loading or loaded.

Returns:
true if the MediaPlayer currently has a file that is loading or loaded, false otherwise
 */
public static boolean HasMediaFile () {
   return (Boolean) sagex.SageAPI.call("HasMediaFile", (Object[])null);
}

/**
Returns true if the MediaPlayer has a file loading or loaded that has video content in it

Returns:
true if the MediaPlayer has a file loading or loaded that has video content in it, false otherwise
 */
public static boolean DoesCurrentMediaFileHaveVideo () {
   return (Boolean) sagex.SageAPI.call("DoesCurrentMediaFileHaveVideo", (Object[])null);
}

/**
Returns true if the MediaPlayer has a file loading or loaded, and that file is a music file

Returns:
true if the MediaPlayer has a file loading or loaded, and that file is a music file
 */
public static boolean IsCurrentMediaFileMusic () {
   return (Boolean) sagex.SageAPI.call("IsCurrentMediaFileMusic", (Object[])null);
}

/**
Returns true if the MediaPlayer has a file loading or loaded, and that file is a DVD

Returns:
true if the MediaPlayer has a file loading or loaded, and that file is a DVD
 */
public static boolean IsCurrentMediaFileDVD () {
   return (Boolean) sagex.SageAPI.call("IsCurrentMediaFileDVD", (Object[])null);
}

/**
Returns true if the MediaPlayer has a file loading or loaded, and that file is currently being recorded

Returns:
true if the MediaPlayer has a file loading or loaded, and that file is currently being recorded
 */
public static boolean IsCurrentMediaFileRecording () {
   return (Boolean) sagex.SageAPI.call("IsCurrentMediaFileRecording", (Object[])null);
}

/**
Returns true if the MediaPlayer is currently playing back content (i.e. content is fully loaded and not in the paused state)

Returns:
true if the MediaPlayer is currently playing back content, false otherwise
 */
public static boolean IsPlaying () {
   return (Boolean) sagex.SageAPI.call("IsPlaying", (Object[])null);
}

/**
Returns true if the MediaPlayer currently has DVD content loaded and that content is showing a DVD menu that can have user interaction

Returns:
true if the MediaPlayer currently has DVD content loaded and that content is showing a DVD menu that can have user interaction, false otherwise
 */
public static boolean IsShowingDVDMenu () {
   return (Boolean) sagex.SageAPI.call("IsShowingDVDMenu", (Object[])null);
}

/**
Returns the current Playlist that is being played back by the MediaPlayer. Playlists can be played back
 using the callStartPlaylist()


Returns:
the current Playlist that is being played back by the MediaPlayer, null otherwise
 */
public static Object GetCurrentPlaylist () {
   return (Object) sagex.SageAPI.call("GetCurrentPlaylist", (Object[])null);
}

/**
Returns the 0-based index into the root Playlist that is currently being played back by the MediaPlayer. 0 is returned
 if no Playlist is currently being played back.

Returns:
the 0-based index into the root Playlist that is currently being played back by the MediaPlayer. 0 is returned
           if no Playlist is currently being played back.
 */
public static int GetCurrentPlaylistIndex () {
   return (Integer) sagex.SageAPI.call("GetCurrentPlaylistIndex", (Object[])null);
}

/**
Gets the earliest time that the current media can be seeked to using theSeek()
call. This
 will be in absolute time.

Returns:
the earliest time that the current media can be seeked to in milliseconds
 */
public static long GetAvailableSeekingStart () {
   return (Long) sagex.SageAPI.call("GetAvailableSeekingStart", (Object[])null);
}

/**
Gets the latest time that the current media can be seeked to using theSeek()
call. This
 will be in absolute time.

Returns:
the latest time that the current media can be seeked to in milliseconds
 */
public static long GetAvailableSeekingEnd () {
   return (Long) sagex.SageAPI.call("GetAvailableSeekingEnd", (Object[])null);
}

/**
Returns true if the argument passed in matches the parental lock code in the system

Parameters:
ParentalLockCode- the code to test
Returns:
true if the specified ParentalLockCode matches the parental lock code SageTV is configured to use
 */
public static boolean IsCorrectParentalLockCode (java.lang.String ParentalLockCode) {
   return (Boolean) sagex.SageAPI.call("IsCorrectParentalLockCode", new Object[] {ParentalLockCode});
}

/**
Sets the video portion of SageTV to always be on top of other windows in the desktop (Windows only).

Parameters:
OnTop- true if the video window of SageTV should be on top of all other windows in the system, false otherwise
 */
public static void SetVideoAlwaysOnTop (boolean OnTop) {
    sagex.SageAPI.call("SetVideoAlwaysOnTop", new Object[] {OnTop});
}

/**
Informs the MediaPlayer to start playback of the next chapter in the current DVD content.
 */
public static void DVDChapterNext () {
    sagex.SageAPI.call("DVDChapterNext", (Object[])null);
}

/**
Informs the MediaPlayer to start playback of the previous chapter in the current DVD content.
 */
public static void DVDChapterPrevious () {
    sagex.SageAPI.call("DVDChapterPrevious", (Object[])null);
}

/**
Informs the MediaPlayer to start playback of the specified chapter in the current DVD content.

Parameters:
ChapterNumber- the chapter number to start playback of in the current DVD
 */
public static void DVDChapterSet (int ChapterNumber) {
    sagex.SageAPI.call("DVDChapterSet", new Object[] {ChapterNumber});
}

/**
Performs the 'Enter' operation when using a menu system in DVD content.
 */
public static void DVDEnter () {
    sagex.SageAPI.call("DVDEnter", (Object[])null);
}

/**
Performs the 'Menu' operation when playing back a DVD which should bring up the root menu of the DVD
 */
public static void DVDMenu () {
    sagex.SageAPI.call("DVDMenu", (Object[])null);
}

/**
Performs the 'Menu' operation when playing back a DVD which should bring up the title menu of the DVD
 */
public static void DVDTitleMenu () {
    sagex.SageAPI.call("DVDTitleMenu", (Object[])null);
}

/**
Performs the 'Return' operation when playing back a DVD which should bring the user back to the last DVD menu they were at
 */
public static void DVDReturn () {
    sagex.SageAPI.call("DVDReturn", (Object[])null);
}

/**
Informs the MediaPlayer to start playback of the next title in the current DVD content.
 */
public static void DVDTitleNext () {
    sagex.SageAPI.call("DVDTitleNext", (Object[])null);
}

/**
Informs the MediaPlayer to start playback of the previous title in the current DVD content.
 */
public static void DVDTitlePrevious () {
    sagex.SageAPI.call("DVDTitlePrevious", (Object[])null);
}

/**
Toggles the state for subtitle display in the DVD content being played back.
 */
public static void DVDSubtitleToggle () {
    sagex.SageAPI.call("DVDSubtitleToggle", (Object[])null);
}

/**
Sets the subtitle that should be displayed in the current DVD content. The names of the languages for the
 corresponding subtitles are obtained from a call toGetDVDAvailableSubpictures()
.
 If no arguments are given to this function then the currently displayed subtitle will be changed to the next one

Parameters:
SubtitleNum- the 0-based index into the list of subtitles that should be displayed
 */
public static void DVDSubtitleChange (int SubtitleNum) {
    sagex.SageAPI.call("DVDSubtitleChange", new Object[] {SubtitleNum});
}

/**
Sets the audio language that should be used in the current DVD content. The names of the languages
 are obtained from a call toGetDVDAvailableLanguages()
.
 If no arguments are given to this function then the current audio language will be changed to the next available language

Parameters:
AudioNum- the 0-based index into the list of audio languages that should be used
 */
public static void DVDAudioChange (int AudioNum) {
    sagex.SageAPI.call("DVDAudioChange", new Object[] {AudioNum});
}

/**
Sets the 'Angle' for playback of the current DVD content. The number of angels
 are obtained from a call toGetDVDNumberOfAngles()
.
 If no arguments are given to this function then the current angle will be changed to the next available angle

Parameters:
AngleNum- the 1-based index that indicates which angle should be used for playback
 */
public static void DVDAngleChange (int AngleNum) {
    sagex.SageAPI.call("DVDAngleChange", new Object[] {AngleNum});
}

/**
Reloads the current file that is loaded by the MediaPlayer. This is useful when changing configuration options
 for the MediaPlayer and then showing playback with those changes.
 */
public static void ReloadCurrentFile () {
    sagex.SageAPI.call("ReloadCurrentFile", (Object[])null);
}

/**
Submits an explicit playback control request to the MediaPlayer if it supports it. (Only DVD-based media players support this call)

Parameters:
Code- this is the value of the control command to be sent to the player, must be one of the following:
        MENU = 201; Param1 should be 1 for title, 2 for root
        TITLE_SET = 202; Param1 should be the title number
        CHAPTER_SET = 205; Param1 should be the chapter number
        CHAPTER_NEXT = 206;
        CHAPTER_PREV = 207;
        ACTIVATE_CURRENT = 208;
        RETURN = 209;
        BUTTON_NAV = 210; Param1 should be 1(up), 2(right), 3(down) or 4(left)
        MOUSE_HOVER = 211; Param1 should be x and Param2 should be y
        MOUSE_CLICK = 212; Param1 should be x and Param2 should be y
        ANGLE_CHANGE = 213; Param1 should be the angle number (1-based)
        SUBTITLE_CHANGE = 214; Param1 should be the subtitle number (0-based)
        SUBTITLE_TOGGLE = 215;
        AUDIO_CHANGE = 216; Param1 should be the audio number (0-based)
Param1- the first parameter for the control command (see above)
Param2- the second parameter for the control command (see above)
 */
public static void DirectPlaybackControl (int Code, long Param1, long Param2) {
    sagex.SageAPI.call("DirectPlaybackControl", new Object[] {Code,Param1,Param2});
}

/**
Gets the current title number that is being played back for DVD content.

Returns:
the current title number that is being played back for DVD content, 0 otherwise
 */
public static int GetDVDCurrentTitle () {
   return (Integer) sagex.SageAPI.call("GetDVDCurrentTitle", (Object[])null);
}

/**
Gets the total number of titles in the current DVD content

Returns:
the total number of titles in the current DVD content
 */
public static int GetDVDNumberOfTitles () {
   return (Integer) sagex.SageAPI.call("GetDVDNumberOfTitles", (Object[])null);
}

/**
Gets the current chapter number that is being played back for DVD content.

Returns:
the current chapter number that is being played back for DVD content
 */
public static int GetDVDCurrentChapter () {
   return (Integer) sagex.SageAPI.call("GetDVDCurrentChapter", (Object[])null);
}

/**
Gets the total number of chapters in the current title in the current DVD content

Returns:
the total number of chapters in the current title in the current DVD content
 */
public static int GetDVDNumberOfChapters () {
   return (Integer) sagex.SageAPI.call("GetDVDNumberOfChapters", (Object[])null);
}

/**
Gets the current 'domain' that the DVD playback is in.

Returns:
the current 'domain' that the DVD playback is in, uses the following values:
         1 = DVD initialization, 2 = disc menus, 3 = title menus, 4 = playback, 5 = stopped
 */
public static int GetDVDCurrentDomain () {
   return (Integer) sagex.SageAPI.call("GetDVDCurrentDomain", (Object[])null);
}

/**
Gets the current angle number that is being played back for DVD content

Returns:
the current angle number that is being played back for DVD content
 */
public static int GetDVDCurrentAngle () {
   return (Integer) sagex.SageAPI.call("GetDVDCurrentAngle", (Object[])null);
}

/**
Gets the total number of angles that are currently available to select from in the current DVD content

Returns:
the total number of angles that are currently available to select from in the current DVD content
 */
public static int GetDVDNumberOfAngles () {
   return (Integer) sagex.SageAPI.call("GetDVDNumberOfAngles", (Object[])null);
}

/**
Gets the current audio playback language that is being used for the current DVD content

Returns:
the current audio playback language that is being used for the current DVD content
 */
public static java.lang.String GetDVDCurrentLanguage () {
   return (java.lang.String) sagex.SageAPI.call("GetDVDCurrentLanguage", (Object[])null);
}

/**
Gets a list of all of the audio languages that are currently available in the current DVD content

Returns:
a list of all of the audio languages that are currently available in the current DVD content
 */
public static java.lang.String[] GetDVDAvailableLanguages () {
   return (java.lang.String[]) sagex.SageAPI.call("GetDVDAvailableLanguages", (Object[])null);
}

/**
Gets the current subtitle that is being used for the current DVD content

Returns:
the current subtitle that is being used for the current DVD content, null if subtitles are currently disabled
 */
public static java.lang.String[] GetDVDCurrentSubpicture () {
   return (java.lang.String[]) sagex.SageAPI.call("GetDVDCurrentSubpicture", (Object[])null);
}

/**
Gets a list of all of the subtitles that are currently available in the current DVD content

Returns:
a list of all of the subtitles that are currently available in the current DVD content
 */
public static java.lang.String[] GetDVDAvailableSubpictures () {
   return (java.lang.String[]) sagex.SageAPI.call("GetDVDAvailableSubpictures", (Object[])null);
}

/**
Gets the current state that MediaPlayer close captioning is set to use. This can be either a localized
 version of "Captions Off" or one of the strings: "CC1", "CC2", "Text1", "Text2"

Returns:
the current state that MediaPlayer close captioning is set to use
 */
public static java.lang.String GetMediaPlayerClosedCaptionState () {
   return (java.lang.String) sagex.SageAPI.call("GetMediaPlayerClosedCaptionState", (Object[])null);
}

/**
Sets the current state that MediaPlayer close captioning should use. This can be one of the strings: "CC1", "CC2", "Text1", "Text2".
 If any other value is used then closed captioning will be turned off.

Parameters:
CCType- the new state that MediaPlayer close captioning should use
 */
public static void SetMediaPlayerClosedCaptionState (java.lang.String CCType) {
    sagex.SageAPI.call("SetMediaPlayerClosedCaptionState", new Object[] {CCType});
}

/**
Returns true if the source the MediaPlayer is trying to playback from indicates a signal loss.
 This can happen when trying to watch digital TV stations.

Returns:
true if the MediaPlayer detects signal loss from the source it's playing back, false otherwise
 */
public static boolean IsMediaPlayerSignalLost () {
   return (Boolean) sagex.SageAPI.call("IsMediaPlayerSignalLost", (Object[])null);
}

/**
Returns an image which is a frame grab of the currently rendered video frame. This is currently only
 supported on Windows when using VMR9 with 3D acceleration

Returns:
a java.awt.image.BufferedImage which holds the last rendered video frame, or null if the call cannot be completed
 */
public static java.awt.image.BufferedImage GetVideoSnapshot () {
   return (java.awt.image.BufferedImage) sagex.SageAPI.call("GetVideoSnapshot", (Object[])null);
}




/** Convenience method for setting this thread's UI Context
@see sagex.SageAPI.setUIConext()*/
public static void setUIContext(String context) {
   sagex.SageAPI.setUIContext(context);
}
}
