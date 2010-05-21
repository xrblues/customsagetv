package org.jdna.bmt.web.client.ui.prefs;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("preferences")
public interface PreferencesService extends RemoteService {
    public PrefItem searchPreferences(String search);
    public PrefItem[] getPreferences(PrefItem parent);
    public boolean savePreferences(PrefItem[] preferences);
    public Log4jPrefs getLog4jPreferences();
    public String saveLog4jPreferences(Log4jPrefs prefs);
    public List<VideoSource> getVideoSources();
    public List<VideoSource> saveVideoSources(List<VideoSource> sources);
    public List<PrefItem> getSageProperties();
    public String getSagePropertiesAsString();
    public ArrayList<String> getSagePropertiesAsList();
    public RegexValidation validateRegex(RegexValidation val);
    public String[] getLog4jLoggers();
    public ArrayList<PrefItem> getLog4jProperties(String log);
    public void saveLog4jProperties(String log, ArrayList<PrefItem> items);
}
