package org.jdna.media.util;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.PersistenceOptions;

public class BackgroundMetadataUpdater extends Thread {
    private static final Logger log = Logger.getLogger(BackgroundMetadataUpdater.class);
    private static BackgroundMetadataUpdater instance = new BackgroundMetadataUpdater();
    
    private int total = 0;
    private int current = 0;
    private IMediaFolder folder;
    private String provider;
    private boolean isRunning = false;
    private IMediaMetadataPersistence persistence = null;
    private PersistenceOptions options = null;
    
    protected BackgroundMetadataUpdater() {
    }

    @Override
    public void run() {
        try {
            log.info("Background Metadata Scan Started for: " + folder.getName());
            isRunning=true;
            total = folder.members().size();
            current = 0;
            // simple visitor that just increased the current item everytime it process a file
            IMediaResourceVisitor updated = new IMediaResourceVisitor() {
                public void visit(IMediaResource resource) {
                    current++;
                }
            };
            AutomaticUpdateMetadataVisitor vis = new AutomaticUpdateMetadataVisitor(provider, persistence, options, null, updated, updated);
            
            // scan
            folder.accept(vis);
        } catch (Exception e) {
            log.error("Background Metadata Scan Failed.", e);
        } finally {
            log.info("Background Metadata Scan completed.");
            total=0;
            current=0;
            isRunning=false;
        }
    }
    
    public float getPercentComplete() {
        if (current==0||total==0) return 0f;
        return (((float)current)/((float)total));
    }

    /**
     * Background Scanner can only be started by calling startScan().  It will ensure that only 1 scan is proceeding at any given time.
     * 
     * @param folder Folder to scan
     * @param provider Metadata Provider to use
     * @param persistence Persistence to use for storing metadata
     * @param options Persistence options
     * @throws Exception if the scan fails or cannot be started
     */
    public synchronized static void startScan(IMediaFolder folder, String provider, IMediaMetadataPersistence persistence, PersistenceOptions options) throws Exception {
        if (isRunning()) throw new Exception("Metadata Scanner is already running, and only 1 instance be run at a time.");
        instance.isRunning=true;
        instance.persistence = persistence;
        instance.options = options;
        instance.folder = folder;
        instance.provider = provider;
        instance.start();
    }
    
    public static float getCompleted() {
        return instance.getPercentComplete();
    }
    
    public static boolean isRunning() {
        return instance.isRunning || instance.isAlive();
    }
}
