package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;




public class TestMisc {
    public static void main(String args[]) throws Throwable {
        Pattern p = Pattern.compile("(.*)-([0-9]+\\.[0-9]+.*)\\.jar");
        String jar = "commons-codec-1.3.2.jar";
        Matcher m = p.matcher(jar);
        if (m.matches()) {
            System.out.println("Jar: " + m.group(1));
            System.out.println("Ver: " + m.group(2));
        } else {
            System.out.println("No Match");
        }
        
    }
    
}
