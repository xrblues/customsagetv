package org.jdna.bmt.web.client.ui.status;

import java.io.Serializable;

public class SystemMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private long endTime;
    private long startTime;
    private int level;
    private int repeat;
    private int typeCode;
    private String typeName;
    private String message;

    private int id;
    
    
    public SystemMessage() {
    }


    /**
     * @return the endTime
     */
    public long getEndTime() {
        return endTime;
    }


    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(long time) {
        this.endTime = time;
    }


    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }


    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }


    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }


    /**
     * @return the repeat
     */
    public int getRepeat() {
        return repeat;
    }


    /**
     * @param repeat the repeat to set
     */
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }


    /**
     * @return the typeCode
     */
    public int getTypeCode() {
        return typeCode;
    }


    /**
     * @param typeCode the typeCode to set
     */
    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }


    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }


    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }


    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }


    public void setId(int id) {
        this.id=id;
    }
    
    public int getId() {
        return id;
    }
}
