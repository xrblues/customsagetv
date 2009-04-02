package org.jdna.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerConfiguration {
    public static void configure() {
        PropertyConfigurator.configure(LoggerConfiguration.class.getResource("/org/jdna/metadataupdater/log4j.properties"));
        Logger.getLogger(LoggerConfiguration.class).info("Logging Configured using: /org/jdna/metadataupdater/log4j.properties");
    }
    public static void configurePlugin() {
        PropertyConfigurator.configure(LoggerConfiguration.class.getResource("/bmt/log4j.properties"));
        Logger.getLogger(LoggerConfiguration.class).info("Plugin Logging Configured using: /bmt/log4j.properties");
    }
}
