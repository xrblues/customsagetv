package org.jdna.configuration;


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
	public String getProperty(String path, String key);
}
