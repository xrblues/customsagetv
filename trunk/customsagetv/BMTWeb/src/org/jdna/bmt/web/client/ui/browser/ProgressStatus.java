package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProgressStatus implements Serializable {
    private String status;
    private double complete;
    private List<MediaResult> items = new ArrayList<MediaResult>();
    private boolean done;
    private boolean cancelled;
    private int totalWork;
    private int worked;
    
    public ProgressStatus() {
        super();
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public double getComplete() {
        return complete;
    }
    public void setComplete(double complete) {
        this.complete = complete;
    }
    public List<MediaResult> getItems() {
        return items;
    }
    public void setIsDone(boolean done) {
        this.done=done;
    }
    
    public boolean isDone() {
        return done;
    }
    public void setIsCancelled(boolean cancelled) {
        this.cancelled=cancelled;
    }
    public boolean isCancelled() {
        return cancelled;
    }
    public int getTotalWork() {
        return totalWork;
    }
    public void setTotalWork(int totalWork) {
        this.totalWork = totalWork;
    }
    public int getWorked() {
        return worked;
    }
    public void setWorked(int worked) {
        this.worked = worked;
    }
}
