package org.jdna.bmt.web.client.ui.util;

import java.io.Serializable;

public class ServiceReply<T> implements Serializable{
    private int code;
    private String message;
    private T data = null;
    
    public ServiceReply() {
    }
    
    public ServiceReply(int code, String message, T data) {
        this.code=code;
        this.message=message;
        this.data=data;
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
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data=data;
    }
}
