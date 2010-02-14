package test.junit;

import static test.junit.lib.FilesTestCase.getFile;
import static test.junit.lib.FilesTestCase.listFiles;
import static test.junit.lib.FilesTestCase.makeDir;
import static test.junit.lib.FilesTestCase.makeFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.sage.MetadataUpdaterPlugin;
import org.jdna.sage.PluginConfiguration;

import sagex.api.MediaFileAPI;
import sagex.phoenix.fanart.IMetadataSearchResult;
import test.junit.lib.InitBMT;

public class MetadataPluginTestCase extends TestCase {
    String PID="testplugin";

    public MetadataPluginTestCase() {
    }

    public MetadataPluginTestCase(String name) {
        super(name);
    }
    
    public void testOnDemanSearch() throws FileNotFoundException, IOException {
        try {
            FileUtils.deleteDirectory(makeDir("test"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        File fanartDir = makeDir("test/Fanart");
        
        Object mf = MediaFileAPI.AddMediaFile(makeFile("test/Movies/Terminator.avi"), "Movies");
        assertEquals("mediafile not added", "Terminator", MediaFileAPI.GetMediaTitle(mf));
        
        phoenix.api.SetFanartCentralFolder(fanartDir);
        phoenix.api.SetIsFanartEnabled(true);
        
        // setup the stub provider
        setStubProvider();
        
        IMetadataSearchResult[] results = phoenix.api.GetMetadataSearchResults(PID, mf);
        assertEquals("search results failed", 1, results.length);

        // udpate the metadata...
        phoenix.api.UpdateMediaFileMetadata(mf, results[0]);

        // test central fanart worked
        getFile("test/Fanart/Movies/The Terminator/Backgrounds/Terminator.jpg");
        getFile("test/Fanart/Movies/The Terminator/Banners/Terminator.jpg");
        getFile("test/Fanart/Movies/The Terminator/Posters/Terminator.jpg");

        // test that it wrote a properties files
        File propFile = getFile("test/Movies/Terminator.avi.properties");
        Properties props = new Properties();
        props.load(new FileInputStream(propFile));
        assertEquals("MediaTitle incorrect", "The Terminator", props.getProperty(SageProperty.MEDIA_TITLE.sageKey));
        assertEquals("MediaTitle incorrect", "The Terminator", props.getProperty(SageProperty.DISPLAY_TITLE.sageKey));
        
        // save the file stamp on the properties file, search and test again, test that the on demand search will overwrite.
        long lastModified = propFile.lastModified();
        results = phoenix.api.GetMetadataSearchResults(PID, mf);
        assertEquals("search results failed", 1, results.length);
        phoenix.api.UpdateMediaFileMetadata(mf, results[0]);
        propFile = getFile("test/Movies/Terminator.avi.properties");
        assertNotSame("propfile did not update!", lastModified , propFile.lastModified());
    }
    
    public void testMediaFilePlugin() throws FileNotFoundException, IOException {
        try {
            FileUtils.deleteDirectory(makeDir("test"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        File fanartDir = makeDir("test/Fanart");
        
        Object mf = MediaFileAPI.AddMediaFile(makeFile("test/Movies/The Terminator (1984).avi"), "Movies");
        assertEquals("mediafile not added", "The Terminator (1984)", MediaFileAPI.GetMediaTitle(mf));

        Object mf2 = MediaFileAPI.AddMediaFile(makeFile("test/Movies/Finding Nemo.avi"), "Movies");
        
        phoenix.api.SetFanartCentralFolder(fanartDir);
        phoenix.api.SetIsFanartEnabled(true);
        
        // setup the stub provider
        setStubProvider();

        PluginConfiguration pluginConf = new PluginConfiguration();
        MetadataUpdaterPlugin plugin = new MetadataUpdaterPlugin();
        Object results = plugin.extractMetadata(getFile("test/Movies/The Terminator (1984).avi"), null);
        
        try {
            Thread.currentThread().sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // test central fanart worked
        listFiles("test/Fanart/Movies/The Terminator/Backgrounds/", "\\.jpg");
        //getFile("test/Fanart/Movies/The Terminator/Banners/The Terminator (1984).jpg");
        listFiles("test/Fanart/Movies/The Terminator/Posters/", "\\.jpg");

        // test that it wrote a properties files
        File propFile = getFile("test/Movies/The Terminator (1984).avi.properties");
        Properties props = new Properties();
        props.load(new FileInputStream(propFile));
        assertEquals("MediaTitle incorrect", "The Terminator", props.getProperty(SageProperty.MEDIA_TITLE.sageKey));
        assertEquals("MediaTitle incorrect", "The Terminator", props.getProperty(SageProperty.DISPLAY_TITLE.sageKey));

        // test the a second attempt does not update the properties
        long lastModified = propFile.lastModified();
        results = plugin.extractMetadata(getFile("test/Movies/The Terminator (1984).avi"), null);
        assertNotNull("No metadata returned", results);
        assertTrue("result is not a Map", results instanceof Map);
        propFile = getFile("test/Movies/The Terminator (1984).avi.properties");
        assertEquals("propfile was updated!", lastModified , propFile.lastModified());
        
        // update the plugin to NOT accept videos, and try again...
        pluginConf.setSupportedMediaTypes("dvd, bluray");
        results = plugin.extractMetadata(getFile("test/Movies/Finding Nemo.avi"), null);
        
        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        File f = getFile("test/Movies/Finding Nemo.avi.properties", false);
        assertFalse("File Should have been excluded!", f.exists());
    }
    
    private void setStubProvider() throws MalformedURLException {
        // TODO: setup so that stub provider works for tv, movies, and music
    }
    
    @Override
    protected void setUp() throws Exception {
        InitBMT.initBMT();
    }
    
}
