package org.jdna.util;

/**
 * Simple Progress Monitor Interfact for tracking progress.  Loosely based on the Eclipse Progress Monitor.
 * 
 * @author seans
 *
 */
public interface IProgressMonitor {
    public static final int UNKNOWN=-1;
    public void beginTask(String name, int worked);
    public void setTaskName(String name);
    public String getTaskName();
    public void done();
    
    /**
     * represents an incremental amount of work done.
     * @param worked
     */
    public void worked(int worked);
    
    public void setCancelled(boolean cancel);
    public boolean isCancelled();
    
    /**
     * Return the % complete, ie, a number from 0.0 to 1.0 representing the amount of work complete.
     * @return
     */
    public double internalWorked();
}
