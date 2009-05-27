package org.jdna.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerConfiguration {
    public static void configure() {
        if (!useDefaultLoggers()) {
            PropertyConfigurator.configure(LoggerConfiguration.class.getResource("/org/jdna/metadataupdater/log4j.properties"));
            Logger.getLogger(LoggerConfiguration.class).info("Logging Configured using: /org/jdna/metadataupdater/log4j.properties");
        }
    }
    
    public static boolean useDefaultLoggers() {
        File f = new File("log4j.properties");
        try {
            if (f.exists()) {
                PropertyConfigurator.configure(f.toURI().toURL());
                Logger.getLogger(LoggerConfiguration.class).info("Using default loggers from: " + f.getAbsolutePath());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
