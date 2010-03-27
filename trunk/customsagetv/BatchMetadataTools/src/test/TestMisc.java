package test;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.language.RefinedSoundex;




public class TestMisc {
    public static void main(String args[]) throws Throwable {
        Random random = new Random();
        String s1 = Integer.toHexString((int)(random.nextDouble()*0xFFFF));
        String s2 = Integer.toHexString(random.nextInt());
        String s3 = Integer.toHexString(random.nextInt()*0xFFFF);
        System.out.println("S1: " + s1);
        System.out.println("S2: " + s2);
        System.out.println("S3: " + s3);
    }
}
