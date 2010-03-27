package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.language.RefinedSoundex;




public class TestMisc {
    public static void main(String args[]) throws Throwable {
        Pattern p = Pattern.compile(".*[/\\\\](.*)[es]([0-9]{1,2})[sexp]{1,2}([[0-9]]{1,2})",Pattern.CASE_INSENSITIVE);
        String s = "/tmp/Battlestar Galactica-e01s01-33.mkv";
        Matcher m  = p.matcher(s);
        if (m.find()) {
            System.out.println("Title: " + m.group(1));
        } else {
            System.out.println("*** Failed");
        }
    }
}
