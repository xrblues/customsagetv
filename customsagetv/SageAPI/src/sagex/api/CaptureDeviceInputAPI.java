package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 29/03/09 6:31 PM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/CaptureDeviceInputAPI.html'>CaptureDeviceInputAPI</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public class CaptureDeviceInputAPI {
/**
Gets the name of the tuning plugin used for this CaptureDeviceInput

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the name of the tuning plugin currently used for the specified CaptureDeviceInput
 */
public static java.lang.String GetInfraredTuningPlugin (java.lang.String CaptureDeviceInput) {
   return (java.lang.String) sagex.SageAPI.call("GetInfraredTuningPlugin", new Object[] {CaptureDeviceInput});
}

/**
Gets the port number used by the tuning plugin for this CaptureDeviceInput

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the port number of the tuning plugin currently used for the specified CaptureDeviceInput
 */
public static int GetInfraredTuningPluginPortNumber (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetInfraredTuningPluginPortNumber", new Object[] {CaptureDeviceInput});
}

/**
Sets the name and port number for the tuning plugin for a CaptureDeviceInput

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
PluginName- the name of the tuning plugin to use on the specified CaptureDeviceInput. This should be a value fromGetInfraredTuningPlugins()
Use the emptry string "" to set the CaptureDeviceInput to not use a plugin
PluginPortNumber- the port number to configure the specified tuning plugin to use on the specified CaptureDeviceInput. Use 0 for the USB port.
Returns:
true if the plugin was setup (if it requires hardware this validates the hardware is connected)
 */
public static boolean SetInfraredTuningPluginAndPort (java.lang.String CaptureDeviceInput, java.lang.String PluginName, int PluginPortNumber) {
   return (Boolean) sagex.SageAPI.call("SetInfraredTuningPluginAndPort", new Object[] {CaptureDeviceInput,PluginName,PluginPortNumber});
}

/**
Tunes the CaptureDeviceInput to the specified physical channel and indicates whether or not a signal is present. This call should only
 be used if the CaptureDeviceInput is already under live control (i.e.WatchLive()
was called on it) or
 if the input has not been configured for use yet. Otherwise this call may interfere with what is currently being recorded.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
ChannelNumber- the channel string to tune to
Returns:
true if the hardware detected a signal on the specified channel
 */
public static boolean AutoTuneChannelTest (java.lang.String CaptureDeviceInput, java.lang.String ChannelNumber) {
   return (Boolean) sagex.SageAPI.call("AutoTuneChannelTest", new Object[] {CaptureDeviceInput,ChannelNumber});
}

/**
Tunes the CaptureDeviceInput to the specified physical channel and returns a list of the available channels. This call should only
 be used if the CaptureDeviceInput is already under live control (i.e.WatchLive()
was called on it) or
 if the input has not been configured for use yet. Otherwise this call may interfere with what is currently being recorded.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
ChannelNumber- the channel string to tune to
Returns:
true if the hardware detected a signal on the specified channel
 */
public static java.lang.String AutoScanChannelInfo (java.lang.String CaptureDeviceInput, java.lang.String ChannelNumber) {
   return (java.lang.String) sagex.SageAPI.call("AutoScanChannelInfo", new Object[] {CaptureDeviceInput,ChannelNumber});
}

/**
Returns the minimum channel number that this CaptureDeviceInput can tune to

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the minimum channel number for the specified CaptureDeviceInput
 */
public static int GetInputMinimumChannelNumber (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetInputMinimumChannelNumber", new Object[] {CaptureDeviceInput});
}

/**
Returns the maximum channel number that this CaptureDeviceInput can tune to

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the maximum channel number for the specified CaptureDeviceInput
 */
public static int GetInputMaximumChannelNumber (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetInputMaximumChannelNumber", new Object[] {CaptureDeviceInput});
}

/**
Sets the brightness for capture on this CaptureDeviceInput. This only affects analog capture devices

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Value- the new value to set the brightness to, in the inclusive range 0-255. Use -1 to set it to the default.
 */
public static void SetCaptureBrightness (java.lang.String CaptureDeviceInput, int Value) {
    sagex.SageAPI.call("SetCaptureBrightness", new Object[] {CaptureDeviceInput,Value});
}

/**
Sets the saturation for capture on this CaptureDeviceInput. This only affects analog capture devices

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Value- the new value to set the saturation to, in the inclusive range 0-255. Use -1 to set it to the default.
 */
public static void SetCaptureSaturation (java.lang.String CaptureDeviceInput, int Value) {
    sagex.SageAPI.call("SetCaptureSaturation", new Object[] {CaptureDeviceInput,Value});
}

/**
Sets the hue for capture on this CaptureDeviceInput. This only affects analog capture devices.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Value- the new value to set the hue to, in the inclusive range 0-255. Use -1 to set it to the default.
 */
public static void SetCaptureHue (java.lang.String CaptureDeviceInput, int Value) {
    sagex.SageAPI.call("SetCaptureHue", new Object[] {CaptureDeviceInput,Value});
}

/**
Sets the contrast for capture on this CaptureDeviceInput. This only affects analog capture devices.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Value- the new value to set the contrast to, in the inclusive range 0-255. Use -1 to set it to the default.
 */
public static void SetCaptureContrast (java.lang.String CaptureDeviceInput, int Value) {
    sagex.SageAPI.call("SetCaptureContrast", new Object[] {CaptureDeviceInput,Value});
}

/**
Sets the sharpness for capture on this CaptureDeviceInput. This only affects analog capture devices.
 NOTE: On Linux this currently sets the audio capture volume level

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Value- the new value to set the sharpness to, in the inclusive range 0-255. Use -1 to set it to the default.
 */
public static void SetCaptureSharpness (java.lang.String CaptureDeviceInput, int Value) {
    sagex.SageAPI.call("SetCaptureSharpness", new Object[] {CaptureDeviceInput,Value});
}

/**
Gets the brightness level for this CaptureDeviceInput. This is only valid for analog capture devices.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the brightness level for the specified CaptureDeviceInput in the inclusive range 0-255
 */
public static int GetCaptureBrightness (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetCaptureBrightness", new Object[] {CaptureDeviceInput});
}

/**
Gets the saturation level for this CaptureDeviceInput. This is only valid for analog capture devices.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the saturation level for the specified CaptureDeviceInput in the inclusive range 0-255
 */
public static int GetCaptureSaturation (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetCaptureSaturation", new Object[] {CaptureDeviceInput});
}

/**
Gets the hue level for this CaptureDeviceInput. This is only valid for analog capture devices.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the hue level for the specified CaptureDeviceInput in the inclusive range 0-255
 */
public static int GetCaptureHue (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetCaptureHue", new Object[] {CaptureDeviceInput});
}

/**
Gets the contrast level for this CaptureDeviceInput. This is only valid for analog capture devices.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the contrast level for the specified CaptureDeviceInput in the inclusive range 0-255
 */
public static int GetCaptureContrast (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetCaptureContrast", new Object[] {CaptureDeviceInput});
}

/**
Gets the sharpness level for this CaptureDeviceInput. This is only valid for analog capture devices.
 NOTE: On Linux this gets the audio volume level

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the sharpness level for the specified CaptureDeviceInput in the inclusive range 0-255
 */
public static int GetCaptureSharpness (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetCaptureSharpness", new Object[] {CaptureDeviceInput});
}

/**
Sets the name of the device that is passed to the IR Tuner plugin for tuning control. Corresponds to a .ir file for current IR transmitters

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
ExternalDeviceName- the name of the external device the IR tuning plugin is supposed to control This value should be obtained from a call to (@link Configuration#GetRemotesForInfraredTuningPlugin GetRemotesForInfraredTuningPlugin()}
 */
public static void SetInfraredTunerRemoteName (java.lang.String CaptureDeviceInput, java.lang.String ExternalDeviceName) {
    sagex.SageAPI.call("SetInfraredTunerRemoteName", new Object[] {CaptureDeviceInput,ExternalDeviceName});
}

/**
Returns the name of the device that is passed to the IR Tuner plugin for tuning control. Corresponds to a .ir file for current IR transmitters.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the name of the external device codes used for the tuner plugin on the specified CaptureDeviceInput
 */
public static java.lang.String GetInfraredTunerRemoteName (java.lang.String CaptureDeviceInput) {
   return (java.lang.String) sagex.SageAPI.call("GetInfraredTunerRemoteName", new Object[] {CaptureDeviceInput});
}

/**
Sets whether or not this CaptureDeviceInput tunes for Antenna or Cable if it's a TV Tuner input

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Value- true if this input is connected to a Cable source, false if it uses Broadcast/Over-the-Air (OTA)
 */
public static void SetRFSignalIsCableTV (java.lang.String CaptureDeviceInput, boolean Value) {
    sagex.SageAPI.call("SetRFSignalIsCableTV", new Object[] {CaptureDeviceInput,Value});
}

/**
Returns whether or not this CaptureDeviceInput tunes for Antenna or Cable if it's a TV Tuner input

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
true if this input is connected to a Cable source, false if it uses Broadcast/Over-the-Air (OTA)
 */
public static boolean IsRFSignalCableTV (java.lang.String CaptureDeviceInput) {
   return (Boolean) sagex.SageAPI.call("IsRFSignalCableTV", new Object[] {CaptureDeviceInput});
}

/**
Returns true if this input was created usingAddInputForRFChannel()
method call

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
true if this input was created usingAddInputForRFChannel()
method call
 */
public static boolean IsExternallyTunedRFInput (java.lang.String CaptureDeviceInput) {
   return (Boolean) sagex.SageAPI.call("IsExternallyTunedRFInput", new Object[] {CaptureDeviceInput});
}

/**
Returns the RF channel number that is used to receive the source signal. This is set usingAddInputForRFChannel()


Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the RF channel number used to receive the source signal on the specified CaptureDeviceInput
 */
public static int GetConstantRFChannelInput (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetConstantRFChannelInput", new Object[] {CaptureDeviceInput});
}

/**
Returns whether or not this CaptureDeviceInput captures both audio and video

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
true if this input captures both audio and video, false if it just captures video
 */
public static boolean IsCaptureDeviceInputAudioVideo (java.lang.String CaptureDeviceInput) {
   return (Boolean) sagex.SageAPI.call("IsCaptureDeviceInputAudioVideo", new Object[] {CaptureDeviceInput});
}

/**
Returns the type of input this is, such as: S-Video, Composite, TV Tuner, etc.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the type of physical connector used for the specified CaptureDeviceInput
 */
public static java.lang.String GetPhysicalInputType (java.lang.String CaptureDeviceInput) {
   return (java.lang.String) sagex.SageAPI.call("GetPhysicalInputType", new Object[] {CaptureDeviceInput});
}

/**
Returns the name of this CaptureDeviceInput connection without the CaptureDevice name prefixing it. This is not the
 same as the 'name' of the CaptureDeviceInput used as the parameter. The String that uniquely identifies a CaptureDeviceInput
 must always have the CaptureDevice's name included in it. Only use this return value for display purposes; do not use
 it for anything else.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput
Returns:
the name of the specified CaptureDeviceInput connection without the CaptureDevice name prefixing it
 */
public static java.lang.String GetCaptureDeviceInputName (java.lang.String CaptureDeviceInput) {
   return (java.lang.String) sagex.SageAPI.call("GetCaptureDeviceInputName", new Object[] {CaptureDeviceInput});
}

/**
Configures this CaptureDeviceInput to use the specified EPG Lineup.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput to set the lineup on
Lineup- the name of the Lineup to configure this CaptureDeviceInput for. This name should be obtained from a call to the EPG server
Returns:
true if the Lineup was successfully configured for the specified CaptureDeviceInput
 */
public static boolean ConfigureInputForEPGDataLineup (java.lang.String CaptureDeviceInput, java.lang.String Lineup) {
   return (Boolean) sagex.SageAPI.call("ConfigureInputForEPGDataLineup", new Object[] {CaptureDeviceInput,Lineup});
}

/**
Configures this CaptureDeviceInput to not use an EPG data source. It will instead create a generic lineup with numeric channels that

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput to use
Returns:
true (the call will always succeed)
 */
public static boolean ConfigureInputWithoutEPGData (java.lang.String CaptureDeviceInput) {
   return (Boolean) sagex.SageAPI.call("ConfigureInputWithoutEPGData", new Object[] {CaptureDeviceInput});
}

/**
Releases this CaptureDeviceInput from its currently configured lineup. It will no longer be considered "configured" or "active". 
 If its lineup is no longer is in use, it will be cleaned up on the next EPG maintenance cycle.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput to use
 */
public static void ReleaseCaptureDeviceInput (java.lang.String CaptureDeviceInput) {
    sagex.SageAPI.call("ReleaseCaptureDeviceInput", new Object[] {CaptureDeviceInput});
}

/**
Returns the CaptureDeviceInput that is recording the MediaFile that is currently loaded by the MediaPlayer

Returns:
the CaptureDeviceInput that is recording the MediaFile that is currently loaded by the MediaPlayer
 */
public static java.lang.String GetCaptureDeviceInputBeingViewed () {
   return (java.lang.String) sagex.SageAPI.call("GetCaptureDeviceInputBeingViewed", (Object[])null);
}

/**
Returns the CaptureDeviceInput that is recording the specified MediaFile

Parameters:
MediaFile- the MediaFile who's recording CaptureDeviceInput should be returned
Returns:
the CaptureDeviceInput that is recording the specified MediaFile; null if that file is not being recorded currently
 */
public static java.lang.String GetCaptureDeviceInputRecordingMediaFile (Object MediaFile) {
   return (java.lang.String) sagex.SageAPI.call("GetCaptureDeviceInputRecordingMediaFile", new Object[] {MediaFile});
}

/**
Returns the name of the lineup that this CaptureDeviceInput is configured to use

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput to use
Returns:
the name of the lineups that is configured for use on the specified CaptureDeviceInput; returns null if the input is not configured
 */
public static java.lang.String GetLineupForCaptureDeviceInput (java.lang.String CaptureDeviceInput) {
   return (java.lang.String) sagex.SageAPI.call("GetLineupForCaptureDeviceInput", new Object[] {CaptureDeviceInput});
}

/**
Returns the CaptureDevice for this CaptureDeviceInput

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput to use
Returns:
the name of the CaptureDevice for the specified CaptureDeviceInput
 */
public static java.lang.String GetCaptureDeviceForInput (java.lang.String CaptureDeviceInput) {
   return (java.lang.String) sagex.SageAPI.call("GetCaptureDeviceForInput", new Object[] {CaptureDeviceInput});
}

/**
Returns the current signal strength for this CaptureDeviceInput. This is only valid for Digital TV inputs.
 The returned value will be between 0 and 100 inclusive. 0 is no signal and 100 is maximum signal strength.

Parameters:
CaptureDeviceInput- the name of the CaptureDeviceInput to use
Returns:
the current signal strength on the specified CaptureDeviceInput
 */
public static int GetSignalStrength (java.lang.String CaptureDeviceInput) {
   return (Integer) sagex.SageAPI.call("GetSignalStrength", new Object[] {CaptureDeviceInput});
}




/** Convenience method for setting this thread's UI Context
@see sagex.SageAPI.setUIConext()*/
public static void setUIContext(String context) {
   sagex.SageAPI.setUIContext(context);
}
}
