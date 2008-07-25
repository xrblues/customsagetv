package org.jdna.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Simple Properties implementation of the IConfigurationProvider.  It is backed by a java.util.Properties instance.
 * 
 * @author seans
 *
 */
public class PropertiesConfigurationProvider implements IConfigurationProvider {
	private Properties props;
	
	public PropertiesConfigurationProvider(Properties parent) {
		if (parent==null) {
			props = new Properties();
		} else {
			props = new Properties(parent);
		}
	}
	
	public PropertiesConfigurationProvider() {
		this(null);
	}
	
	
	/**
	 * get a property from the properties.  It will first check System.getProperty(), and if it exists, then it will use it, otherwise it will use
	 * the backing properties engine.
	 */
	public String getProperty(String path, String key) {
		String k = key;
		if (path!=null) {
			k = (path + "." + key);
		}
		
		return System.getProperty(k, props.getProperty(k));
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
	
}
