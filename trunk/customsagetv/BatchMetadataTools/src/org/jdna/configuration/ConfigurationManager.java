package org.jdna.configuration;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;


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
		if (instance == null) instance=new ConfigurationManager(null);
		return instance;
	}
	
	public IConfigurationProvider config = null;
	
	public ConfigurationManager(IConfigurationProvider conf) {
		if (conf==null) {
			// use a default.
			log.error("Configuration Manager is being initialized WITHOUT a valid properties set.");
			conf = new PropertiesConfigurationProvider(null, new Properties());
		} else {
			log.info("Configuration manager is setting a new Configuration Provider");
		}
		setProvider(conf);
	}
	
	/**
	 * Sets the provider for this instance
	 * 
	 * @param conf
	 */
	public void setProvider(IConfigurationProvider conf) {
		log.info("Setting Configuration Provider: " + conf.getName());
		this.config = conf;
	}
	
	/**
	 * Gets a named configuration item.
	 * 
	 * @param path configuration path in the configuration manager
	 * @param key configuration key
	 * @param def default value if the current key does not exist
	 * 
	 * @return
	 */
	public String getProperty(String key, String def) {
		String v = config.getProperty(key);
		return (v==null) ? def : v;
	}
	
	/**
	 * Convenience getProperty() that uses null as the default value when a property does not exist.
	 * 
	 * @param path
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return getProperty(key, null);
	}

	public IConfigurationProvider getProvider() {
		return config;
	}
	
	public void save() throws IOException {
		config.save();
	}
}
