package SearchEngine;

/**
 * Created by e4206692 on 10/22/2015.
 */
public class JsonStorage {
    private static JsonStorage ourInstance = new JsonStorage();
    private String json = "";
    private String rawResult = "";

    public static JsonStorage get() {
        return ourInstance;
    }

    private JsonStorage() {
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getRawResult() {
        return rawResult;
    }

    public void setRawResult(String rawResult) {
        this.rawResult = rawResult;
    }
}
