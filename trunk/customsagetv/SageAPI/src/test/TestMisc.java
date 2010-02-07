package test;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;

import sagex.ILog;
import sagex.LogProvider;
import sagex.UIContext;
import sagex.api.Global;

public class TestMisc {
    public static void main(String args[]) {
        BasicConfigurator.configure();
        
        Vector v = Global.GetSageCommandNames();
        for (int i=0;i<v.size();i++) {
            System.out.println("Command: " + v.get(i));
        }
    }
}
