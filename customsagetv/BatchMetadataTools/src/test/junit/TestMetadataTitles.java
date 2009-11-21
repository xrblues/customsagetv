package test.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.ws.ServiceMode;

import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.FileMediaFile;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery.Type;
import org.jdna.media.metadata.impl.StubMetadataProvider;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.util.ProgressTracker;
import org.junit.Before;
import org.junit.Test;

import sagex.phoenix.configuration.proxy.GroupProxy;
import test.junit.lib.InitBMT;


public class TestMetadataTitles {
    private String PID = "metadataTitlesTest";
    private IMediaMetadata metadata = null;
    
    private class StubPersistence implements IMediaMetadataPersistence {

        public String getDescription() {
            return null;
        }

        public String getId() {
            // TODO Auto-generated method stub
            return null;
        }

        public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
            return null;
        }

        public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
            System.out.println("Saving...");
            metadata = md;
        }
    }
    
    @Test
    public void testMetadataTitles() throws Exception {
        assertEquals(new MetadataID("test:1234"), new MetadataID("test:1234"));
        
        setStubProvider();
        ConfigurationManager.getInstance().setMetadataIdForTitle("Terminator", new MetadataID(PID+ ":1234"));
        ConfigurationManager.getInstance().setMetadataIdForTitle("Terminator2", new MetadataID(PID+ ":5678"));
        ProgressTracker<IMediaFile> tracker = new ProgressTracker<IMediaFile>();
        AutomaticUpdateMetadataVisitor vis = new AutomaticUpdateMetadataVisitor(PID, new StubPersistence(), new PersistenceOptions(), Type.MOVIE, tracker);
        
     
        metadata=null;
        vis.visit(new FileMediaFile(new File("TerminatorDDD.avi")));
        assertNull(metadata);

        // should find a match from the results
        metadata=null;
        vis.visit(new FileMediaFile(new File("Terminator.avi")));
        assertNotNull(metadata);
        assertEquals(PID + ":1234", metadata.getString(MetadataKey.METADATA_PROVIDER_ID));

        // shoudld find a match from the metadata id, not the results
        metadata=null;
        vis.visit(new FileMediaFile(new File("Terminator2.avi")));
        assertNotNull(metadata);
        assertEquals(PID + ":5678", metadata.getString(MetadataKey.METADATA_PROVIDER_ID));
    }
    
    private void setStubProvider() throws Exception {
        MediaMetadataFactory factory  = MediaMetadataFactory.getInstance();
        
        StubMetadataProvider provider= (StubMetadataProvider) factory.getProvider(PID);
        if (provider == null) {
            provider = new StubMetadataProvider(PID, "Test Plugin Provider");
            factory.addMetaDataProvider(provider);
        }
        provider = (StubMetadataProvider) factory.getProvider(PID);
        if (provider==null) {
            fail("Could not register stub metadata provider");
        }
        provider.reset();
        
        // use the stub provider
        GroupProxy.get(MetadataConfiguration.class).setDefaultProviderId(PID);

        // add in a movie to the stub provider
        MediaSearchResult res = new MediaSearchResult(PID, 0.3f);
        res.setUrl("test://1");
        res.setYear("2008");
        res.setTitle("Terminator");
        res.setMetadataId(new MetadataID(PID + ":1234"));
        
        MediaMetadata md = new MediaMetadata();
        md.set(MetadataKey.DISPLAY_TITLE, "The Terminator");
        md.set(MetadataKey.MEDIA_TITLE, "The Terminator");
        md.set(MetadataKey.METADATA_PROVIDER_ID, res.getMetadataId().toIDString());
        provider.addMetadata(res, md);

        // this one is not int he results
        MetadataID mid = new MetadataID(PID + ":5678");
        md = new MediaMetadata();
        md.set(MetadataKey.DISPLAY_TITLE, "The Terminator B");
        md.set(MetadataKey.MEDIA_TITLE, "The Terminator B");
        md.set(MetadataKey.METADATA_PROVIDER_ID, mid.toIDString());
        md.set(MetadataKey.METADATA_PROVIDER_DATA_URL, "URL:" + mid.toIDString());
        provider.addMetadata(mid, md);
        assertNotNull(factory.getProvider(mid.getKey()).getUrlForId(mid));
        assertNotNull(factory.getProvider(mid.getKey()).getUrlForId(new MetadataID(PID + ":5678")));
        
        provider.addMetadata(MetadataAPI.getProviderDataUrl(md), md);
    }

    @Before
    public void setUp() throws Exception {
        InitBMT.initBMT();
    }
}
