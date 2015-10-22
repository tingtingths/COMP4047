package SearchEngine;

/**
 * Created by e4206692 on 10/22/2015.
 */
public class Loghelper {
    private static Loghelper ourInstance = new Loghelper();

    public static Loghelper get() {
        return ourInstance;
    }

    private Loghelper() {
    }

    public void log(String tag, String msg) {
        System.out.println("@" + tag + " >> " + msg);
    }
}
