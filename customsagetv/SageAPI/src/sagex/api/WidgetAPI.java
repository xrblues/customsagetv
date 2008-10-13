package sagex.api;

/**
 * Unofficial SageTV Generated File - Never Edit Generated Date/Time: 11/10/08
 * 8:53 PM See Official Sage Documentation at <a
 * href='http://download.sage.tv/api/sage/api/WidgetAPI.html'>WidgetAPI</a>
 * This Generated API is not Affiliated with SageTV. It is user contributed.
 */
public class WidgetAPI {
	/**
	 * Loads a new SageTV Application Definition file that defines the entire
	 * user interface for SageTV
	 * 
	 * Parameters: STVFile- the new .stv file that should be loaded for the UI
	 * Returns: true if it was succesful, otherwise an error string
	 */
	public static java.lang.Object LoadSTVFile(java.io.File STVFile) {
		return (java.lang.Object) sagex.SageAPI.call("LoadSTVFile", new Object[] { STVFile });
	}

	/**
	 * Imports a SageTV Application Definition file into the current STV file
	 * that is loaded. This will essentially merge the two together.
	 * 
	 * Parameters: STVFile- the .stv file that should be imported into the
	 * currently loaded one Returns: true if it was succesful, otherwise an
	 * error string
	 */
	public static java.lang.Object ImportSTVFile(java.io.File STVFile) {
		return (java.lang.Object) sagex.SageAPI.call("ImportSTVFile", new Object[] { STVFile });
	}

	/**
	 * Returns true if the currently loaded STV has been modified at all since
	 * its last save
	 * 
	 * Returns: true if the currently loaded STV has been modified at all since
	 * its last save Since: 6.1
	 */
	public static boolean IsSTVModified() {
		return (Boolean) sagex.SageAPI.call("IsSTVModified", (Object[]) null);
	}

	/**
	 * Gets all of the Widgets that are in the currently loaded STV
	 * 
	 * Returns: all of the Widgets that are in the currently loaded STV
	 */
	public static Object[] GetAllWidgets() {
		return (Object[]) sagex.SageAPI.call("GetAllWidgets", (Object[]) null);
	}

	/**
	 * Gets all of the Widgets that are in the currently loaded STV that are of
	 * the specified type
	 * 
	 * Returns: all of the Widgets that are in the currently loaded STV that are
	 * of the specified type
	 */
	public static Object[] GetWidgetsByType(java.lang.String WidgetType) {
		return (Object[]) sagex.SageAPI.call("GetWidgetsByType", new Object[] { WidgetType });
	}

	/**
	 * Creates a new Widget of the specified type and adds it to the STV
	 * 
	 * Parameters: WidgetType- the type of the new Widget Returns: the newly
	 * created Widget
	 */
	public static Object AddWidget(java.lang.String WidgetType) {
		return (Object) sagex.SageAPI.call("AddWidget", new Object[] { WidgetType });
	}

	/**
	 * Removes a Widget from the STV
	 * 
	 * Parameters: Widget- the Widget to remove
	 */
	public static void RemoveWidget(Object Widget) {
		sagex.SageAPI.call("RemoveWidget", new Object[] { Widget });
	}

	/**
	 * Creates a parent-child relationship between two Widgets. If the
	 * relationship already exists, this call has no effect. This new child will
	 * be the last child of the parent.
	 * 
	 * Parameters: WidgetParent- the Widget that should be the parent in the
	 * relationship WidgetChild- the Widget that should be the child in the
	 * relationship
	 */
	public static void AddWidgetChild(Object WidgetParent, Object WidgetChild) {
		sagex.SageAPI.call("AddWidgetChild", new Object[] { WidgetParent, WidgetChild });
	}

	/**
	 * Creates a parent-child relationship between two Widgets. Since
	 * parent-child relationships are ordered, this allows specifying where in
	 * that order this relationship should be.
	 * 
	 * Parameters: WidgetParent- the Widget that should be the parent in the
	 * relationship WidgetChild- the Widget that should be the child in the
	 * relationship ChildIndex- the 0-based index in the parent's child
	 * relationships list that the new relationship should occupy
	 */
	public static void InsertWidgetChild(Object WidgetParent, Object WidgetChild, int ChildIndex) {
		sagex.SageAPI.call("InsertWidgetChild", new Object[] { WidgetParent, WidgetChild, ChildIndex });
	}

	/**
	 * Breaks a parent-child relationships between two Widgets. If the Widgets
	 * do not have the specified parent-child relationship then there is no
	 * effect.
	 * 
	 * Parameters: WidgetParent- the parent of the Widget relationship to break
	 * WidgetChild- the child of the Widget relationship to break
	 */
	public static void RemoveWidgetChild(Object WidgetParent, Object WidgetChild) {
		sagex.SageAPI.call("RemoveWidgetChild", new Object[] { WidgetParent, WidgetChild });
	}

	/**
	 * Returns true if the specified Widgets have a parent-child relationship.
	 * 
	 * Parameters: WidgetParent- the parent Widget to test WidgetChild- the
	 * child Widget to test Returns: true if the specified parent has a
	 * parent-child relationship with the specified child, false otherwise
	 */
	public static boolean IsWidgetParentOf(Object WidgetParent, Object WidgetChild) {
		return (Boolean) sagex.SageAPI.call("IsWidgetParentOf", new Object[] { WidgetParent, WidgetChild });
	}

	/**
	 * Returns the type of a Widget
	 * 
	 * Parameters: Widget- the Widget object Returns: the type name of the
	 * specified Widget
	 */
	public static java.lang.String GetWidgetType(Object Widget) {
		return (java.lang.String) sagex.SageAPI.call("GetWidgetType", new Object[] { Widget });
	}

	/**
	 * Returns true if the specified Widget has a property defined with the
	 * specified name
	 * 
	 * Parameters: Widget- the Widget object PropertyName- the name of the
	 * property to check existence of Returns: true if the specified Widget has
	 * a property defined with the specified name, false otherwise
	 */
	public static boolean HasWidgetProperty(Object Widget, java.lang.String PropertyName) {
		return (Boolean) sagex.SageAPI.call("HasWidgetProperty", new Object[] { Widget, PropertyName });
	}

	/**
	 * Sets a property in a Widget to a specified value. If that property is
	 * already defined, this will overwrite it.
	 * 
	 * Parameters: Widget- the Widget object PropertyName- the name of the
	 * property to set in the Widget PropertyValue- the value to set the
	 * property to
	 */
	public static void SetWidgetProperty(Object Widget, java.lang.String PropertyName, java.lang.String PropertyValue) {
		sagex.SageAPI.call("SetWidgetProperty", new Object[] { Widget, PropertyName, PropertyValue });
	}

	/**
	 * Returns the value for a specified property in a Widget
	 * 
	 * Parameters: Widget- the Widget object PropertyName- the name of the
	 * property to get Returns: the value for a specified property in a Widget
	 */
	public static java.lang.String GetWidgetProperty(Object Widget, java.lang.String PropertyName) {
		return (java.lang.String) sagex.SageAPI.call("GetWidgetProperty", new Object[] { Widget, PropertyName });
	}

	/**
	 * Returns the name of the specified Widget
	 * 
	 * Parameters: Widget- the Widget object Returns: the name of the specified
	 * Widget
	 */
	public static java.lang.String GetWidgetName(Object Widget) {
		return (java.lang.String) sagex.SageAPI.call("GetWidgetName", new Object[] { Widget });
	}

	/**
	 * Sets the name for a Widget
	 * 
	 * Parameters: Widget- the Widget object Name- the value to set the name to
	 * for this Widget
	 */
	public static void SetWidgetName(Object Widget, java.lang.String Name) {
		sagex.SageAPI.call("SetWidgetName", new Object[] { Widget, Name });
	}

	/**
	 * Gets the list of Widgets that are parents of the specified Widget. The
	 * ordering of this list has no effect.
	 * 
	 * Parameters: Widget- the Widget object Returns: a list of Widgets which
	 * are all parents of the specified Widget
	 */
	public static Object[] GetWidgetParents(Object Widget) {
		return (Object[]) sagex.SageAPI.call("GetWidgetParents", new Object[] { Widget });
	}

	/**
	 * Gets the list of Widgets that are children of the specified Widget. The
	 * ordering of this list does have an effect.
	 * 
	 * Parameters: Widget- the Widget object Returns: a list of Widgets which
	 * are all children of the specified Widget
	 */
	public static Object[] GetWidgetChildren(Object Widget) {
		return (Object[]) sagex.SageAPI.call("GetWidgetChildren", new Object[] { Widget });
	}

	/**
	 * Executes a Widget and the chain of child Widgets underneath it
	 * 
	 * Parameters: Widget- the root of the Widget action chain to execute
	 * Returns: the value returned by the last executed Widget in the chain
	 */
	public static java.lang.Object ExecuteWidgetChain(Object Widget) {
		return (java.lang.Object) sagex.SageAPI.call("ExecuteWidgetChain", new Object[] { Widget });
	}

	/**
	 * Launches a new menu in SageTV with the specified Widget as the menu's
	 * definition.
	 * 
	 * Parameters: Widget- the Widget object to use for the launched menu, this
	 * must be a Menu type Widget
	 */
	public static void LaunchMenuWidget(Object Widget) {
		sagex.SageAPI.call("LaunchMenuWidget", new Object[] { Widget });
	}

	/**
	 * Gets the STV file that is currently loaded by the system
	 * 
	 * Returns: the STV file that is currently loaded by the system
	 */
	public static java.lang.String GetCurrentSTVFile() {
		return (java.lang.String) sagex.SageAPI.call("GetCurrentSTVFile", (Object[]) null);
	}

	/**
	 * Searches the children of the specified Widget for one with the specified
	 * type and name. If no match is found then null is returned. If there are
	 * multiple matches then the first one is returned.
	 * 
	 * Parameters: Widget- the Widget who's children should be searched Type-
	 * the type of the Widget to search for, if null than any type will match
	 * Name- the name that the Widget to search for must match, if null than any
	 * name will match Returns: the Widget child of the specified Widget of the
	 * specified type and name
	 */
	public static Object GetWidgetChild(Object Widget, java.lang.String Type, java.lang.String Name) {
		return (Object) sagex.SageAPI.call("GetWidgetChild", new Object[] { Widget, Type, Name });
	}

	/**
	 * Searches the parents of the specified Widget for one with the specified
	 * type and name. If no match is found then null is returned. If there are
	 * multiple matches then the first one is returned.
	 * 
	 * Parameters: Widget- the Widget who's parents should be searched Type- the
	 * type of the Widget to search for, if null than any type will match Name-
	 * the name that the Widget to search for must match, if null than any name
	 * will match Returns: the Widget parent of the specified Widget of the
	 * specified type and name
	 */
	public static Object GetWidgetParent(Object Widget, java.lang.String Type, java.lang.String Name) {
		return (Object) sagex.SageAPI.call("GetWidgetParent", new Object[] { Widget, Type, Name });
	}

	/**
	 * Gets the Widget the defines the menu that is currently loaded by the
	 * system
	 * 
	 * Returns: the Widget the defines the menu that is currently loaded by the
	 * system
	 */
	public static Object GetCurrentMenuWidget() {
		return (Object) sagex.SageAPI.call("GetCurrentMenuWidget", (Object[]) null);
	}

	/**
	 * Gets a list of the Widgets that have defined the menus that were recently
	 * displayed in the UI
	 * 
	 * Returns: a list of the Widgets that have defined the menus that were
	 * recently displayed in the UI
	 */
	public static Object[] GetWidgetMenuHistory() {
		return (Object[]) sagex.SageAPI.call("GetWidgetMenuHistory", (Object[]) null);
	}

	/**
	 * Gets a list of the Widgets that have defined the menus that were recently
	 * displayed in the UI. UnlikeGetWidgetMenuHistory() this only returns Menus
	 * that are 'Back' (not Forward) in the navigations the user has performed.
	 * Similar to getting only the 'Back' history in a web browser.
	 * 
	 * Returns: a list of the Widgets that have defined the menus that were
	 * recently displayed in the UI Since: 5.1
	 */
	public static Object[] GetWidgetMenuBackHistory() {
		return (Object[]) sagex.SageAPI.call("GetWidgetMenuBackHistory", (Object[]) null);
	}

	/**
	 * Evaluates the passed in expression and returns the result. This is
	 * executed in a new variable context w/out any user interface context.
	 * 
	 * Parameters: Expression- the expression string to evaluate Returns: the
	 * result of evaluating the specified expression
	 */
	public static java.lang.Object EvaluateExpression(java.lang.String Expression) {
		return (java.lang.Object) sagex.SageAPI.call("EvaluateExpression", new Object[] { Expression });
	}

	/**
	 * Saves all of the current Widgets as an XML file. Same as the "Save a Copy
	 * as XML..." in the Studio.
	 * 
	 * Parameters: File- the file to write to Overwrite- if true then if the
	 * File exists it will be overwritten Returns: true if successful, false if
	 * not
	 */
	public static boolean SaveWidgetsAsXML(java.io.File File, boolean Overwrite) {
		return (Boolean) sagex.SageAPI.call("SaveWidgetsAsXML", new Object[] { File, Overwrite });
	}

	/**
	 * Returns the UID symbol for the specified Widget
	 * 
	 * Parameters: Widget- the Widget object Returns: the UID symbol which is
	 * used to represent this widget uniquely Since: 6.4
	 */
	public static java.lang.String GetWidgetSymbol(Object Widget) {
		return (java.lang.String) sagex.SageAPI.call("GetWidgetSymbol", new Object[] { Widget });
	}

	/**
	 * Returns the Widget represented by the specified UID symbol
	 * 
	 * Parameters: Symbol- the UID symbol to lookup the Widget for Returns: the
	 * Widget who's symbol matches the argument, null if it cannot be found
	 * Since: 6.4
	 */
	public static Object FindWidgetBySymbol(java.lang.String Symbol) {
		return (Object) sagex.SageAPI.call("FindWidgetBySymbol", new Object[] { Symbol });
	}

	/**
	 * Returns the file path for the default STV file
	 * 
	 * Returns: the file path for the default STV file Since: 6.4
	 */
	public static java.io.File GetDefaultSTVFile() {
		return (java.io.File) sagex.SageAPI.call("GetDefaultSTVFile", (Object[]) null);
	}

}
