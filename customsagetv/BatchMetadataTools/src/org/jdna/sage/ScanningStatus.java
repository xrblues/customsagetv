package org.jdna.sage;

import org.jdna.media.IMediaFile;
import org.jdna.util.ProgressTracker;

public class ScanningStatus extends ProgressTracker<IMediaFile> {
    private static ScanningStatus instance = new ScanningStatus();
    
    public static ScanningStatus getInstance() {
        return instance;
    }
    
    private int MAX=5;
    private long lastScanTime=0;
    private int totalScannedFiles=0;
    private int totalFailed = 0;
    
    public long getLastScanTime() {
        return lastScanTime;
    }
    
    public int getTotalScanned() {
        return totalScannedFiles;
    }
    
    public int getTotalFailed() {
        return totalFailed;
    }
    
    public int getMaxFailed() {
        return MAX;
    }

    /**
     * Only tracks the last MAX items failed.  Default is 5.
     */
    @Override
    public void addFailed(IMediaFile item, String msg, Throwable t) {
        totalFailed++;
        lastScanTime=System.currentTimeMillis();
        if (getFailedItems().size()<MAX) {
            getFailedItems().add(new FailedItem<IMediaFile>(item, msg, t));
        } else {
            getFailedItems().set(totalFailed%MAX, new FailedItem<IMediaFile>(item, msg, t));
        }
    }

    /**
     *  Only tracks the Last Item for the Success Scan
     */
    @Override
    public void addSuccess(IMediaFile item) {
        lastScanTime=System.currentTimeMillis();
        if (getSuccessfulItems().size()==0) {
            getSuccessfulItems().add(0, item);
        } else {
            getSuccessfulItems().set(0, item);
        }
    }

    @Override
    public void worked(int worked) {
        super.worked(worked);
        totalScannedFiles+=worked;
        lastScanTime=System.currentTimeMillis();
    }

    @Override
    public void done() {
        super.done();
        lastScanTime=System.currentTimeMillis();
    }

    @Override
    public void setTaskName(String name) {
        super.setTaskName(name);
        lastScanTime=System.currentTimeMillis();
    }
}
