package org.jdna.util;

/**
 * Implements the most basic progress monitor possible.
 * 
 * @author seans
 *
 */
public class BasicProgressMonitor implements IProgressMonitor {
    private boolean cancelled = false;
    private String task;
    private int totalWork;
    private int worked;
    private int unknownTotal=1000;
    private boolean done = false;
    
    public BasicProgressMonitor() {
        totalWork=IProgressMonitor.UNKNOWN;
    }
    
    public void beginTask(String name, int worked) {
        this.task=name;
        this.totalWork=worked;
    }

    public void done() {
        done=true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled=cancel;
    }

    public void worked(int worked) {
        this.worked+=worked;
    }

    public double internalWorked() {
        if (totalWork==UNKNOWN) {
            worked=worked % unknownTotal;
            if (worked==0||unknownTotal==0) return 0;
            return worked/unknownTotal;
        } else {
            if (worked==0 || totalWork==0) return 0;
            if (worked>=totalWork) return 1;
            return (double)worked/(double)totalWork;
        }
    }

    public String getTaskName() {
        return task;
    }

    public void setTaskName(String name) {
        this.task = name;
    }
    
    public boolean isDone() {
        return done;
    }

    public int getTotalWork() {
        return totalWork;
    }

    public int getWorked() {
        return worked;
    }
}
