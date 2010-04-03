package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import sagex.SageAPI;
import sagex.util.ILog;
import sagex.util.Log4jConfigurator;
import sagex.util.LogProvider;

public class TestMisc {
    public static void main(String args[]) throws ParseException {
        //BasicConfigurator.configure();
        Log4jConfigurator.configureQuietly("sagex-api");
        Log4jConfigurator.configureQuietly("sagex-api");
        
        Logger log = Logger.getLogger(SageAPI.class);
        log.info("Have a sagex api logger");
        
        ILog ilog = LogProvider.getLogger(SageAPI.class);
        ilog.info("Using ILog instance");
        
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date d = f.parse("2001-12-23");
        System.out.println(d);

        f = new SimpleDateFormat("yyyy-MM-dd");
        d = f.parse("2001-12-24 12:12:33");
        System.out.println(d);
    }
}
