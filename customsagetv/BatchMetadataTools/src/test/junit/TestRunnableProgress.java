package test.junit;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.jdna.util.IProgressMonitor;
import org.jdna.util.IRunnableWithProgress;
import org.jdna.util.ProgressTracker;
import org.jdna.util.ProgressTrackerManager;

public class TestRunnableProgress extends TestCase {
    public TestRunnableProgress() {
    }

    public TestRunnableProgress(String name) {
        super(name);
    }
    
    public void testRunnableProgress() {
        BasicConfigurator.configure();
        
        ProgressTrackerManager mgr = ProgressTrackerManager.getInstance();
        
        IRunnableWithProgress<ProgressTracker<String>> runnable = new IRunnableWithProgress<ProgressTracker<String>>() {
            public void run(ProgressTracker<String> monitor) {
                monitor.beginTask("Working", 10);
                for (int i=0;i<10;i++) {
                    if (monitor.isCancelled()) return;
                    
                    monitor.setTaskName("Working on: " + i);
                    monitor.worked(1);
                    monitor.addSuccess(String.valueOf(i));
                    try {
                        Thread.currentThread();
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                monitor.done();
            }
        };
        
        
        String id = mgr.runWithProgress(runnable, new ProgressTracker<String>());
        System.out.println("Tracker Started");
        
        IProgressMonitor monitor = mgr.getProgress(id);
        while (!monitor.isDone()) {
            System.out.printf("%s; %s\n", monitor.getTaskName(), monitor.internalWorked());
            try {
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        assertEquals(true, monitor.isDone());
        
        ProgressTracker<String> tracker = (ProgressTracker<String>) monitor;
        assertEquals(10, tracker.getSuccessfulItems().size());
        
        mgr.removeProgress(id);
        assertTrue(mgr.getProgress(id)==null);
    }

}
