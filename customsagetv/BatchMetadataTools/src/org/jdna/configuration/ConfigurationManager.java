package org.jdna.configuration;

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
	
	private static ConfigurationManager instance = new ConfigurationManager();
	
	public static ConfigurationManager getInstance() {
		return instance;
	}
	
	public IConfigurationProvider config = null;
	
	public ConfigurationManager() {
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
	public String getProperty(String path, String key, String def) {
		if (config==null) throw new RuntimeException("ConfigurationManager is not initialized!  Please call setProvider() first.");
		String v = config.getProperty(path, key);
		return (v==null) ? def : v;
	}
	
	/**
	 * Convenience getProperty() that uses null as the default value when a property does not exist.
	 * 
	 * @param path
	 * @param key
	 * @return
	 */
	public String getProperty(String path, String key) {
		return getProperty(path, key, null);
	}

	/**
	 * Convenience getProperty() method that uses a null path and a null default value.  Using a null path assumes the root of the configuration manager.
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return getProperty(null, key, null);
	}
}
