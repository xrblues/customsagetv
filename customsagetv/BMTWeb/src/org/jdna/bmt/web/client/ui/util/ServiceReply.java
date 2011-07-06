package org.jdna.bmt.web.client.ui.util;

import java.io.Serializable;

public class ServiceReply<T> implements Serializable{
	public ServiceReply(T data) {
		super();
		this.data = data;
	}

	private static final long serialVersionUID = 1L;
	
	private int code=0;
    private String message;
    private T data = null;
    
    public ServiceReply() {
    }
    
    public ServiceReply(int code, String message, T data) {
        this.code=code;
        this.message=message;
        this.data=data;
    }

    public ServiceReply(int code, String message) {
		super();
		this.code = code;
		this.message = message;
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
