package test.junit;

import org.junit.Test;
import static org.junit.Assert.*;

import sagex.api.metadata.ISageProperty;
import sagex.api.metadata.SageMetadata;


public class TestSageMetadata {
    @Test
    public void testSageMetadata() {
        System.out.println("Dumping Sage Metadata Keys");
        for (ISageProperty p : SageMetadata.properties()) {
            System.out.printf("%s: %s\n", p.key(), p.type());
        }
        assertTrue("SageMetadata is Empty", SageMetadata.properties().size()>0);
    }
}
