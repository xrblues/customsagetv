package test.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.jdna.url.CachedUrl;
import org.jdna.url.UrlConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import sagex.SageAPI;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.stub.StubSageAPI;
import test.junit.lib.InitBMT;


public class TestURLCaching {
    @BeforeClass
    public static void init() throws Exception {
        SageAPI.setProvider(new StubSageAPI());
        InitBMT.initBMT();
    }
    
    @Test
    public void testURLCache() {
        UrlConfiguration cfg = GroupProxy.get(UrlConfiguration.class);
        cfg.setCacheDir("target/junit/cache/");
        cfg.setCacheExpiryInSeconds(10);
        assertEquals(10, cfg.getCacheExpiryInSeconds());
        
        try {
            String urlPath = "http://www.google.ca/";

            System.out.println("Getting: " + urlPath);
            CachedUrl url = new CachedUrl(urlPath);
            url.getInputStream(null, false);
            File f = url.getCachedFile();
            assertNotNull("Didn't Create a cached file for " + urlPath, f);
            assertTrue("Failed to create cached file: " + f.getAbsolutePath(), f.exists());
            long time = f.lastModified();
            
            System.out.println("Getting url again, this time it should be cached.");
            CachedUrl url2 = new CachedUrl(urlPath);
            url2.getInputStream(null, false);
            File f2 = url.getCachedFile();
            assertEquals("getCachedUrl updated cache file, not suppose to happen", f2.lastModified(), time);
            
            System.out.println("Sleeping... waiting for cache to expire");
            Thread.sleep((cfg.getCacheExpiryInSeconds()+2)*1000);
            
            System.out.println("Getting url again, this time it should NOT be cached.");
            CachedUrl url3 = new CachedUrl(urlPath);
            url3.getInputStream(null, false);
            File f3 = url.getCachedFile();
            assertTrue("getCachedUrl didn't update file", f3.lastModified() != time);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to complete url cache test");
        }
    }

}
