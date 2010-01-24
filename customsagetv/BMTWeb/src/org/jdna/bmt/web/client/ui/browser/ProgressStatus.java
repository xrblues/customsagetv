package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;
import java.util.Date;

public class ProgressStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private String progressId;
    
    private String status;
    private double complete;
    private boolean done;
    private boolean cancelled;
    private int totalWork;
    private int worked;
    private int successCount;
    private int failedCount;
    private Date date;

    private String label;
    
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
    public String getProgressId() {
        return progressId;
    }
    public void setProgressId(String statusId) {
        this.progressId = statusId;
    }
    /**
     * @return the successCount
     */
    public int getSuccessCount() {
        return successCount;
    }
    /**
     * @param successCount the successCount to set
     */
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    /**
     * @return the failedCount
     */
    public int getFailedCount() {
        return failedCount;
    }
    /**
     * @param failedCount the failedCount to set
     */
    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }
    
    public void setLabel(String label) {
        this.label=label;
    }
    
    public String getLabel() {
        return label;
    }
    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }
}
