package test;


public class TestMisc {
    public static void main(String args[]) {
    	String title = "Test 123 a - 1";
    	System.out.println(title.replaceAll("[^a-zA-Z0-9]+$", ""));
    }
}
