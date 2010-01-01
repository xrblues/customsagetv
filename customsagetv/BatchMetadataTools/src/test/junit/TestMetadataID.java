package test.junit;

import static org.junit.Assert.assertEquals;

import org.jdna.media.metadata.MetadataID;
import org.junit.Test;


public class TestMetadataID {
    @Test
    public void testSimpleMetadataID() {
        MetadataID mid = new MetadataID();
        mid.setProvider("imdb");
        mid.setId("tt1234");
        
        assertEquals("imdb:tt1234", mid.toIDString());
        MetadataID mid2 = new MetadataID(mid.toIDString());
        assertEquals("imdb", mid2.getProvider());
        assertEquals("tt1234", mid2.getId());
    }
    
    @Test
    public void testMetadataIDWithArgs() {
        MetadataID mid = new MetadataID();
        mid.setProvider("tvdb");
        mid.setId("1234");
        mid.addArg("SEASON", "2");
        mid.addArg("EPISODE", "4");
        
        System.out.println("ID: " + mid);
        
        MetadataID mid2 = new MetadataID(mid.toIDString());
        assertEquals("tvdb", mid2.getProvider());
        assertEquals("1234", mid2.getId());
        assertEquals("2", mid2.getArg("SEASON"));
        assertEquals("4", mid2.getArg("EPISODE"));
    }
}
