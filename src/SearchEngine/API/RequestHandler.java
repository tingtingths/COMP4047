package SearchEngine.API;

import SearchEngine.Loghelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Ting on 12/10/2015.
 */
// Path = "/search"
public class RequestHandler extends HttpServlet {

    private String resultFile = "spiderResult.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // do nothing... we don't handle POST request
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getQueryString();
        String keyword = parseQuery(query);
        Loghelper.get().log(this.getClass().getSimpleName(), keyword);

        // get json

        String json = processJson(keyword);

        // return json to client
        PrintWriter out = response.getWriter();
        /*
        out.write("Greetings from " + this.getClass().getSimpleName() + "\n");
        out.write("process time : " + (endTime - startTime) + " ms\n");
        out.write("Query string : " + keyword + "\n");
        out.write("json : " + json);
        */
        //out.write("process time : " + (endTime - startTime) + " ms\n");
        out.write(json);
    }

    private String parseQuery(String qString) {
        String[] queries = qString.split("&");

        for (String q : queries) {
            String[] query = q.split("=");
            if (query[0].toLowerCase().equals("q"))
                return query[1];
        }

        return "";
    }

    private String processJson(String key) {
        long startTime = new Date().getTime();
        String filePath = "";
        // local data format -> domain;url;title;/ k1:n/ k2:k ...
        String json = "[";
        boolean firstMatch = true;

        // read WebSpider result file...
        //Loghelper.getInstance().log(this.getClass().getSimpleName(), System.getProperty("user.dir"));
        File f = new File(System.getProperty("user.dir") + File.separator + resultFile);
        String line = "";
        Pattern pattern = Pattern.compile(".*" + key + ".*", Pattern.CASE_INSENSITIVE);
        try {
            BufferedReader r = new BufferedReader(new FileReader(f));
            while ((line = r.readLine()) != null) { // for every url
                int weight = 1;
                boolean match = false;
                String[] s = line.split(";"); // s[0] - domain, s[1] - url, s[2] - title, s[3] - keywords
                List<String> keywords = Arrays.asList(s[3].split("/ "));
                if (pattern.matcher(s[2]).matches()) {
                    weight += 10;
                    match = true;
                }
                for (String keyword : keywords) {
                    String[] keyWeight = keyword.split(":");
                    if (pattern.matcher(keyword).matches()) {
                        if (keyWeight.length > 1)
                            weight += Integer.valueOf(keyWeight[1]);
                        match = true;
                    }
                }
                if (match) {
                    json += "{\"domain\":\"" + s[0] + "\", \"url\":\"" + s[1] + "\", \"title\":\"" + s[2] + "\", \"weight\":\"" + weight + "\"},";
                }
            }
            r.close();
        } catch (IOException e) {}
        long endTime = new Date().getTime();

        json += "{\"ms\" : \"" + (endTime - startTime) + "\"}";

        return json + "]";
    }
    /*
                        Loghelper.get().log(this.getClass().getSimpleName(), line);
                        Loghelper.get().log(this.getClass().getSimpleName(), "match : " + key);
                        int weight = 1;
                        String[] keyWeight = keyword.split(":");
                        if (keyWeight.length > 1)
                            weight = Integer.valueOf(keyWeight[1]);
                        json += "{";
                        json += "\"domain\":\"" + s[0] + "\", \"url\":\"" + s[1] + "\", \"title\":\"" + s[2] + "\", \"weight\":\"" + weight + "\"";
                        //Loghelper.get().log(this.getClass().getSimpleName(), keyword);
                        json += "},";
     */
}
