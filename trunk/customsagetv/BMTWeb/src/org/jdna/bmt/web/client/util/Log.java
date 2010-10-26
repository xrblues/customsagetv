package org.jdna.bmt.web.client.util;

import com.google.gwt.core.client.GWT;

public class Log {
    public static void debug(String msg) {
        System.out.println("BMT:DEBUG: " + msg);
    }

    public static void error(String msg, Throwable caught) {
        System.out.println("BMT:ERROR:" + msg);
        if (caught!=null) {
            caught.printStackTrace();
        }
        GWT.log(msg, caught);
    }

    public static void error(String msg) {
        System.out.println("BMT:ERROR:" + msg);
    }
}
