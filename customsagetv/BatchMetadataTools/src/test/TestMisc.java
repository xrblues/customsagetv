package test;

import org.jdna.util.StringUtils;


public class TestMisc {
    public static void main(String args[]) {
        String s = "<h1>Title <B> - WTF</B></h1>";
        System.out.println(s);
        System.out.println(StringUtils.removeHtml(s));
    }
}
