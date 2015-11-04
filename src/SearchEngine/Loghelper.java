package SearchEngine;

/**
 * Created by e4206692 on 10/22/2015.
 */
public class Loghelper {
    public static void log(String tag, String msg) {
        System.out.println("@" + tag + " >> " + msg);
    }

    public static void logE(String tag, String msg) {
        System.err.println("@" + tag + " >> " + msg);
    }
}
