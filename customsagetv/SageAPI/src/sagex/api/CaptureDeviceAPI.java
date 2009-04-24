package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 23/04/09 7:39 AM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/CaptureDeviceAPI.html'>CaptureDeviceAPI</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public class CaptureDeviceAPI {
/**
Returns all of the CaptureDevices in the system that SageTV can use

Returns:
the names of all of the CaptureDevices in the system that SageTV can use
 */
public static java.lang.String[] GetCaptureDevices () {
   return (java.lang.String[]) sagex.SageAPI.call("GetCaptureDevices", (Object[])null);
}

/**
Returns all of the CaptureDeviceInputs for a given CaptureDevice.

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
all of the CaptureDeviceInputs for the specified CaptureDevice.
 */
public static java.lang.String[] GetCaptureDeviceInputs (java.lang.String CaptureDevice) {
   return (java.lang.String[]) sagex.SageAPI.call("GetCaptureDeviceInputs", new Object[] {CaptureDevice});
}

/**
Returns all of the CaptureDeviceInputs that are currently configured for use by SageTV.

Returns:
the names of all of the CaptureDeviceInputs that are currently configured for use by SageTV.
 */
public static java.lang.String[] GetConfiguredCaptureDeviceInputs () {
   return (java.lang.String[]) sagex.SageAPI.call("GetConfiguredCaptureDeviceInputs", (Object[])null);
}

/**
Returns whether or not a CaptureDevice is functioning (i.e. the device is offline)

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
false if a CaptureDevice is NOT functioning (i.e. the device is offline), otherwise true
 */
public static boolean IsCaptureDeviceFunctioning (java.lang.String CaptureDevice) {
   return (Boolean) sagex.SageAPI.call("IsCaptureDeviceFunctioning", new Object[] {CaptureDevice});
}

/**
Returns true if a CaptureDevice is a Network Encoder

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
true if the specified CaptureDevice is a Network Encoder
 */
public static boolean IsCaptureDeviceANetworkEncoder (java.lang.String CaptureDevice) {
   return (Boolean) sagex.SageAPI.call("IsCaptureDeviceANetworkEncoder", new Object[] {CaptureDevice});
}

/**
Returns all of the CaptureDevices that are currently configured for use by SageTV

Returns:
all of the CaptureDevices that are currently configured for use by SageTV
 */
public static java.lang.String[] GetActiveCaptureDevices () {
   return (java.lang.String[]) sagex.SageAPI.call("GetActiveCaptureDevices", (Object[])null);
}

/**
Returns true if the CaptureDevice is currently under control of a client who is (or was) watching live TV

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
true if the specified CaptureDevice is currently under control of a client who is watching live/delayed TV
 */
public static boolean IsCaptureDeviceInUseByALiveClient (java.lang.String CaptureDevice) {
   return (Boolean) sagex.SageAPI.call("IsCaptureDeviceInUseByALiveClient", new Object[] {CaptureDevice});
}

/**
Returns a CaptureDeviceInput that corresponds to using the tuner input on the CaptureDevice locked to a certain channel. 
 For example, using the RF connection from your cable box to the capture card on channel 3 would required adding a new input this way.

Parameters:
CaptureDevice- the name of the CaptureDevice to add the new input to
RFChannel- the channel to tune to for this RF input
Returns:
the name of the CaptureDeviceInput that was created which will act as an RF channel input
 */
public static java.lang.String AddInputForRFChannel (java.lang.String CaptureDevice, int RFChannel) {
   return (java.lang.String) sagex.SageAPI.call("AddInputForRFChannel", new Object[] {CaptureDevice,RFChannel});
}

/**
Returns the last CaptureDevice that was accessed by SageTV.

Returns:
the name of the last CaptureDevice that was accessed by SageTV.
 */
public static java.lang.String GetLastUsedCaptureDevice () {
   return (java.lang.String) sagex.SageAPI.call("GetLastUsedCaptureDevice", (Object[])null);
}

/**
Returns the last CaptureDeviceInput that was used by SageTV on the given CaptureDevice

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
the name of the last CaptureDeviceInput that was used by SageTV on the given CaptureDevice
 */
public static java.lang.String GetLastUsedCaptureDeviceInput (java.lang.String CaptureDevice) {
   return (java.lang.String) sagex.SageAPI.call("GetLastUsedCaptureDeviceInput", new Object[] {CaptureDevice});
}

/**
Returns the file that is currently being recorded by this capture device

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
the file that is currently being recorded by the specified capture device
 */
public static Object GetCaptureDeviceCurrentRecordFile (java.lang.String CaptureDevice) {
   return (Object) sagex.SageAPI.call("GetCaptureDeviceCurrentRecordFile", new Object[] {CaptureDevice});
}

/**
Returns the recording qualities which are supported by this CaptureDevice

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
the recording qualities which are supported by the specified CaptureDevice
 */
public static java.lang.String[] GetCaptureDeviceQualities (java.lang.String CaptureDevice) {
   return (java.lang.String[]) sagex.SageAPI.call("GetCaptureDeviceQualities", new Object[] {CaptureDevice});
}

/**
Returns the default recording qualities for this CaptureDevice.

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
the default recording quality for the specified CaptureDevice; if there is no default quality set it will return the empty string
 */
public static java.lang.String GetCaptureDeviceDefaultQuality (java.lang.String CaptureDevice) {
   return (java.lang.String) sagex.SageAPI.call("GetCaptureDeviceDefaultQuality", new Object[] {CaptureDevice});
}

/**
Sets the default recording quality for a CaptureDevice

Parameters:
CaptureDevice- the name of the CaptureDevice
Quality- the default quality setting to use for the specified capture device, use null or the empty string to clear the setting
 */
public static void SetCaptureDeviceDefaultQuality (java.lang.String CaptureDevice, java.lang.String Quality) {
    sagex.SageAPI.call("SetCaptureDeviceDefaultQuality", new Object[] {CaptureDevice,Quality});
}

/**
Sets the audio capture source for a corresponding CaptureDevice

Parameters:
CaptureDevice- the name of the CaptureDevice
AudioSource- the name of the audio capture source, should be one of the values fromGetAudioCaptureSources()
 */
public static void SetCaptureDeviceAudioSource (java.lang.String CaptureDevice, java.lang.String AudioSource) {
    sagex.SageAPI.call("SetCaptureDeviceAudioSource", new Object[] {CaptureDevice,AudioSource});
}

/**
Gets the audio capture source for a corresponding CaptureDevice.

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
the name of the audio capture source for the specified CaptureDevice; the empty string is returned if there is no separate audio capture source (i.e. multiplexed capture or video only capture)
 */
public static java.lang.String GetCaptureDeviceAudioSource (java.lang.String CaptureDevice) {
   return (java.lang.String) sagex.SageAPI.call("GetCaptureDeviceAudioSource", new Object[] {CaptureDevice});
}

/**
Returns an array of all the audio capture sources in the system, used withSetCaptureDeviceAudioSource(CaptureDevice, AudioSource)


Returns:
an array of all the audio capture sources in the system
 */
public static java.lang.String[] GetAudioCaptureSources () {
   return (java.lang.String[]) sagex.SageAPI.call("GetAudioCaptureSources", (Object[])null);
}

/**
Returns true if the CaptureDevice is a hardware encoder

Parameters:
CaptureDevice- the name of the CaptureDevice
Returns:
true if the specified CaptureDevice is a hardware encoder
 */
public static boolean IsCaptureDeviceHardwareEncoder (java.lang.String CaptureDevice) {
   return (Boolean) sagex.SageAPI.call("IsCaptureDeviceHardwareEncoder", new Object[] {CaptureDevice});
}




/** Convenience method for setting this thread's UI Context
@see sagex.SageAPI.setUIConext()*/
public static void setUIContext(String context) {
   sagex.SageAPI.setUIContext(context);
}
}
