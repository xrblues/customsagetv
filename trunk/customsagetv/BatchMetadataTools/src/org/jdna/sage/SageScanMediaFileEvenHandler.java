package org.jdna.sage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.process.MetadataItem;
import org.jdna.process.MetadataProcessor;
import org.jdna.process.ScanMediaFileEvent;
import org.jdna.process.ScanMediaFileEventHandler;
import org.jdna.process.SysEventMessageID;

import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.event.message.SystemMessageEvent;
import sagex.phoenix.event.message.SystemMessageEvent.Severity;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.filters.MediaTypeFilter;
import sagex.phoenix.vfs.filters.OrResourceFilter;

/**
 * 
 * TODO: Add a better queue depletion detection
 * - Have the main thread never quit, but sleep, until there are more items
 * - Create an abort thread that can be used to quickly deplete the queue in the event
 *   that someone want to abort/cancel the scan in progress 
 * TODO: Add more than 1 thread to deplete the queue
 * @author seans
 *
 */
public class SageScanMediaFileEvenHandler implements ScanMediaFileEventHandler {
    private class ScanThread extends Thread {
        private boolean cancelled = false;
        private OrResourceFilter filter = new OrResourceFilter();
        private ProgressTracker<MetadataItem> tracker = new ProgressTracker<MetadataItem>() {
            @Override
            public void addFailed(MetadataItem item, String msg, Throwable t) {
                super.addFailed(item, msg, t);
                
                // use system messages to notify of failed items.
                if (pluginConfig.getUseSystemMessagesForFailed()) {
                    if (item != null && item.getQuery()!=null) {
                        Map<String, String> vars = new HashMap<String, String>();
                        for (SearchQuery.Field f : SearchQuery.Field.values()) {
                            vars.put(f.name(), item.getQuery().get(f));
                        }
                        vars.put("MediaType", item.getQuery().getMediaType().name());
                        SystemMessageEvent evt = new SystemMessageEvent(SysEventMessageID.METADATA_FAILED, Severity.ERROR, "Failed to find metadata for: " + item.getQuery().get(Field.FILE) + " using provider " + item.getProvider(), vars);
                        Phoenix.getInstance().getEventBus().fireEvent(evt);
                    } else if (item != null) {
                        SystemMessageEvent evt = new SystemMessageEvent(SysEventMessageID.METADATA_FAILED, Severity.ERROR, "Failed to find metadata for: " + item.getFile() + " using provider " + item.getProvider());
                        Phoenix.getInstance().getEventBus().fireEvent(evt);
                    }
                }
            }
        };
        
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            cancelled = false;
            while (!cancelled) {
                try {
                    // wait for up to 10 seconds for a file to show up in the queue
                    ScanMediaFileEvent evt = filesToScan.poll(10000, TimeUnit.MILLISECONDS);
                    if (evt==null) {
                        log.debug("Nothing to process... exiting scan thread.");
                        cancelled=true;
                        break;
                    }
                    
                    setupFilters();
                    log.debug("Scanning for metadata for file: " + evt.getFile().getAbsolutePath());
                    
                    MetadataProcessor processor = new MetadataProcessor(evt.getOptions());
                    processor.addFilter(filter);
                    
                    Object sageRes = phoenix.api.GetSageMediaFile(evt.getFile());
                    if (sageRes==null) {
                        scanError("Unknown SageTV File: " + sageRes);
                        continue;
                    }
                    
                    IMediaResource res = phoenix.api.GetMediaResource(sageRes);
                    if (res==null) {
                        scanError("Unknown MediaFile: " + res);
                        continue;
                    }
                    
                    processor.process(res, tracker);
                } catch (InterruptedException e) {
                    log.info("Timed out waiting for a new file to appear in the scan queue.  Shutting down for now.");
                    cancelled=true;
                    break;
                }
            }
            notifyResults(tracker);
            scanThread=null;
        }
        
        private void setupFilters() {
            filter.clear();
            //setup filters...
            for (String s : pluginConfig.getSupportedMediaTypes().split("\\s*,\\s*")) {
                MediaResourceType type = MediaResourceType.toMediaResourceType(s);
                if (type!=null) {
                    filter.addFilter(new MediaTypeFilter(type));
                }
            }
            
        }
        
        private void scanError(String message) {
            if (pluginConfig.getUseSystemMessagesForFailed()) {
                SystemMessageEvent evt = new SystemMessageEvent(SysEventMessageID.SCAN_COMPLETE_ERROR, Severity.WARNING, message);
                Phoenix.getInstance().getEventBus().fireEvent(evt);
            }
        }

        private void notifyResults(ProgressTracker<MetadataItem> tracker2) {
            log.info("Metadata Scan Completed.  Success: " + tracker2.getSuccessfulItems().size() + "; Failed: " + tracker2.getFailedItems().size());
            if (pluginConfig.getUseSystemMessagesForStatus()) {
                SystemMessageEvent evt = new SystemMessageEvent(SysEventMessageID.SCAN_COMPLETE_STATUS, Severity.INFO, "Metadata Scan Completed.  Success: " + tracker2.getSuccessfulItems().size() + "; Failed: " + tracker2.getFailedItems().size());
                Phoenix.getInstance().getEventBus().fireEvent(evt);
            }
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }
    
    private Logger log = Logger.getLogger(SageScanMediaFileEvenHandler.class);
    private BlockingQueue<ScanMediaFileEvent> filesToScan = new LinkedBlockingQueue<ScanMediaFileEvent>();
    private ScanThread scanThread = new ScanThread();
    private PluginConfiguration pluginConfig = null;
    
    public SageScanMediaFileEvenHandler() {
        pluginConfig = GroupProxy.get(PluginConfiguration.class);
    }
    
    public void onScanRequest(ScanMediaFileEvent event) {
        filesToScan.add(event);
        wakeUp();
    }

    private void wakeUp() {
        if (scanThread==null) {
            scanThread = new ScanThread();
            scanThread.setName(Thread.currentThread().getName()+ "-BMTScan1");
        }
        
        if (!scanThread.isAlive()) {
            scanThread.start();
        }
    }
}
