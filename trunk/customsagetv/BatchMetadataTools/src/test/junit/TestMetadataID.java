package test.junit;

import org.jdna.media.metadata.MetadataID;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestMetadataID {
    @Test
    public void testSimpleMetadataID() {
        MetadataID mid = new MetadataID();
        mid.setKey("imdb");
        mid.setId("tt1234");
        
        assertEquals("imdb:tt1234", mid.toIDString());
        MetadataID mid2 = new MetadataID(mid.toIDString());
        assertEquals("imdb", mid2.getKey());
        assertEquals("tt1234", mid2.getId());
    }
    
    @Test
    public void testMetadataIDWithArgs() {
        MetadataID mid = new MetadataID();
        mid.setKey("tvdb");
        mid.setId("1234");
        mid.addArg("SEASON", "2");
        mid.addArg("EPISODE", "4");
        
        System.out.println("ID: " + mid);
        
        MetadataID mid2 = new MetadataID(mid.toIDString());
        assertEquals("tvdb", mid2.getKey());
        assertEquals("1234", mid2.getId());
        assertEquals("2", mid2.getArg("SEASON"));
        assertEquals("4", mid2.getArg("EPISODE"));
    }
}
