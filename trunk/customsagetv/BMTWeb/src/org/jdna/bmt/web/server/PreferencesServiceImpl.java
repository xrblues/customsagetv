package org.jdna.bmt.web.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdna.bmt.web.client.ui.prefs.Log4jPrefs;
import org.jdna.bmt.web.client.ui.prefs.PrefItem;
import org.jdna.bmt.web.client.ui.prefs.PreferencesService;
import org.jdna.util.SortedProperties;

import sagex.phoenix.configuration.Group;
import sagex.phoenix.configuration.IConfigurationElement;
import sagex.phoenix.configuration.NewSearchGroup;
import sagex.phoenix.util.PropertiesUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PreferencesServiceImpl extends RemoteServiceServlet implements PreferencesService {
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
                System.out.println("No Configuration For: " + parent.getKey());
                return null;
            }

            return createChildenArray(group);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public PrefItem searchPreferences(String search) {
        System.out.println("*** Search: " + search);
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
            System.out.println("Item: " + g.getId());
            if (g.getElementType() == Group.GROUP || g.getElementType() == Group.APPLICATION) {
                System.out.println("Group: " + true);
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
            System.out.println("Label: " + pi.getLabel());
        }
    }

    public boolean savePreferences(PrefItem[] preferences) {
        for (PrefItem pi : preferences) {
            System.out.printf("Saving Preference (%s): %s=%s\n", pi.getLabel(), pi.getKey(), pi.getValue());
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
        System.out.println("Logging is reconfigured");
        Logger.getLogger(PreferencesServiceImpl.class).info("logging is reconfigured.");
    }

    public Log4jPrefs getLog4jPreferences() {
        Log4jPrefs prefs = new Log4jPrefs();
        Properties props = getLogProperties();
        String level = props.getProperty("log4j.rootCategory", "error, log");
        String parts[] = level.split(",");
        prefs.setLevel(parts[0]);
        prefs.setFile(props.getProperty("log4j.appender.log.File", "sage-plugins-log4j.log"));
        prefs.setPattern(props.getProperty("log4j.appender.log.layout.ConversionPattern", "%d{ISO8601} %5p [%t] %C - %m%n"));
        
        prefs.setBmtLevel(props.getProperty("log4j.logger.bmt", "info"));
        prefs.setPhoenixLevel(props.getProperty("log4j.logger.phoenix", "info"));
        return prefs;
    }

    public String saveLog4jPreferences(Log4jPrefs prefs) {
        System.out.println("Saving Preferences");
        
        Properties props = getLogProperties();
        String defaultLog = "log";
        props.setProperty("log4j.rootCategory", String.format("%s, %s",prefs.getLevel().get(), defaultLog));

        props.setProperty("log4j.appender.log.File", prefs.getFile().get());
        props.setProperty("log4j.appender.log.layout.ConversionPattern", prefs.getPattern().get());
        
        props.setProperty("log4j.appender.log","org.apache.log4j.DailyRollingFileAppender");
        props.setProperty("log4j.appender.log.DatePattern","'.'yyyy-MM-dd-a");
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

}
