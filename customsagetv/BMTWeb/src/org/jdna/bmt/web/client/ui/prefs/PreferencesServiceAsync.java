package org.jdna.bmt.web.client.ui.prefs;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PreferencesServiceAsync {
    void searchPreferences(String search, AsyncCallback<PrefItem> callback);
    void getPreferences(PrefItem parent, AsyncCallback<PrefItem[]> callback);
    void savePreferences(PrefItem[] preferences, AsyncCallback<Boolean> callback);
    void getLog4jPreferences(AsyncCallback<Log4jPrefs> callback);
    void saveLog4jPreferences(Log4jPrefs prefs, AsyncCallback<String> status);
}