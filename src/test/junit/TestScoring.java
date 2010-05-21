package test.junit;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.BasicConfigurator;
import org.jdna.media.metadata.MetadataUtil;
import org.junit.Test;


public class TestScoring {

    @Test
    public void testScoring() {
        BasicConfigurator.configure();
        float score = MetadataUtil.calculateScore("24", "24");
        assertEquals(1.0, score,0);
        
        // this was failing, but should now be fixed in the scoring code.
        score = MetadataUtil.calculateCompressedScore("24", "24");
        assertEquals(1.0, score,0);
    }
}
