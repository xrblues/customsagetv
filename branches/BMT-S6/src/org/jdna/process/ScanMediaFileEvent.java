package org.jdna.process;

import java.io.File;

import org.jdna.media.metadata.PersistenceOptions;

import sagex.phoenix.event.PhoenixEvent;

public class ScanMediaFileEvent extends PhoenixEvent<ScanMediaFileEventHandler> {
    public static final String TYPE = ScanMediaFileEvent.class.getName();
    private PersistenceOptions options;
    private File file;
    
    public ScanMediaFileEvent(File file, PersistenceOptions options) {
        this.file=file;
        this.options=options;
    }
    
    @Override
    public void dispatch(ScanMediaFileEventHandler handler) {
        handler.onScanRequest(this);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * @return the options
     */
    public PersistenceOptions getOptions() {
        return options;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }
}
