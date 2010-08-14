package org.jdna.bmt.web.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdna.bmt.web.client.ui.prefs.Log4jPrefs;
import org.jdna.bmt.web.client.ui.prefs.PrefItem;
import org.jdna.bmt.web.client.ui.prefs.PreferencesService;
import org.jdna.bmt.web.client.ui.prefs.RegexValidation;
import org.jdna.bmt.web.client.ui.prefs.VideoSource;
import org.jdna.bmt.web.client.ui.prefs.VideoSource.SourceType;

import sagex.api.Configuration;
import sagex.phoenix.configuration.Group;
import sagex.phoenix.configuration.IConfigurationElement;
import sagex.phoenix.configuration.NewSearchGroup;
import sagex.phoenix.util.PropertiesUtils;
import sagex.phoenix.util.SortedProperties;
import sagex.util.Log4jConfigurator;
import sagex.util.Log4jConfigurator.LogStruct;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PreferencesServiceImpl extends RemoteServiceServlet implements PreferencesService {
    private static final Logger log = Logger.getLogger(PreferencesServiceImpl.class);
    
    private Map<String, Group> groups = null;

    public PreferencesServiceImpl() {
        groups = new HashMap<String, Group>();
        
        ServicesInit.init();
    }

    public PrefItem[] getPreferences(PrefItem parent) {
        try {
            List<PrefItem> items = new LinkedList<PrefItem>();

            Group group = null;
            if (parent == null) {
                group = phoenix.api.GetConfigurationRoot();
            } else {
                group = groups.get(parent.getKey());
            }

            if (group == null) {
                log.warn("No Configuration For: " + parent.getKey());
                return null;
            }

            return createChildenArray(group);
        } catch (Throwable t) {
            log.error("getPreferences failed!", t);
            return null;
        }
    }

    public PrefItem searchPreferences(String search) {
        log.debug("*** Search: " + search);
        PrefItem pi = new PrefItem();
        pi.setLabel("Search: " + search);
        pi.setChildren(createChildenArray(phoenix.api.AddConfigurationSearch(search)));
        return pi;
    }

    private PrefItem[] createChildenArray(Group group) {
        List<PrefItem> items = new LinkedList<PrefItem>();
        for (IConfigurationElement g : group.getChildren()) {
            if (!g.isVisible()) continue;
            if (NewSearchGroup.NEW_SEARCH_GROUP_ID.equals(g.getId())) continue;

            PrefItem pi = new PrefItem();
            pi.setEditor(g.getEditor());
            pi.setLabel(g.getLabel());
            pi.setDescription(g.getDescription());
            pi.setKey(g.getId());
            log.debug("Item: " + g.getId());
            if (g.getElementType() == Group.GROUP || g.getElementType() == Group.APPLICATION) {
                log.debug("Group: " + true);
                pi.setGroup(true);
                pi.setKey(String.valueOf(g.hashCode()));
                groups.put(pi.getKey(), (Group) g);
            } else {
                Object o = phoenix.api.GetProperty(g.getId());
                if (o != null) {
                    pi.setValue(String.valueOf(o));
                    pi.setResetValue(pi.getValue());
                }

                o = phoenix.api.GetConfigurationDefaultValue(g);
                if (o != null) {
                    pi.setDefaultValue(String.valueOf(o));
                }
                pi.setType(phoenix.api.GetConfigurationFieldType(g));
            }
            items.add(pi);
        }
        return items.toArray(new PrefItem[items.size()]);
    }

    public static void main(String args[]) {
        BasicConfigurator.configure();
        PreferencesServiceImpl impl = new PreferencesServiceImpl();
        for (PrefItem pi : impl.getPreferences(null)) {
            log.debug("Label: " + pi.getLabel());
        }
    }

    public boolean savePreferences(PrefItem[] preferences) {
        for (PrefItem pi : preferences) {
            log.info(String.format("Saving Preference (%s): %s=%s\n", pi.getLabel(), pi.getKey(), pi.getValue()));
            phoenix.api.SetProperty(pi.getKey(), pi.getValue());
        }
        return true;
    }

    private Properties getLogProperties() {
        Properties props = new SortedProperties();
        File f = new File("log4j.properties");
        if (f.exists()) {
            try {
                PropertiesUtils.load(props, f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }
    
    private void saveProperties(Properties props) throws IOException {
        File f = new File("log4j.properties");
        PropertiesUtils.store(props, f, "log4j configuration created using BMT Web UI");
        
        PropertyConfigurator.configure(props);
        log.info("logging is reconfigured.");
    }

    public Log4jPrefs getLog4jPreferences() {
        Log4jPrefs prefs = new Log4jPrefs();
        Properties props = getLogProperties();
        String level = props.getProperty("log4j.rootCategory", "error, log");
        String parts[] = level.split(",");
        prefs.setLevel(parts[0]);
        prefs.setFile(props.getProperty("log4j.appender.log.File", "sage-plugins-log4j.log"));
        prefs.setPattern(props.getProperty("log4j.appender.log.layout.ConversionPattern", "%5r %-5p [%t] %c - %m%n"));
        
        prefs.setBmtLevel(props.getProperty("log4j.logger.bmt", "info"));
        prefs.setPhoenixLevel(props.getProperty("log4j.logger.phoenix", "info"));
        return prefs;
    }

    public String saveLog4jPreferences(Log4jPrefs prefs) {
        log.info("Saving Preferences");
        
        Properties props = getLogProperties();
        String defaultLog = "log";
        props.setProperty("log4j.rootCategory", String.format("%s, %s",prefs.getLevel().get(), defaultLog));

        props.setProperty("log4j.appender.log.File", prefs.getFile().get());
        props.setProperty("log4j.appender.log.layout.ConversionPattern", prefs.getPattern().get());
        
        props.setProperty("log4j.appender.log","org.apache.log4j.DailyRollingFileAppender");
        props.setProperty("log4j.appender.log.DatePattern","'.'yyyy-MM-dd");
        props.setProperty("log4j.appender.log.layout","org.apache.log4j.PatternLayout");
        
        props.setProperty("log4j.logger.org.jdna",prefs.getBmtLevel().get());
        props.setProperty("log4j.logger.bmt", prefs.getBmtLevel().get());
        
        props.setProperty("log4j.logger.phoenix", prefs.getPhoenixLevel().get());
        props.setProperty("log4j.logger.sagex.phoenix", prefs.getPhoenixLevel().get());
        
        try {
            saveProperties(props);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return "ok";
    }

    public List<VideoSource> getVideoSources() {
        List<VideoSource> list = new ArrayList<VideoSource>();
        File files[] = Configuration.GetVideoLibraryImportPaths();
        if (files!=null) {
            for (File f : files) {
                list.add(new VideoSource(f.getAbsolutePath(), SourceType.VIDEO));
            }
        }
        return list;
    }

    public List<VideoSource> saveVideoSources(List<VideoSource> sources) {
        for (VideoSource vs : sources) {
            if (vs.isDeleted()) {
                Configuration.RemoveVideoLibraryImportPath(new File(vs.getPath()));
            } else if (vs.isNew()) {
                Configuration.AddVideoLibraryImportPath(vs.getPath());
            }
        }
        
        return getVideoSources();
    }

    public List<PrefItem> getSageProperties() {
        List<PrefItem> items = new ArrayList<PrefItem>();
        Properties props = new Properties();
        try {
            PropertiesUtils.load(props, new File("Sage.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Sage.properties");
        }
        
        for (Object key : props.keySet()) {
            PrefItem i = new PrefItem();
            i.setKey(String.valueOf(key));
            i.setLabel(String.valueOf(key));
            i.setValue(String.valueOf(props.getProperty(String.valueOf(key))));
            i.setType("string");
            items.add(i);
        }
        
        return items;
    }

    public String getSagePropertiesAsString() {
        try {
            return IOUtils.toString(new FileReader(new File("Sage.properties")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Sage.properties");
        }
    }

    public ArrayList<String> getSagePropertiesAsList() {
        ArrayList<String> items = new ArrayList<String>();
        Properties props = new Properties();
        try {
            PropertiesUtils.load(props, new File("Sage.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Sage.properties");
        }
        
        for (Map.Entry<Object, Object> es : props.entrySet()) {
            items.add(es.getKey() + "=" + es.getValue());
        }
        
        Collections.sort(items);
        return items;
    }

    public RegexValidation validateRegex(RegexValidation val) {
        try {
            val.setResults(null);
            Pattern p = Pattern.compile(val.getRegex());
            LineIterator li = IOUtils.lineIterator(IOUtils.toInputStream(val.getSampleData(), "UTF-8"), "UTF-8");
            while (li.hasNext()) {
                String s = li.nextLine();
                Matcher m = p.matcher(s);
                if (m.find()) {
                    if (val.getResults()==null) {
                        val.setResults(s);
                    } else {
                        val.setResults(val.getResults() + "\n" + s);
                    }
                }
            }
            if (StringUtils.isEmpty(val.getResults())) {
                val.setResults("Regex is valid, but it did not match anything.");
            }
        } catch (Exception e) {
            val.setResults(ExceptionUtils.getStackTrace(e));
            val.setValid(false);
        }
        return val;
    }

    public String[] getLog4jLoggers() {
        Log4jConfigurator.LogStruct[] logs =  Log4jConfigurator.getConfiguredLogs();
        if (logs==null) {
            return null;
        }
        
        String log[] = new String[logs.length];
        for (int i=0;i<logs.length;i++) {
            log[i] = logs[i].id;
        }
        return log;
    }
    
    private LogStruct getLog(String log) {
        Log4jConfigurator.LogStruct[] logs =  Log4jConfigurator.getConfiguredLogs();
        if (logs==null) {
            return null;
        }
        for (int i=0;i<logs.length;i++) {
            if (log.equals(logs[i].id)) return logs[i];
        }
        return null;
    }
    
    public ArrayList<PrefItem> getLog4jProperties(String logId) {
        ArrayList<PrefItem> items = new ArrayList<PrefItem>();
        LogStruct log = getLog(logId);
        for (Map.Entry<Object, Object> me: log.properties.entrySet()) {
            PrefItem pi = new PrefItem();
            pi.setKey(String.valueOf(me.getKey()));
            pi.setValue(String.valueOf(me.getValue()));
            items.add(pi);
        }
        Collections.sort(items, new Comparator<PrefItem>() {
            public int compare(PrefItem p1, PrefItem p2) {
                return p1.getKey().compareTo(p2.getKey());
            }
        });
        return items;
    }

    public void saveLog4jProperties(String logId, ArrayList<PrefItem> items) {
        Properties props = new Properties();
        for (PrefItem p: items) {
            props.setProperty(p.getKey(), p.getValue());
        }
        Log4jConfigurator.reconfigure(logId, props);
    }

	@Override
	public void refreshConfiguration(String id) {
		if (id==null) throw new RuntimeException("Invalid Refresh ID");
		if (REFRESH_VFS.equals("id")) {
			phoenix.umb.ReloadViews();
		} else if (REFRESH_MENUS.equals("id")) {
			phoenix.menu.ReloadMenus();
		} else if (REFRESH_MEDIA_TITLES.equals("id")) {
			phoenix.umb.ReloadMediaTitles();
		}
	}
}
