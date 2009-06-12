package org.jdna.util;

import java.util.LinkedList;
import java.util.List;

public class ProgressTracker<T> implements IProgressMonitor {
    public static class FailedItem<T2> {
        public T2 item;
        public Throwable error;
        public String message;
        
        public FailedItem(T2 item, String msg) {
            this(item,msg,null);
        }

        public FailedItem(T2 item, String msg, Throwable error) {
            this.item=item;
            this.message=msg;
            this.error=error;
        }
        
        public T2 getItem() {
            return item;
        }
        public Throwable getError() {
            return error;
        }
        public String getMessage() {
            return message;
        }
    }
    
    private IProgressMonitor monitor = null;
    private List<T> success = new LinkedList<T>();
    private List<FailedItem<T>> failed = new LinkedList<FailedItem<T>>();
    
    public ProgressTracker() {
        this(new BasicProgressMonitor());
    }
    
    public ProgressTracker(IProgressMonitor monitor) {
        this.monitor = monitor;
    }
    
    public void addSuccess(T item) {
        success.add(item);
    }
    
    public void addFailed(T item, String msg) {
        addFailed(item, msg, null);
    }
    
    public void addFailed(T item, String msg, Throwable t) {
        failed.add(new FailedItem<T>(item, msg, t));
    }
    
    public List<T> getSuccessfulItems() {
        return success;
    }
    
    public List<FailedItem<T>> getFailedItems() {
        return failed;
    }

    public void beginTask(String name, int worked) {
        monitor.beginTask(name, worked);
    }

    public void done() {
        monitor.done();
    }

    public boolean isCancelled() {
        return monitor.isCancelled();
    }

    public void setCancelled(boolean cancel) {
        monitor.setCancelled(cancel);
    }

    public void worked(int worked) {
        monitor.worked(worked);
    }

    public double internalWorked() {
        return monitor.internalWorked();
    }

    public String getTaskName() {
        return monitor.getTaskName();
    }

    public void setTaskName(String name) {
        monitor.setTaskName(name);
    }
}
