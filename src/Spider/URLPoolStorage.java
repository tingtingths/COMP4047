package Spider;

import java.util.LinkedList;

/**
 * Created by ting on 2015/10/23.
 */
public class URLPoolStorage {
    private static URLPoolStorage ourInstance = new URLPoolStorage();
    public LinkedList<String> pool = new LinkedList<String>();

    public static URLPoolStorage get() {
        return ourInstance;
    }

    private URLPoolStorage() {
    }

}
