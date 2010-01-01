package test.junit;

import static org.junit.Assert.assertEquals;
import static test.junit.lib.FilesTestCase.makeFile;

import java.io.File;
import java.util.Map;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.sage.SageMetadataConfiguration;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.junit.Test;

import sagex.SageAPI;
import sagex.api.MediaFileAPI;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.impl.FileResourceFactory;
import sagex.stub.StubSageAPI;

public class TestNormalizeMetadata {
    @Test
    public void testNormalizeMetadataNoImportNoTitleMask() {
        IMediaMetadata md = getTVMetadata();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(false);
        options.setUseTitleMasks(false);
        
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        assertEquals("Futurama", MetadataAPI.getDisplayTitle(md));
    }

    @Test
    public void testNormalizeMetadataNoImportTVUseTitleMask() {
        IMediaMetadata md = getTVMetadata();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(false);
        options.setUseTitleMasks(true);
        
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        assertEquals("Futurama - S04E08 - Bender Gets Made", MetadataAPI.getDisplayTitle(md));
    }

    @Test
    public void testNormalizeMetadataImportTVUseTitleMask() {
        IMediaMetadata md = getTVMetadata();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(true);
        options.setUseTitleMasks(true);
        
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        assertEquals("Futurama", MetadataAPI.getDisplayTitle(md));
    }
    
    @Test
    public void testNormalizeMetadataImportTVNoUseTitleMask() {
        IMediaMetadata md = getTVMetadata();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(true);
        options.setUseTitleMasks(false);
        
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        assertEquals("Futurama", MetadataAPI.getDisplayTitle(md));
    }

    @Test
    public void testNormalizeMetadataNoImportTVUseTitleMaskDisc() {
        IMediaMetadata md = getTVMetadataForDisc();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(false);
        options.setUseTitleMasks(true);
        
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        assertEquals("Futurama - S04D08", MetadataAPI.getDisplayTitle(md));
    }

    @Test
    public void testNormalizeMetadataNoImportTVUseTitleMaskDiscMultiples() {
        IMediaMetadata md = getTVMetadataForDisc();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(false);
        options.setUseTitleMasks(true);
        
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        
        assertEquals("Futurama - S04D08", MetadataAPI.getDisplayTitle(md));
    }

    @Test
    public void testPropertiesMap() {
        IMediaMetadata md = getTVMetadataForDisc();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(false);
        options.setUseTitleMasks(true);
        
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        assertEquals("Futurama - S04D08", MetadataAPI.getDisplayTitle(md));
        Map<String,String> props = SageTVPropertiesPersistence.getSageTVMetadataMap(getTVMediaFile(), md, options);
        assertEquals("Futurama - S04D08", props.get(SageProperty.DISPLAY_TITLE.sageKey));
        assertEquals("4", props.get(SageProperty.SEASON_NUMBER.sageKey));
        assertEquals("8", props.get(SageProperty.DISC.sageKey));
    }

    @Test
    public void testPropertiesMapSeaonEp() {
        IMediaMetadata md = getTVMetadata();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(false);
        options.setUseTitleMasks(true);
        
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        assertEquals("Futurama - S04E08 - Bender Gets Made", MetadataAPI.getDisplayTitle(md));
        Map<String,String> props = SageTVPropertiesPersistence.getSageTVMetadataMap(getTVMediaFile(), md, options);
        assertEquals("Futurama - S04E08 - Bender Gets Made", props.get(SageProperty.DISPLAY_TITLE.sageKey));
        assertEquals("4", props.get(SageProperty.SEASON_NUMBER.sageKey));
        assertEquals("8", props.get(SageProperty.EPISODE_NUMBER.sageKey));
        assertEquals("Bender Gets Made", props.get(SageProperty.EPISODE_TITLE.sageKey));
    }
    
    @Test
    public void testRewriteTitle() {
        SageAPI.setProvider(new StubSageAPI());
        
        GroupProxy.get(SageMetadataConfiguration.class).setRewriteTitle(true);
        
        IMediaMetadata md = getMovieMetadata();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(false);
        options.setUseTitleMasks(true);
        
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        MetadataAPI.normalizeMetadata(getTVMediaFile(), md, options);
        assertEquals("Robot, I", MetadataAPI.getDisplayTitle(md));
        assertEquals("I Robot", MetadataAPI.getMediaTitle(md));
        
        Map<String,String> props = SageTVPropertiesPersistence.getSageTVMetadataMap(getTVMediaFile(), md, options);
        assertEquals("Robot, I", props.get(SageProperty.DISPLAY_TITLE.sageKey));
        assertEquals("I Robot", props.get(SageProperty.MEDIA_TITLE.sageKey));
    }
    
    @Test
    public void testPropertiesMapWithASageMediaFile() {
        StubSageAPI provider = new StubSageAPI();
        SageAPI.setProvider(provider);
        Object mfObj = MediaFileAPI.AddMediaFile(makeFile("TV/bender.avi"), "TV");
        IMediaFile smf = phoenix.api.GetMediaFile(mfObj);
        
        IMediaMetadata md = getTVMetadataForDisc();

        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(true);
        options.setUseTitleMasks(true);
        
        MetadataAPI.normalizeMetadata(smf, md, options);
        assertEquals("Futurama", MetadataAPI.getDisplayTitle(md));
        Map<String,String> props = SageTVPropertiesPersistence.getSageTVMetadataMap(getTVMediaFile(), md, options);
        assertEquals("Futurama", props.get(SageProperty.DISPLAY_TITLE.sageKey));
        assertEquals("4", props.get(SageProperty.SEASON_NUMBER.sageKey));
        assertEquals("8", props.get(SageProperty.DISC.sageKey));
    }
    
    private IMediaFile getTVMediaFile() {
        return (IMediaFile) FileResourceFactory.createResource(new File("/tmp/futurama-bendergetsmade.avi"));
    }
    
    private IMediaMetadata getTVMetadata() {
        MediaMetadata md = new MediaMetadata();
        MetadataAPI.setMediaTitle(md , "Futurama");
        MetadataAPI.setEpisodeTitle(md, "Bender Gets Made");
        MetadataAPI.setMediaType(md, "TV");
        MetadataAPI.setSeason(md, "4");
        MetadataAPI.setEpisode(md, "8");
        return md;
    }

    private IMediaMetadata getMovieMetadata() {
        MediaMetadata md = new MediaMetadata();
        MetadataAPI.setMediaTitle(md , "I Robot");
        MetadataAPI.setMediaType(md, "Movie");
        return md;
    }

    private IMediaMetadata getTVMetadataForDisc() {
        MediaMetadata md = new MediaMetadata();
        MetadataAPI.setMediaTitle(md , "Futurama");
        MetadataAPI.setEpisodeTitle(md, "Bender Gets Made");
        MetadataAPI.setMediaType(md, "TV");
        MetadataAPI.setSeason(md, "4");
        MetadataAPI.setDisc(md, "8");
        return md;
    }
}
