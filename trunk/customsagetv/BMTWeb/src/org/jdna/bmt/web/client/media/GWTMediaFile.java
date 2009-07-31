package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.IPath;
import org.jdna.media.metadata.IMediaMetadata;

public class GWTMediaFile implements IMediaFile, Serializable {
    private IPath path;
    private ContentType contentType;
    private boolean exists;
    private String basename;
    private String ext;
    private String name;
    private String title;
    private Type type;
    private long lastModified;
    private String minorTitle;
    
    private int sageMediaFileId;
    private String posterUrl;
    private String message;
    
    private GWTMediaMetadata metadata;

    public GWTMediaFile() {
    }
    
    public GWTMediaFile(IMediaFile file) {
        this.path=GWTPathUtils.createPath(file.getLocation());
        this.contentType=file.getContentType();
        this.exists=file.exists();
        this.basename=file.getBasename();
        this.ext=file.getExtension();
        this.name=file.getName();
        this.title=file.getTitle();
        this.type=file.getType();
        this.lastModified=file.lastModified();
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void accept(IMediaResourceVisitor visitor) {
        visitor.visit(this);
    }

    public void delete() {
        throw new RuntimeException("delete() not supported if GWTMediaFile");
    }

    public boolean exists() {
        return exists;
    }

    public String getBasename() {
        return basename;
    }

    public String getExtension() {
        return ext;
    }

    public IPath getLocation() {
        return path;
    }

    public String getName() {
        return name;
    }

    public IMediaResource getParent() {
        return null;
    }

    public String getTitle() {
        return title;
    }

    public Type getType() {
        return type;
    }

    public boolean isReadOnly() {
        return true;
    }

    public long lastModified() {
        return lastModified;
    }

    public void touch() {
    }

    public int compareTo(IMediaResource o) {
        return getLocation().compareTo(o.getLocation());
    }
    
    public void attachMetadata(GWTMediaMetadata metadata) {
        this.metadata=metadata;
    }
    
    public GWTMediaMetadata getMetadata() {
        return metadata;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public int getSageMediaFileId() {
        return sageMediaFileId;
    }

    public void setSageMediaFileId(int sageMediaFileId) {
        this.sageMediaFileId = sageMediaFileId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String string) {
        this.title = string;
    }

    public String getMinorTitle() {
        return minorTitle;
    }

    public void setMinorTitle(String minorTitle) {
        this.minorTitle = minorTitle;
    }
    
    
}
