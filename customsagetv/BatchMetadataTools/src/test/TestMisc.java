package test;



public class TestMisc {
    public static void main(String args[]) {
        String str = "|Bill|Sam,Lorne|";
        String x[] = str.split("[,\\|]");
        for (String s : x) {
            System.out.println(s);
        }
    }
}
