package test.junit;

import static test.junit.FilesTestCase.getFile;
import static test.junit.FilesTestCase.makeDir;
import static test.junit.FilesTestCase.makeFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.jdna.media.VirtualMediaFile;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.impl.StubMetadataProvider;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.util.PathUtils;
import org.jdna.sage.MetadataUpdaterPlugin;
import org.jdna.sage.ScanningStatus;

import sagex.api.MediaFileAPI;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaArtifactType;

public class MetadataPluginTestCase extends TestCase {
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
        
        IMetadataSearchResult[] results = phoenix.api.GetMetadataSearchResults(StubMetadataProvider.PROVIDER_ID, mf);
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
        
        // save the file stamp on the properties file, search and test again, test that the on demand search will overwrite.
        long lastModified = propFile.lastModified();
        results = phoenix.api.GetMetadataSearchResults(StubMetadataProvider.PROVIDER_ID, mf);
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
        
        Object mf = MediaFileAPI.AddMediaFile(makeFile("test/Movies/Terminator.avi"), "Movies");
        assertEquals("mediafile not added", "Terminator", MediaFileAPI.GetMediaTitle(mf));
        
        phoenix.api.SetFanartCentralFolder(fanartDir);
        phoenix.api.SetIsFanartEnabled(true);
        
        // setup the stub provider
        setStubProvider();

        MetadataUpdaterPlugin plugin = new MetadataUpdaterPlugin();
        Object results = plugin.extractMetadata(getFile("test/Movies/Terminator.avi"), null);
        assertNotNull("No metadata returned", results);
        assertTrue("result is not a Map", results instanceof Map);
        
        // test central fanart worked
        getFile("test/Fanart/Movies/The Terminator/Backgrounds/Terminator.jpg");
        getFile("test/Fanart/Movies/The Terminator/Banners/Terminator.jpg");
        getFile("test/Fanart/Movies/The Terminator/Posters/Terminator.jpg");

        // test that it wrote a properties files
        File propFile = getFile("test/Movies/Terminator.avi.properties");
        Properties props = new Properties();
        props.load(new FileInputStream(propFile));
        assertEquals("MediaTitle incorrect", "The Terminator", props.getProperty(SageProperty.MEDIA_TITLE.sageKey));

        // test the a second attempt does not update the properties
        long lastModified = propFile.lastModified();
        results = plugin.extractMetadata(getFile("test/Movies/Terminator.avi"), null);
        assertNotNull("No metadata returned", results);
        assertTrue("result is not a Map", results instanceof Map);
        propFile = getFile("test/Movies/Terminator.avi.properties");
        assertEquals("propfile was updated!", lastModified , propFile.lastModified());
    }
    
    private void setStubProvider() throws MalformedURLException {
        MediaMetadataFactory factory  = MediaMetadataFactory.getInstance();

        StubMetadataProvider provider= (StubMetadataProvider) factory.getProvider(StubMetadataProvider.PROVIDER_ID);
        if (provider == null) {
            provider = new StubMetadataProvider();
            factory.addMetaDataProvider(provider);
        }
        provider = (StubMetadataProvider) factory.getProvider(StubMetadataProvider.PROVIDER_ID);
        if (provider==null) {
            fail("Could not register stub metadata provider");
        }
        provider.reset();
        
        // use the stub provider
        GroupProxy.get(MetadataConfiguration.class).setDefaultProviderId(StubMetadataProvider.PROVIDER_ID);

        // add in a movie to the stub provider
        MediaSearchResult res = new MediaSearchResult(StubMetadataProvider.PROVIDER_ID, 0.9f);
        res.setUrl("test://1");
        res.setYear("2008");
        res.setTitle("Terminator");
        
        MediaMetadata md = new MediaMetadata();
        md.set(MetadataKey.DISPLAY_TITLE, "The Terminator");
        md.set(MetadataKey.MEDIA_TITLE, "The Terminator");
        
        File f = makeFile("test/tmpimages/Terminator.jpg");
        MediaArt ma = new MediaArt();
        ma.setProviderId(StubMetadataProvider.PROVIDER_ID);
        ma.setDownloadUrl(f.toURL().toExternalForm());
        ma.setType(MediaArtifactType.BACKGROUND);
        md.addMediaArt(ma);

        ma = new MediaArt();
        ma.setProviderId(StubMetadataProvider.PROVIDER_ID);
        ma.setDownloadUrl(f.toURL().toExternalForm());
        ma.setType(MediaArtifactType.BANNER);
        md.addMediaArt(ma);

        ma = new MediaArt();
        ma.setProviderId(StubMetadataProvider.PROVIDER_ID);
        ma.setDownloadUrl(f.toURL().toExternalForm());
        ma.setType(MediaArtifactType.POSTER);
        md.addMediaArt(ma);

        // create a dummy image
        BufferedImage buffer = new BufferedImage(500, 1000, BufferedImage.TYPE_INT_ARGB);
        try {
            ImageIO.write(buffer, "jpg", f);
        } catch (IOException e) {
            fail("Unable to create a dummy image");
        }
        
        provider.addMetadata(res, md);
    }
    
    public void testScanningStatus() throws URISyntaxException {
        ScanningStatus status = new ScanningStatus();
        assertEquals(0, status.getTotalScanned());
        assertEquals(0, status.getTotalFailed());
        
        VirtualMediaFile m1 = new VirtualMediaFile(PathUtils.createPath("test:/test1"));
        VirtualMediaFile m2 = new VirtualMediaFile(PathUtils.createPath("test:/test2"));
        VirtualMediaFile m3 = new VirtualMediaFile(PathUtils.createPath("test:/test3"));
        VirtualMediaFile m4 = new VirtualMediaFile(PathUtils.createPath("test:/test4"));
        status.addSuccess(m1);
        status.addSuccess(m2);
        assertEquals(1, status.getSuccessfulItems().size());
        
        status.addFailed(m3, "fail1");
        status.addFailed(m4, "fail2");
        assertEquals(2, status.getFailedItems().size());
        
        for (int i=0;i<10;i++) {
            status.addFailed(m1, "fail");
        }
        assertEquals(5, status.getFailedItems().size());
    }

    @Override
    protected void setUp() throws Exception {
        InitBMT.initBMT();
    }
    
}
