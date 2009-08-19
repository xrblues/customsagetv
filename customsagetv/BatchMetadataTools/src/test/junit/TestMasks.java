package test.junit;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.impl.sage.SageMetadataConfiguration;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.junit.Test;


public class TestMasks {

    @Test
    public void testMasks() {
        BasicConfigurator.configure();
        MediaMetadata md = new MediaMetadata();
        MetadataAPI.setMediaTitle(md, "Show Title");
        MetadataAPI.setSeason(md, "01");
        MetadataAPI.setEpisode(md, "02");
        MetadataAPI.setEpisodeTitle(md, "EP Title");
        
        Map<String, String> props = SageTVPropertiesPersistence.getSageTVMetadataMap(md);
        
        assertEquals(MetadataKey.MEDIA_TITLE, SageProperty.metadataKey(SageProperty.MEDIA_TITLE.sageKey));
        
        SageMetadataConfiguration cfg = new SageMetadataConfiguration();
        assertEquals("Show Title", MediaMetadataUtils.format(cfg.getSageTVTitleMask(), props));
        assertEquals("Show Title", MediaMetadataUtils.format(cfg.getSageTVTitleMask(), md));
    }
}
