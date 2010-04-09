package test;

import java.text.ParseException;

import sagex.SageAPI;
import sagex.api.Global;
import sagex.remote.rmi.RMISageAPI;

public class TestMisc {
    public static void main(String args[]) throws ParseException {
        SageAPI.setProvider(new RMISageAPI("seans-desktop", 1098)); // seans-desktop:1098
        //SageAPI.setProvider(new RMISageAPI("mediaserver", 1098));
        
        System.out.println(Global.GetOS());
    }
}
