package org.jdna.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Simple Properties implementation of the IConfigurationProvider.  It is backed by a java.util.Properties instance.
 * 
 * @author seans
 *
 */
public class PropertiesConfigurationProvider implements IConfigurationProvider {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PropertiesConfigurationProvider.class);
	
	private Properties props;
	private File persistentStore;
	
	public PropertiesConfigurationProvider(File persistentStore, Properties defaultProps) {
		try {
			load(persistentStore, defaultProps);
		} catch (IOException e) {
			log.error("Failed to load properties!!! May experience some issues.", e);
		}
	}
	
	private void load(File persistentStore, Properties defaultProps) throws IOException {
		this.persistentStore=persistentStore;
		if (defaultProps==null) {
			props = new Properties();
		} else {
			props = defaultProps;
		}
		
		Properties mainProps = new Properties(props);
		if (persistentStore!=null) {
			if (persistentStore.exists()) {
				mainProps.load(new FileReader(persistentStore));
			}
		}
		props = mainProps;
	}

	public PropertiesConfigurationProvider(Properties props) {
		this(null,props);
	}
	
	public PropertiesConfigurationProvider(File persistentStore) {
		this(persistentStore,null);
	}
	
	/**
	 * get a property from the properties.  It will first check System.getProperty(), and if it exists, then it will use it, otherwise it will use
	 * the backing properties engine.
	 */
	public String getProperty(String key) {
		return System.getProperty(key, props.getProperty(key));
	}

	public void load(InputStream is) throws IOException {
		props.load(is);
	}
	
	public void save(OutputStream os) throws IOException {
		props.store(os, "ConfigurationManager Properties");
	}

	public String getName() {
		return this.getClass().getName();
	}
	
	public String[] getKeys() {
		Enumeration keys = props.propertyNames();
		List<String> l = new ArrayList<String>();
		while (keys.hasMoreElements()) {
			l.add((String) keys.nextElement());
		}
		return (String[]) l.toArray(new String[l.size()]); 
	}

	public String getPersistentUri() {
		if (persistentStore!=null) return null;
		return persistentStore.toURI().toString();
	}

	public void save() throws IOException {
		if (persistentStore==null) throw new IOException("Persistent Store not Defined.");
		props.store(new FileWriter(persistentStore), "Saved: " + SimpleDateFormat.getDateInstance().format(new Date()));
	}

	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}
}
