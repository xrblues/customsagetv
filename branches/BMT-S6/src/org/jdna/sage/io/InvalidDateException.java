package org.jdna.sage.io;


public class InvalidDateException extends Exception {
    private static final long serialVersionUID = -1426986610959738671L;
    InvalidDateException(String msg) {
        super(msg);
    }
    InvalidDateException(String msg, Throwable cause) {
        super(msg,cause);
    }
    
};