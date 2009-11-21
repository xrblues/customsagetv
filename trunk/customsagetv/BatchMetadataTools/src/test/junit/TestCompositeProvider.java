package test.junit;

import org.jdna.media.metadata.impl.StubMetadataProvider;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestCompositeProvider {
    @Test
    public void testComposite() {
        StubMetadataProvider p1 = new StubMetadataProvider("stub1", "Stub Provider 1");
    }
}
