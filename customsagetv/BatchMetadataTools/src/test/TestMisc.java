package test;

import java.util.Random;




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
