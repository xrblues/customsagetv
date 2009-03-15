package org.jdna.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.impl.MediaConfiguration;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.impl.composite.CompositeMetadataConfiguration;
import org.jdna.media.metadata.impl.dvdproflocal.DVDProfilerLocalConfiguration;
import org.jdna.media.metadata.impl.imdb.IMDBConfiguration;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.metadata.impl.sage.SageMetadataConfiguration;
import org.jdna.media.metadata.impl.themoviedb.TheMovieDBMetadataProvider;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperConfiguration;
import org.jdna.metadataupdater.MetadataUpdaterConfiguration;
import org.jdna.persistence.IPersistence;
import org.jdna.persistence.PropertiesPersistence;
import org.jdna.persistence.annotations.Table;
import org.jdna.url.UrlConfiguration;
import org.jdna.util.SortedProperties;

/**
 * ConfigurationManager provides an Abstract way to access grouped
 * configurations. Before the ConfigurationManager can be used, it must have a
 * IConfigurationProvider registered.
 * 
 * This class is a singleton, but it needs to be initialized with a provider
 * before it can be used.
 * 
 * @author seans
 * 
 */
public class ConfigurationManager {
    private static final Logger         log = Logger.getLogger(ConfigurationManager.class);

    private static ConfigurationManager instance;

    public static ConfigurationManager getInstance() {
        if (instance == null) instance = new ConfigurationManager();
        return instance;
    }

    private File               configFile;
    private Properties         props;
    private IPersistence       persistence;
    
    
    private File titleMapPropsFile;
    private Properties titleMapProps;

    // all classes that we manage should be listed here
    // so that we can load and save the configurations
    private Class[]            configurationClasses = new Class[] {
            UrlConfiguration.class,
            MetadataUpdaterConfiguration.class,
            MediaConfiguration.class,
            MetadataConfiguration.class,
            DVDProfilerLocalConfiguration.class,
            SageMetadataConfiguration.class,
            IMDBConfiguration.class,
            CompositeMetadataConfiguration.class   };

    private Map<Class, Object> loaded               = new HashMap<Class, Object>();

    public ConfigurationManager() {
        load();
    }

    private void load() {
        String cFile = System.getProperty("metadata.properties", "metadata.properties");
        configFile = new File(cFile);
        log.debug("Loading: " + configFile.getAbsolutePath());
        props = new SortedProperties();
        if (configFile.exists()) {
            try {
                props.load(new FileInputStream(configFile));
            } catch (Exception e) {
                log.error("Failed to load custom properties from: " + cFile);
            }
        } else {
            log.warn("Configuration: " + cFile + " does not exist.  Using defaults.");
        }
        persistence = new PropertiesPersistence(props);
        
        // now load the tvfilename props, if they exist
        titleMapProps = new SortedProperties();
        titleMapPropsFile = new File("metadata-titles.properties");
        log.debug("Loading: " + titleMapPropsFile.getAbsolutePath());
        if (titleMapPropsFile.exists()) {
            try {
                titleMapProps.load(new FileInputStream(titleMapPropsFile));
            } catch (Exception e) {
                log.error("Failed to load title mappings from: " + titleMapPropsFile.getAbsolutePath());
            }
        } else {
            log.info("No titles map configured: " + titleMapPropsFile.getAbsolutePath());
        }
    }

    public synchronized void updated(Object o) throws Exception {
        persistence.save(o);
    }

    public synchronized void save() throws IOException {
        log.debug("Writing configuration to persistent store: " + configFile.getAbsolutePath());
        
        FileWriter fw = new FileWriter(configFile);
        props.store(fw, "Configuration Properties");
        fw.flush();
        fw.close();

        // remove loaded objects, so that they get recreated.
        loaded.clear();
    }

    public synchronized void saveTitleMappings() throws IOException {
        log.debug("Writing Title mappings: " + titleMapPropsFile.getAbsolutePath());
        
        FileWriter fw = new FileWriter(titleMapPropsFile);
        titleMapProps.store(fw, "Title to MediaProviderDataID mappings");
        fw.flush();
        fw.close();
    }
    
    public MetadataID getMetadataIdForTitle(String title) {
        log.debug("Looking for MetadataID for title: " + title);
        if (title==null) return null;
        String mid = titleMapProps.getProperty(title);
        if (!StringUtils.isEmpty(mid)) {
            log.debug("Found MetadataID + " + mid + " for title: " + title);
            return new MetadataID(mid);
        }
        log.debug("No MetadataID configured for title: " + title);
        return null;
    }
    
    public void setMetadataIdForTitle(String title, MetadataID id) {
        if (id==null||title==null) return;
        titleMapProps.setProperty(title, id.toIDString());
    }

    public UrlConfiguration getUrlConfiguration() {
        return load(UrlConfiguration.class);
    }

    public MetadataUpdaterConfiguration getMetadataUpdaterConfiguration() {
        return load(MetadataUpdaterConfiguration.class);
    }

    public MediaConfiguration getMediaConfiguration() {
        return load(MediaConfiguration.class);
    }

    public MetadataConfiguration getMetadataConfiguration() {
        return load(MetadataConfiguration.class);
    }

    public DVDProfilerLocalConfiguration getDVDProfilerLocalConfiguration() {
        return load(DVDProfilerLocalConfiguration.class);
    }

    public SageMetadataConfiguration getSageMetadataConfiguration() {
        return load(SageMetadataConfiguration.class);
    }

    public List<CompositeMetadataConfiguration> getCompositeMetadataConfiguration() {
        try {
            List<CompositeMetadataConfiguration> l = (List<CompositeMetadataConfiguration>) loaded.get(CompositeMetadataConfiguration.class);
            if (l == null) {
                l = persistence.loadAll(CompositeMetadataConfiguration.class);
                if (l != null) {
                    loaded.put(CompositeMetadataConfiguration.class, l);
                }
            }
            if (l == null || l.size() == 0) {
                // add in the sample CompositeProvider
                CompositeMetadataConfiguration c = new CompositeMetadataConfiguration();
                c.setId("sample");
                c.setName("Sample Composite Provider");
                c.setDescription("This is a sample Composite Provider that will disappear as soon as you create one");
                c.setSearchProviderId(IMDBMetaDataProvider.PROVIDER_ID);
                c.setDetailProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
                c.setFieldsFromSearchProvider("Genre");
                l = new ArrayList<CompositeMetadataConfiguration>();
                l.add(c);
            }
            return l;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public IMDBConfiguration getIMDBConfiguration() {
        return load(IMDBConfiguration.class);
    }

    protected <T> T load(Class<T> objectType) {
        T o = (T) loaded.get(objectType);
        if (o == null) {
            try {
                o = persistence.load(objectType);
                loaded.put(objectType, o);
            } catch (Exception e) {
                log.error("Failed to Load: " + objectType.getName());
                o = null;
            }
        }
        return o;
    }

    /**
     * Use only in rare cases. No Checks are done on the properties, so there is
     * much room for error.
     * 
     * @param prop
     * @param value
     */
    public void setProperty(String prop, String value) {
        log.warn("Setting Override Property: " + prop + ": " + value);
        props.setProperty(prop, value);

        // find the class annotation
        // find the field annoation
        // set the field property
        
        Pattern p = Pattern.compile("/([^/]+)/(.*)");
        Matcher m = p.matcher(prop);
        if (!m.find()) throw new RuntimeException("Invalid Property: " + prop);
        
        Class cl = null;
        for (Class<?> c : configurationClasses) {
            Table t = c.getAnnotation(Table.class);
            if (t.name().equals(m.group(1))) {
                cl = c;
                break;
            }
        }
        
        if (cl==null) throw new RuntimeException("Invalid Property! No matching configuration object for: " + prop);
        
        Object o = load(cl);
        if (o==null) throw new RuntimeException("Invalid Property! Unable to create configuration object for: " + prop);
        
        try {
            invoke(o, getField(o, m.group(2)), value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    private static Field getField(Object o, String field) {
        Field f=null;
        try {
            f = o.getClass().getDeclaredField(field);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Field: " + field);
        }
        return f;
    }
   
    private static void invoke(Object o, Field f, String value) throws Exception {
        f.setAccessible(true);
        Class<?> clType = f.getType();
        if (clType.equals(String.class)) {
            f.set(o, value);
        } else if (clType.equals(Integer.class) || clType.equals(int.class)) {
            f.set(o, Integer.parseInt(value));
        } else if (clType.equals(Float.class) || clType.equals(float.class)) {
            f.set(o, Float.parseFloat(value));
        } else if (clType.equals(Double.class) || clType.equals(double.class)) {
            f.set(o, Double.parseDouble(value));
        } else if (clType.equals(Long.class) || clType.equals(long.class)) {
            f.set(o, Long.parseLong(value));
        } else if (clType.equals(Boolean.class) || clType.equals(boolean.class)) {
            f.set(o, Boolean.parseBoolean(value));
        } else {
            throw new Exception("Unsupport Type: " + clType.getName());
        }
    }

    
    /**
     * Load all configurations and then dump their values.
     * 
     * @param pw
     */
    public void dumpProperties(PrintWriter pw) {
        for (int i = 0; i < configurationClasses.length; i++) {
            try {
                if (configurationClasses[i].equals(CompositeMetadataConfiguration.class)) {
                    List l = getCompositeMetadataConfiguration();
                    for (Object o : l) {
                        ((PropertiesPersistence) persistence).dumpValues(o, pw);
                    }
                } else {
                    ((PropertiesPersistence) persistence).dumpValues(load(configurationClasses[i]), pw);
                }
                pw.println("");
            } catch (Exception e) {
                pw.println("Failed to dump properties for: " + configurationClasses[i]);
                e.printStackTrace(pw);
            }
        }
    }

    public String getConfigFileLocation() {
        if (configFile == null) return null;
        return configFile.getAbsolutePath();
    }

    public XbmcScraperConfiguration getScraperConfiguration(String id) {
        try {
            return persistence.load(XbmcScraperConfiguration.class, id);
        } catch (Exception e) {
            log.error("Failed to create configuration for: " + id, e);
        }
        return null;
    }
    
    
}
