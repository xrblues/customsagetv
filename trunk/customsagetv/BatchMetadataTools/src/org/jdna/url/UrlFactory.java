package org.jdna.url;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;


public class UrlFactory implements IUrlFactory {
	private static final Logger log = Logger.getLogger(UrlFactory.class);
	private static IUrlFactory factory;
	
	public static IUrl newUrl(String url) {
		if (factory==null) {
			createFactory();
		}
		try {
			return factory.createUrl(url);
		} catch (Exception e) {
			log.error("Factory failed to create a url from the factory, so we are returning default url.", e);
			// we never a let url return the error
			return new Url(url);
		}
	}
	
	private static void createFactory() {
		try {
			String cl = ConfigurationManager.getInstance().getProperty("org.jdna.url.UrlFactory.factoryClass", "org.jdna.url.CachedUrlFactory");
			if (cl==null) {
				factory = new UrlFactory();
			} else {
				factory = (IUrlFactory) Class.forName(cl).newInstance();
			}
		} catch (Throwable t) {
			log.error("Failed to create IUrlFactory! Using Default.", t);
			factory = new UrlFactory();
		}
	}

	public IUrl createUrl(String url) {
		return new Url(url);
	}
}
