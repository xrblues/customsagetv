package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit Generated Date/Time: 05/10/08
 * 4:56 PM See Official Sage Documentation at <a
 * href='http://download.sage.tv/api/sage/api/PlaylistAPI.html'>PlaylistAPI</a>
 * This Generated API is not Affiliated with SageTV. It is user contributed.
 */
public class PlaylistAPI {
	/**
	 * Adds the specified item to this Playlist. The item may be either an
	 * Airing, Album, MediaFile or another Playlist.
	 * 
	 * Parameters: Playlist- the Playlist object to add the new item to NewItem-
	 * the new item to add to the Playlist; must be an Airing, Album, MediaFile
	 * or Playlist
	 */
	public static void AddToPlaylist(Object Playlist, java.lang.Object NewItem) {
		sagex.SageAPI.call("AddToPlaylist", new Object[] { Playlist, NewItem });
	}

	/**
	 * Gets the name of the specified Playlist
	 * 
	 * Parameters: Playlist- the Playlist object Returns: the name of the
	 * specified Playlist
	 */
	public static java.lang.String GetName(Object Playlist) {
		return (java.lang.String) sagex.SageAPI.call("GetName", new Object[] { Playlist });
	}

	/**
	 * Gets the number of items in the specified Playlist
	 * 
	 * Parameters: Playlist- the Playlist object Returns: the number of items in
	 * the specified Playlist
	 */
	public static int GetNumberOfPlaylistItems(Object Playlist) {
		return (Integer) sagex.SageAPI.call("GetNumberOfPlaylistItems", new Object[] { Playlist });
	}

	/**
	 * Gets the item in this Playlist at the specified index
	 * 
	 * Parameters: Playlist- the Playlist object Index- the 0-based index into
	 * the playlist to get the item from Returns: the item at the specified
	 * index in the Playlist; this will be an Airing, Album, Playlist or null
	 */
	public static java.lang.Object GetPlaylistItemAt(Object Playlist, int Index) {
		return (java.lang.Object) sagex.SageAPI.call("GetPlaylistItemAt", new Object[] { Playlist, Index });
	}

	/**
	 * Gets the type of item in the Playlist at the specified index
	 * 
	 * Parameters: Playlist- the Playlist object Index- the 0-based index into
	 * the Playlist to get the item type for Returns: the type of item at the
	 * specified index in the Playlist; one of "Airing", "Album", "Playlist" or ""
	 * ("Airing" is used for MediaFile items)
	 */
	public static java.lang.String GetPlaylistItemTypeAt(Object Playlist, int Index) {
		return (java.lang.String) sagex.SageAPI.call("GetPlaylistItemTypeAt", new Object[] { Playlist, Index });
	}

	/**
	 * Gets the list of items in the specified Playlist
	 * 
	 * Parameters: Playlist- the Playlist object Returns: a list of the items in
	 * the specified Playlist
	 */
	public static java.lang.Object[] GetPlaylistItems(Object Playlist) {
		return (java.lang.Object[]) sagex.SageAPI.call("GetPlaylistItems", new Object[] { Playlist });
	}

	/**
	 * Inserts a new item into the specified Playlist at the specified position.
	 * 
	 * Parameters: Playlist- the Playlist object to add the new item to
	 * InsertIndex- the 0-based index that the new item should be inserted at
	 * NewItem- the new item to insert into the Playlist; must be an Airing,
	 * Album, MediaFile or Playlist
	 */
	public static void InsertIntoPlaylist(Object Playlist, int InsertIndex, java.lang.Object NewItem) {
		sagex.SageAPI.call("InsertIntoPlaylist", new Object[] { Playlist, InsertIndex, NewItem });
	}

	/**
	 * Swaps the position of the item at the specified index in the Playlist
	 * with the item at the position (Index - 1)
	 * 
	 * Parameters: Playlist- the Playlist object Index- the position of the item
	 * to move up one in the playlist
	 */
	public static void MovePlaylistItemUp(Object Playlist, int Index) {
		sagex.SageAPI.call("MovePlaylistItemUp", new Object[] { Playlist, Index });
	}

	/**
	 * Swaps the position of the item at the specified index in the Playlist
	 * with the item at the position (Index 1)
	 * 
	 * Parameters: Playlist- the Playlist object Index- the position of the item
	 * to move down one in the playlist
	 */
	public static void MovePlaylistItemDown(Object Playlist, int Index) {
		sagex.SageAPI.call("MovePlaylistItemDown", new Object[] { Playlist, Index });
	}

	/**
	 * Removes the specified item from the Playlist. If this item appears in the
	 * Playlist more than once, only the first occurrence will be removed.
	 * 
	 * Parameters: Playlist- the Playlist object Item- the item to remove from
	 * the Playlist, must be an Airing, MediaFile, Album or Playlist
	 */
	public static void RemovePlaylistItem(Object Playlist, java.lang.Object Item) {
		sagex.SageAPI.call("RemovePlaylistItem", new Object[] { Playlist, Item });
	}

	/**
	 * Removes the specified item at the specified index from the Playlist.
	 * 
	 * Parameters: Playlist- the Playlist object ItemIndex- the index of the
	 * item to remove from the Playlist
	 */
	public static void RemovePlaylistItemAt(Object Playlist, int ItemIndex) {
		sagex.SageAPI.call("RemovePlaylistItemAt", new Object[] { Playlist, ItemIndex });
	}

	/**
	 * Sets the name for this Playlist
	 * 
	 * Parameters: Playlist- the Playlist objecxt Name- the name to set for this
	 * Plyalist
	 */
	public static void SetName(Object Playlist, java.lang.String Name) {
		sagex.SageAPI.call("SetName", new Object[] { Playlist, Name });
	}

	/**
	 * Returns true if the passed in argument is a Playlist object
	 * 
	 * Parameters: Playlist- the object to test to see if it is a Playlist
	 * object Returns: true if the passed in argument is a Playlist object,
	 * false otherwise
	 */
	public static boolean IsPlaylistObject(java.lang.Object Playlist) {
		return (Boolean) sagex.SageAPI.call("IsPlaylistObject", new Object[] { Playlist });
	}

	/**
	 * Gets a list of all of the Playlists in the database
	 * 
	 * Returns: a list of all of the Playlists in the database
	 */
	public static Object[] GetPlaylists() {
		return (Object[]) sagex.SageAPI.call("GetPlaylists", (Object[]) null);
	}

	/**
	 * Removes a specified Playlist from the databse completely. The files in
	 * the Playlist will NOT be removed.
	 * 
	 * Parameters: Playlist- the Playlist object to remove
	 */
	public static void RemovePlaylist(Object Playlist) {
		sagex.SageAPI.call("RemovePlaylist", new Object[] { Playlist });
	}

	/**
	 * Returns true if the specified Playlist contains any video files, false
	 * otherwise
	 * 
	 * Parameters: Playlist- the Playlist object Returns: true if the specified
	 * Playlist contains any video files, false otherwise Since: 5.1
	 */
	public static boolean DoesPlaylistHaveVideo(Object Playlist) {
		return (Boolean) sagex.SageAPI.call("DoesPlaylistHaveVideo", new Object[] { Playlist });
	}

	/**
	 * Creates a new Playlist object
	 * 
	 * Parameters: Name- the name for the new Playlist
	 */
	public static Object AddPlaylist(java.lang.String Name) {
		return (Object) sagex.SageAPI.call("AddPlaylist", new Object[] { Name });
	}

	/**
	 * Returns the 'Now Playing' playlist. This can be used as a local,
	 * client-specific playlist which songs can be added to and then played as a
	 * temporary set of songs. i.e. usually playlists are shared between all of
	 * the clients that are connected to a SageTV system, but this one is NOT
	 * shared
	 * 
	 * Returns: the Playlist object to use as the 'Now Playing' list Since: 5.1
	 */
	public static Object GetNowPlayingList() {
		return (Object) sagex.SageAPI.call("GetNowPlayingList", (Object[]) null);
	}

}
