package org.jdna.bmt.web.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class Log {
    public static void debug(String msg) {
        System.out.println(msg);
    }

    public static void error(String msg, Throwable caught) {
        System.out.println(msg);
        caught.printStackTrace();
        GWT.log(msg, caught);
        Window.alert(msg);
    }

    public static void error(String msg) {
        debug(msg);
        Window.alert(msg);
    }
}
