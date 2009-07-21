package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 20/07/09 5:47 PM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/SeriesInfoAPI.html'>SeriesInfoAPI</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public class SeriesInfoAPI {
/**
Returns a list of all of the SeriesInfo which is information about television series

Returns:
a list of all of the SeriesInfo
Since:
5.1
 */
public static Object[] GetAllSeriesInfo () {
  return (Object[]) sagex.SageAPI.call("GetAllSeriesInfo", (Object[])null);
}

/**
Returns the title for the specified SeriesInfo

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
the title for the specified SeriesInfo
Since:
5.1
 */
public static java.lang.String GetSeriesTitle (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesTitle", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns the description for the specified SeriesInfo

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
the description for the specified SeriesInfo
Since:
5.1
 */
public static java.lang.String GetSeriesDescription (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesDescription", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns the history description for the specified SeriesInfo

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
the history description for the specified SeriesInfo
Since:
5.1
 */
public static java.lang.String GetSeriesHistory (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesHistory", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns a String describing the premiere date for the specified SeriesInfo

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
a String describing the premiere date for the specified SeriesInfo
Since:
5.1
 */
public static java.lang.String GetSeriesPremiereDate (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesPremiereDate", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns a String describing the finale date for the specified SeriesInfo

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
a String describing the finale date for the specified SeriesInfo
Since:
5.1
 */
public static java.lang.String GetSeriesFinaleDate (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesFinaleDate", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns the name of the network the specified SeriesInfo airs on

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
the name of the network the specified SeriesInfo airs on
Since:
5.1
 */
public static java.lang.String GetSeriesNetwork (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesNetwork", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns the name of the day of the week the specified SeriesInfo airs on

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
the name of the day of the week the specified SeriesInfo airs on
Since:
5.1
 */
public static java.lang.String GetSeriesDayOfWeek (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesDayOfWeek", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns the hour/minute timeslot that the specified SeriesInfo airs at

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
the hour/minute timeslot that the specified SeriesInfo airs at
Since:
5.1
 */
public static java.lang.String GetSeriesHourAndMinuteTimeslot (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesHourAndMinuteTimeslot", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns true if the specified SeriesInfo has a corresponding image for it

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
true if the specified SeriesInfo has a corresponding image for it
Since:
5.1
 */
public static boolean HasSeriesImage (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("HasSeriesImage", new Object[] {SeriesInfo});
  if (o!=null) return (Boolean) o;
  return false;
}

/**
Returns the image that corresponds to this SeriesInfo if there is one

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
the image that corresponds to this SeriesInfo if there is one
Since:
5.1
 */
public static Object GetSeriesImage (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesImage", new Object[] {SeriesInfo});
  if (o!=null) return (Object) o;
  return null;
}

/**
Returns the number of characters that we have information on for the specified series

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
the number of characters that we have information on for the specified series
Since:
5.1
 */
public static int GetNumberOfCharactersInSeries (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetNumberOfCharactersInSeries", new Object[] {SeriesInfo});
  if (o!=null) return (Integer) o;
  return 0;
}

/**
Returns the name of the actor/actress for the specfied index in the specified SeriesInfo. The range
 for the index is from 0 to one less than the value ofGetNumberOfCharactersInSeries()


Parameters:
SeriesInfo- the SeriesInfo object
Index- the 0-based index of the actor to retrieve
Returns:
the name of the actor/actress for the specfied index in the specified SeriesInfo
Since:
5.1
 */
public static java.lang.String GetSeriesActor (Object SeriesInfo, int Index) {
  Object o = sagex.SageAPI.call("GetSeriesActor", new Object[] {SeriesInfo,Index});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns a list of the names of the actors/actresses in the specified SeriesInfo.

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
a list of the names of the actors/actresses in the specified SeriesInfo
Since:
5.1
 */
public static java.lang.String GetSeriesActorList (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesActorList", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns the name of the character for the specfied index in the specified SeriesInfo. The range
 for the index is from 0 to one less than the value ofGetNumberOfCharactersInSeries()


Parameters:
SeriesInfo- the SeriesInfo object
Index- the 0-based index of the actor to retrieve
Returns:
the name of the character for the specfied index in the specified SeriesInfo
Since:
5.1
 */
public static java.lang.String GetSeriesCharacter (Object SeriesInfo, int Index) {
  Object o = sagex.SageAPI.call("GetSeriesCharacter", new Object[] {SeriesInfo,Index});
  if (o!=null) return (java.lang.String) o;
  return null;
}

/**
Returns a list of the names of the characters in the specified SeriesInfo.

Parameters:
SeriesInfo- the SeriesInfo object
Returns:
a list of the names of the characters in the specified SeriesInfo
Since:
5.1
 */
public static java.lang.String GetSeriesCharacterList (Object SeriesInfo) {
  Object o = sagex.SageAPI.call("GetSeriesCharacterList", new Object[] {SeriesInfo});
  if (o!=null) return (java.lang.String) o;
  return null;
}




/** Convenience method for setting this thread's UI Context
@see sagex.SageAPI.setUIConext()*/
public static void setUIContext(String context) {
   sagex.SageAPI.setUIContext(context);
}
}
