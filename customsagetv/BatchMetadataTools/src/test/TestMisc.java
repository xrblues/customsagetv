package test;


public class TestMisc {
    public static void main(String args[]) {
        String s = "|Test|";
        String ss[] = s.split("\\|");
        for (String x : ss) {
            System.out.println(x);
        }
    }
}
