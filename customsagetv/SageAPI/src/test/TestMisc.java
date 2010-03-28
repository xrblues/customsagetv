package test;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;

import sagex.SageAPI;
import sagex.UIContext;
import sagex.api.Global;
import sagex.api.enums.MediaFileAPIEnum;
import sagex.api.enums.SageCommandEnum;
import sagex.util.ILog;
import sagex.util.LogProvider;

public class TestMisc {
    public static void main(String args[]) throws ParseException {
        BasicConfigurator.configure();
        
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date d = f.parse("2001-12-23");
        System.out.println(d);

        f = new SimpleDateFormat("yyyy-MM-dd");
        d = f.parse("2001-12-24 12:12:33");
        System.out.println(d);
    }
}
