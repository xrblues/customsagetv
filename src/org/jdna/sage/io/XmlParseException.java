package org.jdna.sage.io;

public class XmlParseException extends Exception {
    private static final long serialVersionUID = -7686448759218603666L;

    final String objectName;
    XmlParseException(String objectName, String message){
        super(message);
        this.objectName=objectName;
    }
    
    XmlParseException(String objectName, String message, Exception e){
        super(message,e);
        this.objectName=objectName;
    }
    
    public String toString() {
        return "Failed to parse XML for "+objectName+": "+getMessage();
    }
    
}
