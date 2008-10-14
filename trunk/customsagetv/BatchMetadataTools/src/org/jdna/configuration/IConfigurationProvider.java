package org.jdna.configuration;

import java.io.IOException;

/**
 * A Configuration Provider provides read only access to configuration data in an abstract way.  It is used to enable code to use configuration data,
 * but not actually depend on xml, properties, etc.
 * 
 * @author seans
 *
 */
public interface IConfigurationProvider {
	public String getName();
	
	/**
	 * Get a property from the given path for a given key.  Implementations should account for a null path, which would indicate a root property.
	 * This method must return null which a given key cannot found for a given path.
	 * 
	 * @param path path in the configuration
	 * @param key key to find
	 * @return key value or null if the key does not exist
	 */
	public String getProperty(String key);
	
	/**
	 * Sets new property value
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value);
	
	/**
	 * returns all the configurable keys in this provider
	 * 
	 * @return String array of keys name.
	 */
	public String[] getKeys();
	
	/**
	 * Saves any changes in this provider to a persistent location
	 */
	public void save() throws IOException;

	/**
	 * Returns the uri location that this provider uses to store it's persistent data.
	 * @return
	 */
	public String getPersistentUri();
}
