package test.junit;

import junit.framework.TestCase;

import org.jdna.util.BasicProgressMonitor;
import org.jdna.util.IProgressMonitor;
import org.jdna.util.ProgressTracker;
import org.jdna.util.ProgressTracker.FailedItem;

public class ProgressMonitorTestCase extends TestCase {
    public ProgressMonitorTestCase() {
    }

    public ProgressMonitorTestCase(String name) {
        super(name);
    }
    
    public void testProgressMonitor() {
        IProgressMonitor mon = new BasicProgressMonitor();
        mon.beginTask("Test", 100);
        assertEquals("Test", mon.getTaskName());
        assertEquals(0.0, mon.internalWorked());
        
        mon.worked(10);
        assertEquals(0.10, mon.internalWorked());

        // worked in cumulative
        mon.worked(10);
        assertEquals(0.20, mon.internalWorked());
        
        // should push it over the top
        mon.worked(100);
        assertEquals(1.0, mon.internalWorked());
    }
    
    public void testProgressTracker() {
        ProgressTracker<String> track = new ProgressTracker<String>(new BasicProgressMonitor());
        track.addSuccess("Test");
        track.addFailed("TestFailed", "Failed");
        assertEquals(1, track.getSuccessfulItems().size());
        assertEquals(1, track.getFailedItems().size());
        
        FailedItem<String> item =  track.getFailedItems().get(0);
        assertEquals("TestFailed", item.getItem());
    }
}
