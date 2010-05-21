package org.jdna.sage.io;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;

class MediaFileInfoCopy {
    long startTimeMillis=-1;
    long durationMillis=-1;
    int airingID=-1;
    int mediafileID=-1;
    boolean watched=false;
    boolean dontLike=false;
    boolean manualRecord=false;
    boolean archived=false;
    File mediaFile=null;
    String xmlFileName=null;
    
    public MediaFileInfoCopy(Object mediafile) 
    throws InvocationTargetException, IllegalArgumentException
    {
        if ( ! MediaFileAPI.IsVideoFile(mediafile) 
            || ! MediaFileAPI.IsTVFile(mediafile)) {
            throw new IllegalArgumentException("Argument is not a TV file");
        }
        watched=AiringAPI.IsWatched(mediafile);
        dontLike=AiringAPI.IsDontLike(mediafile);
        manualRecord=AiringAPI.IsManualRecord(mediafile);
        archived=MediaFileAPI.IsLibraryFile(mediafile);
        durationMillis=MediaFileAPI.GetFileDuration(mediafile);
        startTimeMillis=MediaFileAPI.GetFileStartTime(mediafile);
        airingID=AiringAPI.GetAiringID(mediafile);
        mediafileID=MediaFileAPI.GetMediaFileID(mediafile);
        File files[]=MediaFileAPI.GetSegmentFiles(mediafile);
        if ( files != null && files.length>0) {
            mediaFile=files[0];
        }
    }
    
    public boolean equals(Object obj) {
        MediaFileInfoCopy other=(MediaFileInfoCopy)obj;
        return (
                this.watched==other.watched
                && this.dontLike==other.dontLike
                && this.manualRecord==other.manualRecord
                && this.archived == other.archived
                && this.durationMillis == other.durationMillis
                && this.startTimeMillis == other.startTimeMillis
                && this.airingID==other.airingID
                && this.mediafileID==other.mediafileID
                && ( this.mediaFile==other.mediaFile 
                     || this.mediaFile.equals(other.mediaFile))
            );
    }
}
