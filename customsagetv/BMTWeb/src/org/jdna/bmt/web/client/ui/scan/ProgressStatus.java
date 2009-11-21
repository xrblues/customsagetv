package org.jdna.bmt.web.client.ui.scan;

import java.io.Serializable;
import java.util.List;

import org.jdna.bmt.web.client.media.GWTMediaFile;

public class ProgressStatus implements Serializable {
    private String statusId;
    
    private String status;
    private double complete;
    private boolean done;
    private boolean cancelled;
    private int totalWork;
    private int worked;
    
    private List<GWTMediaFile> items;
    
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
    public String getStatusId() {
        return statusId;
    }
    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
    
    public List<GWTMediaFile> getItems() {
        return items;
    }

    public void setItems(List<GWTMediaFile> items) {
        this.items=items;
    }
}
