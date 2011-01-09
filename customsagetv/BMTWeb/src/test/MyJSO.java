package test;

import com.google.gwt.core.client.JavaScriptObject;

/**
* Java overlay of a JavaScriptObject, whose JSON
* representation is { count: 5 }.
*/
public class MyJSO extends JavaScriptObject {
  // Convert a JSON encoded string into a MyJSO instance
  public static native MyJSO fromJSONString(String jsonString) /*-{
    return eval('(' + jsonString + ')');
  }-*/;

  // Returns the count property of this MyJSO
  public native int getCount() /*-{
    return this.count;
  }-*/;
}
