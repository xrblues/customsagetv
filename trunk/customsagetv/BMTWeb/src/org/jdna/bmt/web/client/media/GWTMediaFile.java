package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.IPath;

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
    
    public GWTMediaArt defaultPoster;
    public GWTMediaArt defaultBackground;
    public GWTMediaArt defaultBanner;
    
    public String defaultPosterDir;
    public String defaultBackgroundDir;
    public String defaultBannerDir;
    
    
    private Property<Boolean> sageRecording = new Property<Boolean>(false);
    
    private GWTMediaMetadata metadata;
    
    private String showId;
    private String airingId;

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

    public GWTMediaArt getDefaultPoster() {
        return defaultPoster;
    }

    public void setDefaultPoster(GWTMediaArt defaultPoster) {
        this.defaultPoster = defaultPoster;
    }

    public GWTMediaArt getDefaultBackground() {
        return defaultBackground;
    }

    public void setDefaultBackground(GWTMediaArt defaultBackground) {
        this.defaultBackground = defaultBackground;
    }

    public GWTMediaArt getDefaultBanner() {
        return defaultBanner;
    }

    public void setDefaultBanner(GWTMediaArt defaultBanner) {
        this.defaultBanner = defaultBanner;
    }

    public Property<Boolean> getSageRecording() {
        return sageRecording;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getAiringId() {
        return airingId;
    }

    public void setAiringId(String airingId) {
        this.airingId = airingId;
    }

    public String getDefaultPosterDir() {
        return defaultPosterDir;
    }

    public void setDefaultPosterDir(String defaultPosterDir) {
        this.defaultPosterDir = defaultPosterDir;
    }

    public String getDefaultBackgroundDir() {
        return defaultBackgroundDir;
    }

    public void setDefaultBackgroundDir(String defaultBackgroundDir) {
        this.defaultBackgroundDir = defaultBackgroundDir;
    }

    public String getDefaultBannerDir() {
        return defaultBannerDir;
    }

    public void setDefaultBannerDir(String defaultBannerDir) {
        this.defaultBannerDir = defaultBannerDir;
    }

    public boolean renameTo(String newName) {
        return false;
    }
}
