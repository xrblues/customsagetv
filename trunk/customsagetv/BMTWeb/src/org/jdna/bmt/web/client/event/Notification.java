package org.jdna.bmt.web.client.event;

import java.io.Serializable;

public class Notification implements Serializable {
	private static final long serialVersionUID = 1L;
	
    public static enum MessageType {INFO, ERROR, WARN}
    private MessageType messageType = MessageType.INFO;
    private String message;
    private Throwable exception;
    
    public Notification() {
    }
    
    public Notification(String message) {
        this(MessageType.INFO, message, null);
    }
    
    public Notification(MessageType messageType, String message) {
        this(messageType, message,null);
    }
    
    public Notification(MessageType messageType, String message, Throwable ex) {
        this.message=message;
        this.exception=ex;
        this.messageType = messageType;
    }
    
    public String getMessage() {
        return message;
    }

    public Throwable getException() {
        return exception;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
