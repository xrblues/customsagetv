package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 04/07/09 10:31 AM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/SystemMessageAPI.html'>SystemMessageAPI</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public class SystemMessageAPI {
/**
Gets the global alert level in the system.

Returns:
a value from 0-3; with 0=No Alert, 1=Info Alert, 2=Warning Alert, 3=Error Alert
Since:
6.6
 */
public static int GetSystemAlertLevel () {
  Object o = sagex.SageAPI.call("GetSystemAlertLevel", (Object[])null);
  if (o!=null) return (Integer) o;
  return 0;
}

/**
Returns the list of SystemMessage objects currently in the queue.

Returns:
an array of SystemMessage objects currently in the queue
Since:
6.6
 */
public static Object[] GetSystemMessages () {
  return (Object[]) sagex.SageAPI.call("GetSystemMessages", (Object[])null);
}

/**
Resets the global alert level in the system back to zero.

Since:
6.6
 */
public static void ResetSystemAlertLevel () {
   sagex.SageAPI.call("ResetSystemAlertLevel", (Object[])null);
}

/**
Deletes all the SystemMessages from the queue. This will not have any effect on the global alert level.

Since:
6.6
 */
public static void DeleteAllSystemMessages () {
   sagex.SageAPI.call("DeleteAllSystemMessages", (Object[])null);
}

/**
Deletes the specified SystemMessage from the queue. This will not have any effect on the global alert level.

Parameters:
message- the SystemMessage object to delete
Since:
6.6
 */
public static void DeleteSystemMessage (Object message) {
   sagex.SageAPI.call("DeleteSystemMessage", new Object[] {message});
}

/**
Gets the 'message string' associated with this SystemMessage. This is the same result as converting the object to a String.

Parameters:
message- the SystemMessage object to get the 'message string' for
Returns:
the 'message string' for the specified SystemMessage
Since:
6.6
 */
public static java.lang.String GetSystemMessageString (Object message) {
  Object o = sagex.SageAPI.call("GetSystemMessageString", new Object[] {message});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Gets the time when this SystemMessage was first posted.

Parameters:
message- the SystemMessage object to get the time of
Returns:
the time for the specified SystemMessage
Since:
6.6
 */
public static long GetSystemMessageTime (Object message) {
  Object o = sagex.SageAPI.call("GetSystemMessageTime", new Object[] {message});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Gets the time when this SystemMessage was last posted. For messages that did not repeat this will be the same as
 GetSystemMessageTime. For messages that repeated; this will be the time of the last repeating occurence.

Parameters:
message- the SystemMessage object to get the end time of
Returns:
the end time for the specified SystemMessage
Since:
6.6
 */
public static long GetSystemMessageEndTime (Object message) {
  Object o = sagex.SageAPI.call("GetSystemMessageEndTime", new Object[] {message});
  if (o!=null) return (Long) o;
  return 0l;
}

/**
Gets the number of times this message was repeated. For a message that repeated once (i.e. it had 2 occurences), this
 method will return 2.

Parameters:
message- the SystemMessage object to get the repeat count for
Returns:
the repeat count for the specified SystemMessage
Since:
6.6
 */
public static int GetSystemMessageRepeatCount (Object message) {
  Object o = sagex.SageAPI.call("GetSystemMessageRepeatCount", new Object[] {message});
  if (o!=null) return (Integer) o;
  return 0;
}

/**
Returns a localized string which represents the type of SystemMessage that was specified.

Parameters:
message- the SystemMessage object to get the type of
Returns:
the type for the specified SystemMessage
Since:
6.6
 */
public static java.lang.String GetSystemMessageTypeName (Object message) {
  Object o = sagex.SageAPI.call("GetSystemMessageTypeName", new Object[] {message});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns an integer which represents the type of SystemMessage that was specified.

Parameters:
message- the SystemMessage object to get the type of
Returns:
the type for the specified SystemMessage
Since:
6.6
 */
public static int GetSystemMessageTypeCode (Object message) {
  Object o = sagex.SageAPI.call("GetSystemMessageTypeCode", new Object[] {message});
  if (o!=null) return (Integer) o;
  return 0;
}

/**
Returns the alert level for the SystemMessage that was specified.

Parameters:
message- the SystemMessage object to get the alert level of
Returns:
a value from 0-3; with 0=No Alert, 1=Info Alert, 2=Warning Alert, 3=Error Alert
Since:
6.6
 */
public static int GetSystemMessageLevel (Object message) {
  Object o = sagex.SageAPI.call("GetSystemMessageLevel", new Object[] {message});
  if (o!=null) return (Integer) o;
  return 0;
}

/**
Returns the SystemMessage variable property associated with the specified SystemMessage. Depending
 upon the type of message; different variables will be assigned that can be used to do further analysis/processing
 on the message or to guide the user through resolution steps.

Parameters:
message- the SystemMessage object to get the alert level of
VarName- the name of the variable to lookup in this SystemMessage (string based values)
Returns:
a String that corresponds to the requested variable or null if it does not exist
Since:
6.6
 */
public static java.lang.String GetSystemMessageVariable (Object message, java.lang.String VarName) {
  Object o = sagex.SageAPI.call("GetSystemMessageVariable", new Object[] {message,VarName});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Creates a new SystemMessage and posts it to the message queue.
 Predefined message codes of interest for posting messages are:SOFTWARE_UPDATE_MSG = 1202STORAGE_MONITOR_MSG = 1203GENERAL_MSG = 1204

Parameters:
MessageCode- the integer code that specifies the type of message
MessageLevel- the integer code specifying the level of the message; 0=Status(does not raise global level),1=Info, 2=Warning, 3=Error
MessageString- a localized message string that explains what the message is in detail
MessageVariables- a java.util.Properties object which has name->value pairs that represent variables corresponding to the details of this message
Since:
6.6
 */
public static void PostSystemMessage (int MessageCode, int MessageLevel, java.lang.String MessageString, java.util.Properties MessageVariables) {
   sagex.SageAPI.call("PostSystemMessage", new Object[] {MessageCode,MessageLevel,MessageString,MessageVariables});
}




/** Convenience method for setting this thread's UI Context
@see sagex.SageAPI.setUIConext()*/
public static void setUIContext(String context) {
   sagex.SageAPI.setUIContext(context);
}
}
