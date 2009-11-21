package org.jdna.url;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(path = "bmt/urlconfiguration", label="URL Settings", description = "Configures Properties regarding how URLs are handled.")
public class UrlConfiguration extends GroupProxy {
    @AField(label="Cache Dir", description = "Cache Directory where cached URLs are stored", editor="dirChooser")
    protected FieldProxy<String> cacheDir = new FieldProxy<String>("cache/url");

    @AField(label="Cache Expiry", description = "How long, in seconds, URLs remain in the cache")
    protected FieldProxy<Integer> cacheExpiryInSeconds = new FieldProxy<Integer>(60 * 60 * 4);

    @AField(label="URL Factory Class", description = "URL Factory class name for creating new Url objects")
    protected FieldProxy<String> urlFactoryClass      = new FieldProxy<String>(org.jdna.url.CachedUrlFactory.class.getName());

    @AField(label="Http User Agent", description = "HTTP User Agent that is sent with each hew http request")
    protected FieldProxy<String> httpUserAgent        =  new FieldProxy<String>("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1");

    public UrlConfiguration() {
        super();
        init();
    }

    public String getCacheDir() {
        return cacheDir.get();
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir.set(cacheDir);
    }

    public int getCacheExpiryInSeconds() {
        return cacheExpiryInSeconds.getInt();
    }

    public void setCacheExpiryInSeconds(int cacheExpiryInSeconds) {
        this.cacheExpiryInSeconds.set(cacheExpiryInSeconds);
    }

    public String getUrlFactoryClass() {
        return urlFactoryClass.get();
    }

    public void setUrlFactoryClass(String urlFactoryClass) {
        this.urlFactoryClass.set(urlFactoryClass);
    }

    public String getHttpUserAgent() {
        return httpUserAgent.get();
    }

    public void setHttpUserAgent(String httpUserAgent) {
        this.httpUserAgent.set(httpUserAgent);
    }
}
