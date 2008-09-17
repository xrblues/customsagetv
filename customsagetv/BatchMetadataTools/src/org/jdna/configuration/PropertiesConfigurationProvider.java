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
	
	public PropertiesConfigurationProvider(Properties defaultProps) {
		if (defaultProps==null) {
			props = new Properties();
		} else {
			props = new Properties(defaultProps);
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
	
	/**
	 * This will load a new set of properties as the main set of properties.  If there is already a set of properties loaded,
	 * then those existing properties will serve as defaults of the new set of properties.
	 * @param newProps
	 */
	public void addProperties(InputStream is) throws IOException {
		try {
			Properties newProps = new Properties(props);
			newProps.load(is);
			props = newProps;
		} catch (IOException e) {
			throw e;
		}
	}
	
}
