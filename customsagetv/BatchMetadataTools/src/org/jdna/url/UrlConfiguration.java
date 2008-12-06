package org.jdna.url;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(name="urlconfiguration", requiresKey=false, description="Configures Properties regarding how URLs are handled.")
public class UrlConfiguration {
	@Field(description="Cache Directory where cached URLs are stored")
	private String cacheDir="cache/url/";
	
	@Field(description="How long, in seconds, URLs remain in the cache")
	private int cacheExpiryInSeconds=60*60*24;
	
	@Field(description="URL Factory class name for creating new Url objects")
	private String urlFactoryClass=org.jdna.url.CachedUrlFactory.class.getName();
	
	@Field(description="HTTP User Agent that is sent with each hew http request")
	private String httpUserAgent="Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1";
	
	public UrlConfiguration() {
	}
	
	public String getCacheDir() {
		return cacheDir;
	}
	public void setCacheDir(String cacheDir) {
		this.cacheDir = cacheDir;
	}
	public int getCacheExpiryInSeconds() {
		return cacheExpiryInSeconds;
	}
	public void setCacheExpiryInSeconds(int cacheExpiryInSeconds) {
		this.cacheExpiryInSeconds = cacheExpiryInSeconds;
	}
	public String getUrlFactoryClass() {
		return urlFactoryClass;
	}
	public void setUrlFactoryClass(String urlFactoryClass) {
		this.urlFactoryClass = urlFactoryClass;
	}

	public String getHttpUserAgent() {
		return httpUserAgent;
	}

	public void setHttpUserAgent(String httpUserAgent) {
		this.httpUserAgent = httpUserAgent;
	}
}
