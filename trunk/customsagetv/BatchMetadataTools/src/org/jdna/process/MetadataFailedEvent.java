package org.jdna.process;

import java.util.Calendar;
import java.util.Date;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;

import sagex.phoenix.event.PhoenixEvent;
import sagex.phoenix.vfs.IMediaFile;

public class MetadataFailedEvent extends PhoenixEvent<MetadataFailedEventHandler> {
    public static final String TYPE = MetadataFailedEvent.class.getName();
    private IMediaFile mediaFile;
    private Date date = null;
    private PersistenceOptions options;
    private IMediaMetadata metadata;
    private String message;
    private Throwable error;
    private SearchQuery query;
    private int code=-1;
    
    public MetadataFailedEvent(int code, IMediaFile mediafile, SearchQuery query, PersistenceOptions options, String message) {
        this(code, mediafile, query, options, message, null);
    }
    
    public MetadataFailedEvent(int code, IMediaFile mediafile, SearchQuery query, PersistenceOptions options, String message, Throwable t) {
        this.code=code;
        this.mediaFile = mediafile;
        this.options=options;
        this.date = Calendar.getInstance().getTime();
        this.query = query;
        this.message=message;
        this.error=t;
    }

    @Override
    public void dispatch(MetadataFailedEventHandler handler) {
        handler.onMetadataFailed(this);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * @return the mediaFile
     */
    public IMediaFile getMediaFile() {
        return mediaFile;
    }

    /**
     * @return the persistDate
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the options
     */
    public PersistenceOptions getOptions() {
        return options;
    }

    /**
     * @return the metadata
     */
    public IMediaMetadata getMetadata() {
        return metadata;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the error
     */
    public Throwable getError() {
        return error;
    }

    /**
     * @return the query
     */
    public SearchQuery getQuery() {
        return query;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }
}
