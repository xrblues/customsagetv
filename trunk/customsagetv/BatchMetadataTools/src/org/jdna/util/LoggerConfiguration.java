package org.jdna.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;

public class LoggerConfiguration {
    public static void configure() {
        if (!useDefaultLoggers()) {
            try {
                OutputStream os = new FileOutputStream(new File("log4j.properties"));
                IOUtils.copy(LoggerConfiguration.class.getResourceAsStream("/org/jdna/metadataupdater/log4j.properties"), os);
                os.flush();
                os.close();
                PropertyConfigurator.configure("log4j.properties");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static boolean useDefaultLoggers() {
        File f = new File("log4j.properties");
        try {
            if (f.exists()) {
                PropertyConfigurator.configure(f.getAbsolutePath());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
