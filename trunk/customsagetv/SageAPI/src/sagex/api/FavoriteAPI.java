package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 29/03/09 6:31 PM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/FavoriteAPI.html'>FavoriteAPI</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public class FavoriteAPI {
/**
Returns true if this Favorite is configured to record both first runs and reruns.

Parameters:
Favorite- the Favorite object
Returns:
true if this Favorite is configured to record both first runs AND reruns, false otherwise
 */
public static boolean IsFirstRunsAndReRuns (Object Favorite) {
   return (Boolean) sagex.SageAPI.call("IsFirstRunsAndReRuns", new Object[] {Favorite});
}

/**
Returns true if this Favorite is configured to record first runs but not reruns.

Parameters:
Favorite- the Favorite object
Returns:
true if this Favorite is configured to record first runs only, false otherwise
 */
public static boolean IsFirstRunsOnly (Object Favorite) {
   return (Boolean) sagex.SageAPI.call("IsFirstRunsOnly", new Object[] {Favorite});
}

/**
Returns true if this Favorite is configured to record reruns but not first runs.

Parameters:
Favorite- the Favorite object
Returns:
true if this Favorite is configured to record reruns only, false otherwise
 */
public static boolean IsReRunsOnly (Object Favorite) {
   return (Boolean) sagex.SageAPI.call("IsReRunsOnly", new Object[] {Favorite});
}

/**
Returns true if this Favorite is configured to record first runs (it may or may not record reruns)

Parameters:
Favorite- the Favorite object
Returns:
true if this Favorite is configured to record first runs, false otherwise
 */
public static boolean IsFirstRuns (Object Favorite) {
   return (Boolean) sagex.SageAPI.call("IsFirstRuns", new Object[] {Favorite});
}

/**
Returns true if this Favorite is configured to record reruns (it may or may not record first runs)

Parameters:
Favorite- the Favorite object
Returns:
true if this Favorite is configured to record reruns, false otherwise
 */
public static boolean IsReRuns (Object Favorite) {
   return (Boolean) sagex.SageAPI.call("IsReRuns", new Object[] {Favorite});
}

/**
Returns true if this Favorite is SageTV is allowed to automatically delete recordings of this
 Favorite when it needs more disk space. If this is false, then SageTV will never automatically
 delete files recorded for this Favorite; the user will have to delete the files themself.

Parameters:
Favorite- the Favorite object
Returns:
true if this Favorite is configured for auto delete, false otherwise
 */
public static boolean IsAutoDelete (Object Favorite) {
   return (Boolean) sagex.SageAPI.call("IsAutoDelete", new Object[] {Favorite});
}

/**
Returns the maximum number of recordings that match this Favorite that should be kept on disk. If AutoDelete is
 set to true then SageTV will continue to record new airings of this Favorite as they air, and delete the oldest
 recording on disk if it hits the keep at most limit. If AutoDelete is false then SageTV will stop recording this Favorite
 once it has this many recordings on disk

Parameters:
Favorite- the Favorite object
Returns:
the maximum number of recordings SageTV should keep on disk of this Favorite, 0 if it is unlimited
 */
public static int GetKeepAtMost (Object Favorite) {
   return (Integer) sagex.SageAPI.call("GetKeepAtMost", new Object[] {Favorite});
}

/**
Returns a String that describes this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
a descriptive string for this Favorite
 */
public static java.lang.String GetFavoriteDescription (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteDescription", new Object[] {Favorite});
}

/**
Returns the title that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the title that an Airing must match to be included in this Favorite, returns the empty string if the title isn't a field that needs to match
 */
public static java.lang.String GetFavoriteTitle (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteTitle", new Object[] {Favorite});
}

/**
Returns the category that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the category that an Airing must match to be included in this Favorite, returns the empty string if category isn't a field that needs to match
 */
public static java.lang.String GetFavoriteCategory (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteCategory", new Object[] {Favorite});
}

/**
Returns the subcategory that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the subcategory that an Airing must match to be included in this Favorite, returns the empty string if subcategory isn't a field that needs to match
 */
public static java.lang.String GetFavoriteSubCategory (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteSubCategory", new Object[] {Favorite});
}

/**
Returns the person that an Airing must have to be included in this Favorite. The person may also be restricted by their role in the content.

Parameters:
Favorite- the Favorite object
Returns:
the person that an Airing must have to be included in this Favorite, returns the empty string if person isn't a field that needs to match
 */
public static java.lang.String GetFavoritePerson (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoritePerson", new Object[] {Favorite});
}

/**
Returns the rating that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the rating that an Airing must match to be included in this Favorite, returns the empty string if rating isn't a field that needs to match
 */
public static java.lang.String GetFavoriteRated (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteRated", new Object[] {Favorite});
}

/**
Returns the role that an Airing must have the Favorite Person in to be included in this Favorite. This only applies if a person is set for this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the role that the favorite person for an Airing must have to be included in this Favorite, returns the empty string if role doesn't matter
 */
public static java.lang.String GetFavoritePersonRole (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoritePersonRole", new Object[] {Favorite});
}

/**
Returns the year that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the year that an Airing must match to be included in this Favorite, returns the empty string if year isn't a field that needs to match
 */
public static java.lang.String GetFavoriteYear (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteYear", new Object[] {Favorite});
}

/**
Returns the parental rating that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the parental rating that an Airing must match to be included in this Favorite, returns the empty string if parental rating isn't a field that needs to match
 */
public static java.lang.String GetFavoriteParentalRating (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteParentalRating", new Object[] {Favorite});
}

/**
Returns the channel name (call sign) that an Airing must be on to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the channel name that an Airing must be on to be included in this Favorite, returns the empty string if channel doesn't matter; for mult-channel favorites this will be a semicolon or comma-delimited list of channel names
 */
public static java.lang.String GetFavoriteChannel (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteChannel", new Object[] {Favorite});
}

/**
Returns the keyword string that an Airing must match to be included in this Favorite. This is case insensitive. Double quotes
 can be used to require word to be in a certain order. Otherwise; each individual word is searched for in the Airing's details and must
 match a whole word for the match to succeed.  The * character can be used to match 0 or more characters. The ? character can be used
 to match a single character. Regex is the matching language used for this so any other regex parsing characters besides * and ? can be used.
 All of the fields of the object are searched as part of this.

Parameters:
Favorite- the Favorite object
Returns:
the keyword that an Airing must match to be included in this Favorite, returns the empty string if keyword doesn't matter
 */
public static java.lang.String GetFavoriteKeyword (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteKeyword", new Object[] {Favorite});
}

/**
Returns the network name that an Airing must be on to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the network name that an Airing must be on to be included in this Favorite, returns the empty string if network doesn't matter
 */
public static java.lang.String GetFavoriteNetwork (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteNetwork", new Object[] {Favorite});
}

/**
Returns the timeslot that an Airing must be in to be included in this Favorite. It just needs to overlap the timeslot.

Parameters:
Favorite- the Favorite object
Returns:
the timeslot that an Airing must be in to be included in this Favorite, returns the empty string if timeslot doesn't matter
 */
public static java.lang.String GetFavoriteTimeslot (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteTimeslot", new Object[] {Favorite});
}

/**
Returns the amount of time any recording for this Favorite should start before the actual Airing begins.

Parameters:
Favorite- the Favorite object
Returns:
the amount of time any recording for this Favorite should start before the actual Airing begins, in milliseconds
 */
public static long GetStartPadding (Object Favorite) {
   return (Long) sagex.SageAPI.call("GetStartPadding", new Object[] {Favorite});
}

/**
Returns the amount of time any recording for this Favorite should stop after the actual Airing ends.

Parameters:
Favorite- the Favorite object
Returns:
the amount of time any recording for this Favorite should stop after the actual Airing ends, in milliseconds
 */
public static long GetStopPadding (Object Favorite) {
   return (Long) sagex.SageAPI.call("GetStopPadding", new Object[] {Favorite});
}

/**
Gets the name of the recording quality that should be used when recording this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the name of the recording quality that should be used when recording this Favorite, the empty string if the default recording quality should be used
 */
public static java.lang.String GetFavoriteQuality (Object Favorite) {
   return (java.lang.String) sagex.SageAPI.call("GetFavoriteQuality", new Object[] {Favorite});
}

/**
Sets the amount of time any recording for this Favorite should start before the actual Airing begins.

Parameters:
Favorite- the Favorite object
StartPadding- the amount of time any recording for this Favorite should start before the actual Airing begins, in milliseconds
 */
public static void SetStartPadding (Object Favorite, long StartPadding) {
    sagex.SageAPI.call("SetStartPadding", new Object[] {Favorite,StartPadding});
}

/**
Sets the amount of time any recording for this Favorite should stop after the actual Airing ends.

Parameters:
Favorite- the Favorite object
StopPadding- the amount of time any recording for this Favorite should stop after the actual Airing ends, in milliseconds
 */
public static void SetStopPadding (Object Favorite, long StopPadding) {
    sagex.SageAPI.call("SetStopPadding", new Object[] {Favorite,StopPadding});
}

/**
Sets the name of the recording quality that should be used when recording this Favorite.

Parameters:
Favorite- the Favorite object
Quality- the name of the recording quality that should be used when recording this Favorite, the empty string if the default recording quality should be used
 */
public static void SetFavoriteQuality (Object Favorite, java.lang.String Quality) {
    sagex.SageAPI.call("SetFavoriteQuality", new Object[] {Favorite,Quality});
}

/**
Establishes a priority of one Favorite over another. This will take undo any previous prioritization that it directly conflicts with.
 Favorites with a higher priority will be recorded over ones with a lower priority if there's a case where both cannot be recorded at once.

Parameters:
HigherPriorityFavorite- the Favorite object that you wish to make a higher priority than the other specified Favorite object
LowerPriorityFavorite- the Favorite object that you wish to make a lower priority than the other specified Favorite object
 */
public static void CreateFavoritePriority (Object HigherPriorityFavorite, Object LowerPriorityFavorite) {
    sagex.SageAPI.call("CreateFavoritePriority", new Object[] {HigherPriorityFavorite,LowerPriorityFavorite});
}

/**
Confirms that the user is aware that a manual recording they've selected creates a conflict with this favorite recording. This is purely
 for notifcation purposes.

Parameters:
ManualRecordAiring- the manual record Airing object to confirm the recording priority of
FavoriteAiring- the Airing for the Favorite that won't be recorded due to the manual record
 */
public static void ConfirmManualRecordOverFavoritePriority (Object ManualRecordAiring, Object FavoriteAiring) {
    sagex.SageAPI.call("ConfirmManualRecordOverFavoritePriority", new Object[] {ManualRecordAiring,FavoriteAiring});
}

/**
Sets whether or not SageTV is allowed to automatically delete recordings of this
 Favorite when it needs more disk space. If this is true, then SageTV will never automatically
 delete files recorded for this Favorite; the user will have to delete the files themself.

Parameters:
Favorite- the Favorite object
DontAutoDelete- true if this Favorite is configured to NOT auto delete, false otherwise
 */
public static void SetDontAutodelete (Object Favorite, boolean DontAutoDelete) {
    sagex.SageAPI.call("SetDontAutodelete", new Object[] {Favorite,DontAutoDelete});
}

/**
Sets the maximum number of recordings that match this Favorite that should be kept on disk. If AutoDelete is
 set to true then SageTV will continue to record new airings of this Favorite as they air, and delete the oldest
 recording on disk if it hits the keep at most limit. If AutoDelete is false then SageTV will stop recording this Favorite
 once it has this many recordings on disk

Parameters:
Favorite- the Favorite object
NumberToKeep- the maximum number of recordings SageTV should keep on disk of this Favorite, 0 if it is unlimited
 */
public static void SetKeepAtMost (Object Favorite, int NumberToKeep) {
    sagex.SageAPI.call("SetKeepAtMost", new Object[] {Favorite,NumberToKeep});
}

/**
Sets whether first runs, reruns or both types of airings should be recorded for this Favorite. If both arguments
 are false, SageTV will record both first runs and reruns.

Parameters:
Favorite- the Favorite object
FirstRuns- true if first runs should be recorded with this favorite, false otherwise
ReRuns- true if rerusn should be recorded with this Favorite, false otherwise
 */
public static void SetRunStatus (Object Favorite, boolean FirstRuns, boolean ReRuns) {
    sagex.SageAPI.call("SetRunStatus", new Object[] {Favorite,FirstRuns,ReRuns});
}

/**
Sets the title that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Title- the title that an Airing must match to be included in this Favorite, use the empty string or null if the title isn't a field that needs to match
 */
public static void SetFavoriteTitle (Object Favorite, java.lang.String Title) {
    sagex.SageAPI.call("SetFavoriteTitle", new Object[] {Favorite,Title});
}

/**
Sets the category and optionally the subcategory that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Category- the category that an Airing must match to be included in this Favorite, use the empty string or null if the category isn't a field that needs to match
SubCategory- the category that an Airing must match to be included in this Favorite, use the empty string the subcategory doesn't need to match
 */
public static void SetFavoriteCategories (Object Favorite, java.lang.String Category, java.lang.String SubCategory) {
    sagex.SageAPI.call("SetFavoriteCategories", new Object[] {Favorite,Category,SubCategory});
}

/**
Sets a person (and optionally the role the person must appear in) that must be in an Airing for it to be included in this Favorite

Parameters:
Favorite- the Favorite object
Person- the name of the person that needs to be included in an Airing for it to match this Favorite, use the empty string or null if person doesn't need to match
Role- the name of the role the corresponding person needs to be in, or the emptry string or null if role doesn't matter
 */
public static void SetFavoritePerson (Object Favorite, java.lang.String Person, java.lang.String Role) {
    sagex.SageAPI.call("SetFavoritePerson", new Object[] {Favorite,Person,Role});
}

/**
Sets the rating that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Rated- the rating that an Airing must match to be included in this Favorite, use the empty string or null if rating isn't a field that needs to match
 */
public static void SetFavoriteRated (Object Favorite, java.lang.String Rated) {
    sagex.SageAPI.call("SetFavoriteRated", new Object[] {Favorite,Rated});
}

/**
Sets the year that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Year- the year that an Airing must match to be included in this Favorite, use the empty string or null if year isn't a field that needs to match
 */
public static void SetFavoriteYear (Object Favorite, java.lang.String Year) {
    sagex.SageAPI.call("SetFavoriteYear", new Object[] {Favorite,Year});
}

/**
Sets the parental rating that an Airing must match to be included in this Favorite.

Parameters:
Favorite- the Favorite object
ParentalRating- the parental rating that an Airing must match to be included in this Favorite, use the empty string or null if parental rating isn't a field that needs to match
 */
public static void SetFavoriteParentalRating (Object Favorite, java.lang.String ParentalRating) {
    sagex.SageAPI.call("SetFavoriteParentalRating", new Object[] {Favorite,ParentalRating});
}

/**
Sets the keyword string that an Airing must match to be included in this Favorite. This is case insensitive. Double quotes
 can be used to require word to be in a certain order. Otherwise; each individual word is searched for in the Airing's details and must
 match a whole word for the match to succeed.  The * character can be used to match 0 or more characters. The ? character can be used
 to match a single character. Regex is the matching language used for this so any other regex parsing characters besides * and ? can be used.
 All of the fields of the object are searched as part of this.

Parameters:
Favorite- the Favorite object
Keyword- the keyword that an Airing must match to be included in this Favorite, use the empty string or null if keyword doesn't matter
 */
public static void SetFavoriteKeyword (Object Favorite, java.lang.String Keyword) {
    sagex.SageAPI.call("SetFavoriteKeyword", new Object[] {Favorite,Keyword});
}

/**
Sets the channel name (call sign) that an Airing must be on to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Channel- the channel name (String) or Channel object (Channel) that an Airing must be on to be included in this Favorite, use null or the empty string if channel doesn't matter; you may also use a semicolon or comma-delimited list of channel names for mult-channel favorites
 */
public static void SetFavoriteChannel (Object Favorite, java.lang.Object Channel) {
    sagex.SageAPI.call("SetFavoriteChannel", new Object[] {Favorite,Channel});
}

/**
Sets the network name that an Airing must be on to be included in this Favorite.

Parameters:
Favorite- the Favorite object
Network- the network name that an Airing must be on to be included in this Favorite, use null or the empty string if network doesn't matter
 */
public static void SetFavoriteNetwork (Object Favorite, java.lang.String Network) {
    sagex.SageAPI.call("SetFavoriteNetwork", new Object[] {Favorite,Network});
}

/**
Sets the timeslot that an Airing must be in to be included in this Favorite. It just needs to overlap the timeslot.

Parameters:
Favorite- the Favorite object
Timeslot- the timeslot that an Airing must be in to be included in this Favorite, use null or the empty string if timeslot doesn't matter.
         The timeslot should be in one of three formats: 1) Day Time, 2) Day, 3) Time. Where Day is a day of the week, and Time is an
         hour of the day such as 3pm or 8:00 (if the user's locale uses am/pm then it'll be like 3pm, otherwise it'll use like 8:00)
 */
public static void SetFavoriteTimeslot (Object Favorite, java.lang.String Timeslot) {
    sagex.SageAPI.call("SetFavoriteTimeslot", new Object[] {Favorite,Timeslot});
}

/**
Gets the Favorite that matches this Airing if one exists.

Parameters:
Airing- the Airing object to get a matching Favorite for
Returns:
a Favorite object that matches this Airing or null if there is no such Favorite
 */
public static Object GetFavoriteForAiring (Object Airing) {
   return (Object) sagex.SageAPI.call("GetFavoriteForAiring", new Object[] {Airing});
}

/**
Returns true if the specified Favorite object matches the specified Airing object.

Parameters:
Favorite- the Favorite object to test
Airing- the Airing object to test
Returns:
true if the specified Favorite matches the specified Airing, false otherwise
 */
public static boolean DoesFavoriteMatchAiring (Object Favorite, Object Airing) {
   return (Boolean) sagex.SageAPI.call("DoesFavoriteMatchAiring", new Object[] {Favorite,Airing});
}

/**
Returns true if the argument is a Favorite object, false otherwise

Parameters:
Favorite- an object to test to see if its an instance of Favorite
Returns:
true if the argument is an instance of a Favorite object, false otherwise
 */
public static boolean IsFavoriteObject (java.lang.Object Favorite) {
   return (Boolean) sagex.SageAPI.call("IsFavoriteObject", new Object[] {Favorite});
}

/**
Updates all of the fields of a Favorite that can be used to match. The rules for the arguments are specified in the
 individual methods that allow settting of the corresponding parameter.

Parameters:
Favorite- the Favorite object to update
Title- the title that an Airing must match to be included in this Favorite, use the empty string or null if the title isn't a field that needs to match
FirstRuns- true if this Favorite should match First Runs, false otherwise
ReRuns- true if this Favorite should match ReRuns, false otherwise
Category- the category that an Airing must match to be included in this Favorite, use the empty string or null if category isn't a field that needs to match
SubCategory- the subcategory that an Airing must match to be included in this Favorite, use the empty string or null if subcategory isn't a field that needs to match
Person- the name of the person that needs to be included in an Airing for it to match this Favorite, use the empty string or null if person doesn't need to match
RoleForPerson- the name of the role the corresponding person needs to be in, or the emptry string or null if role doesn't matter
Rated- the rating that an Airing must match to be included in this Favorite, use the empty string or null if rating isn't a field that needs to match
Year- the year that an Airing must match to be included in this Favorite, use the empty string or null if year isn't a field that needs to match
ParentalRating- the parental rating that an Airing must match to be included in this Favorite, use the empty string or null if parental rating isn't a field that needs to match
Network- the network name that an Airing must be on to be included in this Favorite, use null or the empty string if network doesn't matter
ChannelCallSign- the channel name that an Airing must be on to be included in this Favorite, use null or the empty string if channel doesn't matter; you may also use a semicolon or comma-delimited list of channel names for mult-channel favorites
Timeslot- the timeslot that an Airing must be in to be included in this Favorite, use null or the empty string if timeslot doesn't matter.
         The timeslot should be in one of three formats: 1) Day Time, 2) Day, 3) Time. Where Day is a day of the week, and Time is an
         hour of the day such as 3pm or 8:00 (if the user's locale uses am/pm then it'll be like 3pm, otherwise it'll use like 8:00)
Keyword- the keyword that an Airing must match to be included in this Favorite, use the empty string or null if keyword doesn't matter
Returns:
the updated Favorite object
 */
public static Object UpdateFavorite (Object Favorite, java.lang.String Title, boolean FirstRuns, boolean ReRuns, java.lang.String Category, java.lang.String SubCategory, java.lang.String Person, java.lang.String RoleForPerson, java.lang.String Rated, java.lang.String Year, java.lang.String ParentalRating, java.lang.String Network, java.lang.String ChannelCallSign, java.lang.String Timeslot, java.lang.String Keyword) {
   return (Object) sagex.SageAPI.call("UpdateFavorite", new Object[] {Favorite,Title,FirstRuns,ReRuns,Category,SubCategory,Person,RoleForPerson,Rated,Year,ParentalRating,Network,ChannelCallSign,Timeslot,Keyword});
}

/**
Returns a list of all of the Airings in the database that match this Favorite.

Parameters:
Favorite- the Favorite object
Returns:
the list of Airings in the DB that match this Favorite
 */
public static Object[] GetFavoriteAirings (Object Favorite) {
   return (Object[]) sagex.SageAPI.call("GetFavoriteAirings", new Object[] {Favorite});
}

/**
Gets a unique ID for this Favorite which can be used withGetFavoriteForID()
for retrieving the object later.

Parameters:
Favorite- the Favorite object
Returns:
the unique ID for this Favorite
 */
public static int GetFavoriteID (Object Favorite) {
   return (Integer) sagex.SageAPI.call("GetFavoriteID", new Object[] {Favorite});
}

/**
Gets the Favorite object with the corresponding ID from the database. Use withGetFavoriteID()


Parameters:
FavoriteID- the ID to look up in the DB for a Favorite object
Returns:
the Favorite object with the specified ID if it exists, null otherwise
 */
public static Object GetFavoriteForID (int FavoriteID) {
   return (Object) sagex.SageAPI.call("GetFavoriteForID", new Object[] {FavoriteID});
}

/**
Gets all of the Favorite objects from the database

Returns:
all of the Favorite objects in the database
 */
public static Object[] GetFavorites () {
   return (Object[]) sagex.SageAPI.call("GetFavorites", (Object[])null);
}

/**
Removes a Favorite object from the database. Airings matching this Favorite will not necesarilly be automatically recorded anymore (intelligent recording may still record them)

Parameters:
Favorite- the Favorite object to remove
 */
public static void RemoveFavorite (Object Favorite) {
    sagex.SageAPI.call("RemoveFavorite", new Object[] {Favorite});
}

/**
Creates a new Favorite object in SageTV. Airings that match this Favorite will be recorded and saved into the SageTV Recordings.

Parameters:
Title- the title that an Airing must match to be included in this Favorite, use the empty string or null if the title isn't a field that needs to match
FirstRuns- true if this Favorite should match First Runs, false otherwise
ReRuns- true if this Favorite should match ReRuns, false otherwise (if both FirstRuns and ReRuns are false, then it will match both)
Category- the category that an Airing must match to be included in this Favorite, use the empty string or null if category isn't a field that needs to match
SubCategory- the subcategory that an Airing must match to be included in this Favorite, use the empty string or null if subcategory isn't a field that needs to match
Person- the name of the person that needs to be included in an Airing for it to match this Favorite, use the empty string or null if person doesn't need to match
RoleForPerson- the name of the role the corresponding person needs to be in, or the emptry string or null if role doesn't matter
Rated- the rating that an Airing must match to be included in this Favorite, use the empty string or null if rating isn't a field that needs to match
Year- the year that an Airing must match to be included in this Favorite, use the empty string or null if year isn't a field that needs to match
ParentalRating- the parental rating that an Airing must match to be included in this Favorite, use the empty string or null if parental rating isn't a field that needs to match
Network- the network name that an Airing must be on to be included in this Favorite, use null or the empty string if network doesn't matter
ChannelCallSign- the channel name that an Airing must be on to be included in this Favorite, use null or the empty string if channel doesn't matter; you may also use a semicolon or comma-delimited list of channel names for multi-channel favorites
Timeslot- the timeslot that an Airing must be in to be included in this Favorite, use null or the empty string if timeslot doesn't matter.
         The timeslot should be in one of three formats: 1) Day Time, 2) Day, 3) Time. Where Day is a day of the week, and Time is an
         hour of the day such as 3pm or 8:00 (if the user's locale uses am/pm then it'll be like 3pm, otherwise it'll use like 8:00)
Keyword- the keyword that an Airing must match to be included in this Favorite, use the empty string or null if keyword doesn't matter
Returns:
the newly created Favorite object
 */
public static Object AddFavorite (java.lang.String Title, boolean FirstRuns, boolean ReRuns, java.lang.String Category, java.lang.String SubCategory, java.lang.String Person, java.lang.String RoleForPerson, java.lang.String Rated, java.lang.String Year, java.lang.String ParentalRating, java.lang.String Network, java.lang.String ChannelCallSign, java.lang.String Timeslot, java.lang.String Keyword) {
   return (Object) sagex.SageAPI.call("AddFavorite", new Object[] {Title,FirstRuns,ReRuns,Category,SubCategory,Person,RoleForPerson,Rated,Year,ParentalRating,Network,ChannelCallSign,Timeslot,Keyword});
}




/** Convenience method for setting this thread's UI Context
@see sagex.SageAPI.setUIConext()*/
public static void setUIContext(String context) {
   sagex.SageAPI.setUIContext(context);
}
}
