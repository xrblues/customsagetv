package bmt;

import org.apache.log4j.Logger;
import org.jdna.metadataupdater.Version;

import sagex.phoenix.util.SageTV;
import sagex.util.Log4jConfigurator;

/**
 * Use S7 Plugin instead... this will no longer do anything in S7
 * 
 * @author seans
 * 
 */
@Deprecated
public class BMT {
    private static boolean initialized = false;

    @Deprecated
    public static synchronized void init() {
        try {
            if (initialized) return;
            initialized = true;
            Log4jConfigurator.configureQuietly("bmt", BMT.class.getClassLoader());
            
            String sageVer = SageTV.getSageVersion();
            if (sageVer != null && sageVer.startsWith("7.")) {
                Logger log = Logger.getLogger(BMT.class);
                log.info("BMT.init() is deprecated in S7");
                return;
            }
            Logger log = Logger.getLogger(BMT.class);
            log.info("========= BEGIN BATCH METADATA TOOLS ENVIRONMENT ==============");
            log.info("    BMT Version:  " + Version.VERSION);
            log.info("Phoenix Version:  " + phoenix.api.GetVersion());
            log.info("  Sagex Version:  " + sagex.api.Version.GetVersion());
            log.info("   Java Version:  " + System.getProperty("java.version"));
            log.info(" Java Classpath:  " + System.getProperty("java.class.path"));
            log.info("========= END BATCH METADATA TOOLS ENVIRONMENT ==============");
        } catch (Throwable t) {
            Logger.getRootLogger().error("Failed to initialize BMT!", t);
        }
    }
}
