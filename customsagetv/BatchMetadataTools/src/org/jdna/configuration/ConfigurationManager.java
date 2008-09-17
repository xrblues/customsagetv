package org.jdna.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
		if (instance == null) instance=new ConfigurationManager();
		return instance;
	}
	
	public IConfigurationProvider config = null;
	
	public ConfigurationManager() {
		// attempt to load a default set of configuration properties from the res://configurationmanager.properties
		PropertiesConfigurationProvider pcp = new PropertiesConfigurationProvider();
		try {
			try {
				pcp.addProperties(this.getClass().getClassLoader().getResourceAsStream("configurationmanager.properties"));
				log.info("Loaded default configuration.");
			} catch (Exception e) {
				log.error("Failed to load default configuration properties!", e);
			}
			
			String propFile = System.getProperty("configurationmanager.properties", "configurationmanager.properties");
			File pFile = null;
			pFile = new File(propFile);

			if (!pFile.exists()) {
				log.info("No user configuration.  You can create a user configuration by creating the following file: " + pFile.getAbsolutePath());
			} else {
				log.info("Attempting to load user defined properties: " + pFile.getAbsolutePath());
				try {
					pcp.addProperties(new FileInputStream(pFile));
				} catch (IOException e) {
					log.error("Failed to load properties: " + pFile.getAbsolutePath(), e);
					throw e;
				}
			}
		} catch (Exception e) {
			log.error("No default properties configuration found.  Using System.getProperties() as the default properties.");
		}
		config = pcp;
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
	
	public IConfigurationProvider getProvider() {
		return config;
	}
}
