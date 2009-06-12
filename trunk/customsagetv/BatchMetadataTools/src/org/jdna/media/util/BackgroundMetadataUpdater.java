package org.jdna.media.util;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.util.ProgressTracker;

/**
 * A Manager for running background Automatic Updater processes.  There should only be a single instance of this thread.
 * 
 * @author seans
 *
 */
public class BackgroundMetadataUpdater extends Thread {
    private static final Logger log = Logger.getLogger(BackgroundMetadataUpdater.class);
    private static BackgroundMetadataUpdater instance = new BackgroundMetadataUpdater();
    
    private IMediaFolder folder;
    private String provider;
    private boolean isRunning = false;
    private IMediaMetadataPersistence persistence = null;
    private PersistenceOptions options = null;
    private ProgressTracker<IMediaFile> tracker;
    
    protected BackgroundMetadataUpdater() {
    }

    @Override
    public void run() {
        try {
            tracker.beginTask("Scanning " + folder.members().size() + " using Provider: " + provider, folder.members().size());
            log.info("Background Metadata Scan Started for: " + folder.getName());
            isRunning=true;
            AutomaticUpdateMetadataVisitor vis = new AutomaticUpdateMetadataVisitor(provider, persistence, options, null, tracker);
            folder.accept(vis);
        } catch (Exception e) {
            log.error("Background Metadata Scan Failed.", e);
        } finally {
            isRunning=false;
            log.info("Background Metadata Scan completed.");
            tracker.done();
        }
    }

    /**
     * Background Scanner can only be started by calling startScan().  It will ensure that only 1 scan is proceeding at any given time.
     * 
     * @param folder Folder to scan
     * @param provider Metadata Provider to use
     * @param persistence Persistence to use for storing metadata
     * @param options Persistence options
     * @return true if the scan was started - false if the scan was not started
     */
    public synchronized static boolean startScan(IMediaFolder folder, String provider, IMediaMetadataPersistence persistence, PersistenceOptions options, ProgressTracker<IMediaFile> tracker) throws Exception {
        if (isRunning()) {
            log.warn("A Scan is in progress, and another cannot be started until this one completes.  Current Progress: " + tracker.internalWorked());
            return false;
        }
        
        log.debug("Starting a new Scan using provider: " + provider + " for " + folder.members().size() + " items.");
        instance.isRunning=true;
        instance.persistence = persistence;
        instance.options = options;
        instance.folder = folder;
        instance.provider = provider;
        instance.tracker = tracker;
        instance.start();
        
        return true;
    }
    
    public static boolean isRunning() {
        return instance.isRunning;
    }
    
    public ProgressTracker<IMediaFile> getTracker() {
        return tracker;
    }
    
    public static BackgroundMetadataUpdater getInstance() {
        return instance;
    }
}
