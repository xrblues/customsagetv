package org.jdna.sage.io;

public class MediaFileSegment {

    private String filename;
    private long startTime=0;
    private long duration=0;
    public long getDuration() {
        return duration;
    }
    public String getFilename() {
        return filename;
    }
    public long getStartTime() {
        return startTime;
    }
    void setDuration(long duration) {
        this.duration = duration;
    }
    void setFilename(String filename) {
        this.filename = filename;
    }
    void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    MediaFileSegment(String filename, long startTime, long duration) {
        super();
        this.filename = filename;
        this.startTime = startTime;
        this.duration = duration;
    }
    public boolean equals(Object other) {
        if (! (other instanceof MediaFileSegment))
            return false;
        MediaFileSegment otherSeg = (MediaFileSegment) other;

        return this.startTime == otherSeg.startTime
        && this.duration == otherSeg.duration
        && this.filename.equals(otherSeg.filename);

    }

}
