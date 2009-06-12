package org.jdna.bmt.web.client.ui.prefs;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("preferences")
public interface PreferencesService extends RemoteService {
    public PrefItem searchPreferences(String search);
    public PrefItem[] getPreferences(PrefItem parent);
    public boolean savePreferences(PrefItem[] preferences);
    public Log4jPrefs getLog4jPreferences();
    public String saveLog4jPreferences(Log4jPrefs prefs);
}
