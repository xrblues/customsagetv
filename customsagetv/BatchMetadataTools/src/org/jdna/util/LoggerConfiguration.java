package org.jdna.util;

import org.apache.log4j.PropertyConfigurator;

public class LoggerConfiguration {
    public static void configure() {
        PropertyConfigurator.configure(LoggerConfiguration.class.getResource("/org/jdna/metadataupdater/log4j.properties"));
    }
    public static void configurePlugin() {
        PropertyConfigurator.configure(LoggerConfiguration.class.getResource("/bmt/log4j.properties"));
    }
}
