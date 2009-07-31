package org.jdna.bmt.web.client.ui.util;

import java.io.Serializable;

public class ServiceReply implements Serializable{
    private int code;
    private String message;
    
    public ServiceReply() {
    }
    
    public ServiceReply(int code, String message) {
        this.code=code;
        this.message=message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
