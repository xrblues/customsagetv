package org.jdna.process;

import java.util.List;

import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.progress.TrackedItem;

public class ProgressSingleton {
    private static final ProgressTracker<MetadataItem> tracker = new ProgressTracker<MetadataItem>();
    public static final ProgressTracker<MetadataItem> getTracker() {
        return tracker;
    }
    
    public static int getFailedCount() {
        return getTracker().getFailedItems().size();
    }

    public static int getSuccessCount() {
        return getTracker().getSuccessfulItems().size();
    }
    
    public static void clearSuccess() {
        getTracker().getSuccessfulItems().clear();
    }

    public static void clearFailed() {
        getTracker().getFailedItems().clear();
    }
    
    public static List<TrackedItem<MetadataItem>> getSuccess() {
        return getTracker().getSuccessfulItems();
    }

    public static List<TrackedItem<MetadataItem>> getFailed() {
        return getTracker().getFailedItems();
    }
}
