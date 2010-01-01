package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;

import sagex.ILog;
import sagex.LogProvider;
import sagex.UIContext;

public class TestMisc {
    public static void main(String args[]) {
        BasicConfigurator.configure();
        
        ILog log = LogProvider.getLogger(TestMisc.class);
        log.info("Logging is working");
        
        UIContext ctx = UIContext.getCurrentContext();
        log.debug("Context: " + ctx);
        
        String thread = "AWTThreadWatcher-001d098ac46c@ac4b99";
        //String thread = "MiniUIServer@10a829";
        //String thread = "Thread-21@da213a";
        Pattern p = Pattern.compile("-([0-9a-f]{12}+)@");
        Matcher m = p.matcher(thread);
        if (m.find()) {
            log.debug("UI: " + m.group(1));
        } else {
            log.debug("Nothing");
        }
    }
}
