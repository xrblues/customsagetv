package test;


public class TestMisc {
    public static void main(String args[]) {
        String title = "This is a <b>good</b> <font x='s'>title</font><this is removed/>";
        System.out.println("Title1: " + title);
        System.out.println("Title2: " + title.replaceAll("<[^>]+>", ""));
    }
}
