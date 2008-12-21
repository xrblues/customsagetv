package org.jdna.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jdna.media.impl.MediaConfiguration;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.impl.composite.CompositeMetadataConfiguration;
import org.jdna.media.metadata.impl.dvdprof.DVDProfilerConfiguration;
import org.jdna.media.metadata.impl.dvdproflocal.DVDProfilerLocalConfiguration;
import org.jdna.media.metadata.impl.imdb.IMDBConfiguration;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.metadata.impl.sage.SageMetadataConfiguration;
import org.jdna.media.metadata.impl.themoviedb.TheMovieDBMetadataProvider;
import org.jdna.metadataupdater.MetadataUpdaterConfiguration;
import org.jdna.persistence.IPersistence;
import org.jdna.persistence.PropertiesPersistence;
import org.jdna.url.UrlConfiguration;


/**
 * ConfigurationManager provides an Abstract way to access grouped configurations.  Before the ConfigurationManager can be used, it must
 * have a IConfigurationProvider registered.
 * 
 * This class is a singleton, but it needs to be initialized with a provider before it can be used.
 * 
 * @author seans
 *
 */
public class ConfigurationManager {
	private static final Logger log = Logger.getLogger(ConfigurationManager.class);
	
	private static ConfigurationManager instance;
	
	public static ConfigurationManager getInstance() {
		if (instance == null) instance=new ConfigurationManager();
		return instance;
	}
	
	private File configFile;
	private Properties props;
	private IPersistence persistence;

	// all classes that we manage should be listed here
	// so that we can load and save the configurations
	private Class[] configurationClasses = new Class[] {
		UrlConfiguration.class,
		MetadataUpdaterConfiguration.class,
		MediaConfiguration.class,
		MetadataConfiguration.class,
		DVDProfilerConfiguration.class,
		DVDProfilerLocalConfiguration.class,
		SageMetadataConfiguration.class,
		IMDBConfiguration.class,
		CompositeMetadataConfiguration.class
	};
	
	private Map<Class, Object> loaded = new HashMap<Class, Object>();
	
	public ConfigurationManager() {
		load();
	}
	
	private void load() {
		String cFile = System.getProperty("metadata.properties", "metadata.properties");
		configFile = new File(cFile);
		props = new Properties();
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
	}

	private synchronized void save() throws IOException {
		log.debug("Writing configuration to persistent store.");
		FileWriter fw = new FileWriter(configFile);
		
		// use our own store, so that we can sort the keys for readability
		SortedSet s= new TreeSet();
		for (Enumeration e=props.propertyNames();e.hasMoreElements();) {
			s.add(e.nextElement());
		}

		for (Object o : s) {
			fw.write(o.toString());
			fw.write("=");
			fw.write(props.getProperty(o.toString()));
			fw.write("\n");
		}
		
		fw.flush();
		fw.close();
		
		// remove loaded objects, so that they get recreated.
		loaded.clear();
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
	
	public DVDProfilerConfiguration getDVDProfilerConfiguration() {
		return load(DVDProfilerConfiguration.class);
	}
	
	public DVDProfilerLocalConfiguration getDVDProfilerLocalConfiguration() {
		return load(DVDProfilerLocalConfiguration.class);
	}

	public SageMetadataConfiguration getSageMetadataConfiguration() {
		return load(SageMetadataConfiguration.class);
	}

	public List<CompositeMetadataConfiguration> getCompositeMetadataConfiguration() {
		try {
			List <CompositeMetadataConfiguration> l = (List<CompositeMetadataConfiguration>) loaded.get(CompositeMetadataConfiguration.class);
			if (l==null) {
				l = persistence.loadAll(CompositeMetadataConfiguration.class);
				if (l!=null) {
					loaded.put(CompositeMetadataConfiguration.class, l);
				}
			}
			if (l==null || l.size()==0) {
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
		if (o==null) {
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
	 * Use only in rare cases.  No Checks are done on the properties, so there is much room for error.
	 * 
	 * @param prop
	 * @param value
	 */
	public void setProperty(String prop, String value) {
		log.warn("Setting Override Property: " + prop + ": " + value);
		props.setProperty(prop, value);
		
		// remove any loaded objects, so that they get created with the new values when needed.
		loaded.clear();
	}
	
	/**
	 * Load all configurations and then dump their values.
	 * 
	 * @param pw
	 */
	public void dumpProperties(PrintWriter pw) {
		for (int i=0;i<configurationClasses.length;i++) {
			try {
				if (configurationClasses[i].equals(CompositeMetadataConfiguration.class)) {
					List l = getCompositeMetadataConfiguration();
					for (Object o : l) {
						((PropertiesPersistence)persistence).dumpValues(o, pw);
					}
				} else {
					((PropertiesPersistence)persistence).dumpValues(load(configurationClasses[i]), pw);
				}
				pw.println("");
			} catch (Exception e) {
				pw.println("Failed to dump properties for: " + configurationClasses[i]);
				e.printStackTrace(pw);
			}
		}
	}
	
	public String getConfigFileLocation() {
		if (configFile==null)  return null;
		return configFile.getAbsolutePath();
	}
}
