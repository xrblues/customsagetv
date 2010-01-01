package sagex;

import java.lang.reflect.Constructor;

/**
 * 
 * LogProvider should be used in place of log4j in sagex.apis.  To create a log, use the following
 * ILog log = LogProvider.getLogger(Class);
 * 
 * 
 * @author seans
 *
 */
@SuppressWarnings("unchecked")
public class LogProvider {
    private static final class NullLog implements ILog {
        public void debug(String msg) {
        }

        public void error(String msg) {
        }

        public void error(String msg, Throwable t) {
        }

        public void info(String msg) {
        }

        public void warn(String msg) {
        }

        public void warn(String msg, Throwable t) {
        }
    }
    
    private static Class<ILog> logClass = null;
    private static Constructor<ILog> logInit = null;
    private static final NullLog NULLLOG = new NullLog();
    
    static {
        try {
            logClass = (Class<ILog>) Class.forName("sagex.Log4jLog");
            logInit = logClass.getConstructor(Class.class);
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Log4j Not Found.  Logging Disabled.");
        }
    }
    
    public static ILog getLogger(Class name) {
        if (logInit==null) {
            return NULLLOG;
        } else {
            try {
                return logInit.newInstance(name);
            } catch (Throwable t) {
                System.out.println("Failed to create Log Instance. Logging Disabled.");
                logInit=null;
                return NULLLOG;
            }
        }
    }
}
