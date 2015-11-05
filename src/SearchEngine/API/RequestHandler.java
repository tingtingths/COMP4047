package SearchEngine.API;

import SearchEngine.Loghelper;
import SearchEngine.Node;
import SearchEngine.Settings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ting on 12/10/2015.
 */
// Path = "/search"
public class RequestHandler extends HttpServlet {

    private String pat = "\((\(.+\)|[^()]) (AND|OR) (\(.+\)|[^()])\)"; // search for ((?) (AND or OR ?) (?)), ? = capture
    private Pattern pattern;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // do nothing... we don't handle POST request
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getQueryString();
        /* Each node represent an AND/OR group, with left and right child. 
         * Parsing and processing will be done recursively. 
         * E.g.
         * (A OR B), ((A OR B) AND C) ...etc.
         */
        pattern = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
        String qString = query.split("&")[0].split("=")[1].replace("%20", " ");
        Loghelper.log(this.getClass().getSimpleName(), "qString: " + qString);
        Node initNode = parseNodes(qString);
        // debug building, the correct result should be same as the user input
        Loghelper.log(this.getClass().getSimpleName(), "Building string...");
        String s = buildNodeStr(initNode);
        Loghelper.log(this.getClass().getSimpleName(), s.equals(qString) + ", type: " + initNode.getNodeType() + ", pat: " + pat + ", " + s);
        Loghelper.log(this.getClass().getSimpleName(), "Building done...");

        /*
        ArrayList<String> keywordGroup = parseQuery(query);
        String keyword = "";

        Loghelper.log(this.getClass().getSimpleName(), keyword);
        */
        // get json
        String json = processJson(initNode);

        // return json to client
        PrintWriter out = response.getWriter();
        out.write(json);

    }

    private String parseQuery(String qString) {
        String[] queries = qString.split("&");

        for (String q : queries) {
            String[] query = q.split("=");
            if (query[0].equalsIgnoreCase("q"))
                return query[1];
        }

        return "";
    }

    // Tokenize the user query and build nodes
    private Node parseNodes(String q) {
        //Loghelper.log(this.getClass().getSimpleName(), q);
        Matcher m = pattern.matcher(q);
        Node n = null;

        if (m.matches()) {
            String type = m.group(2); // AND? OR?
            String left = m.group(1);
            String right = m.group(3);
            Loghelper.log(this.getClass().getSimpleName(), "Matches, " + left + " " + type + " " + right);
            if (type.equalsIgnoreCase("AND"))
                n = new Node(Node.AND_NODE, parseNodes(left), parseNodes(right));
            if (type.equalsIgnoreCase("OR"))
                n = new Node(Node.OR_NODE, parseNodes(left), parseNodes(right));
        } else {
            Loghelper.log(this.getClass().getSimpleName(), "No Matches, " + q);
            n = new Node(Node.STR_NODE, q);
        }

        return n;
    }

    private String buildNodeStr(Node n) {
        if (n.getNodeType() == Node.STR_NODE)
            return n.getKeyword();
        if (n.getNodeType() == Node.AND_NODE)
            return "(" + buildNodeStr(n.getLeft()) + " AND " + buildNodeStr(n.getRight()) + ")";
        if (n.getNodeType() == Node.OR_NODE)
            return "(" + buildNodeStr(n.getLeft()) + " OR " + buildNodeStr(n.getRight()) + ")";
        return "null";

    }

    private String processJson(Node n) {
        long startTime = new Date().getTime();
        String filePath = "";
        // local data format -> domain;url;title;/ k1:n/ k2:k ...
        String json = "[";
        boolean firstMatch = true;

        // read WebSpider result file...
        //Loghelper.getInstance().log(this.getClass().getSimpleName(), System.getProperty("user.dir"));
        File f = new File(Settings.workingDir + "spiderResult.txt");
        String line = "";
        //Pattern pattern = Pattern.compile(".*" + key + ".*", Pattern.CASE_INSENSITIVE);
        try {
            BufferedReader r = new BufferedReader(new FileReader(f));
            while ((line = r.readLine()) != null) { // for every url
                int weight = 1;
                boolean match = false;
                String[] s = line.split(";"); // s[0] - domain, s[1] - url, s[2] - title, s[3] - keywords
                List<String> keywords = Arrays.asList(s[3].split("/ "));
                if (pattern.matcher(s[2]).matches()) { // match with page title
                    weight += 10;
                    //match = true;
                }
                /*
                // check every keyword and match with user keyword - original algorithm
                for (String keyword : keywords) {
                    String[] keyWeight = keyword.split(":");
                    if (pattern.matcher(keyword).matches()) { // match with keywords
                        if (keyWeight.length > 1)
                            weight += Integer.valueOf(keyWeight[1]);
                        match = true;
                    }
                }
                */
                // new algorithm - boolean node checking
                boolean mat = nodeIsTrue(n, keywords);
                Loghelper.log(this.getClass().getSimpleName(), "mat: " + mat);
                if (mat) {
                    match = true;
                }

                pattern = Pattern.compile("", Pattern.CASE_INSENSITIVE);

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

    private boolean nodeIsTrue(Node n, List<String> keywords) {
        if (n.getNodeType() == Node.STR_NODE) {
            boolean result = containsIgnorecase(n.getKeyword(), keywords);
            Loghelper.log(this.getClass().getSimpleName(), "STR " + n.getKeyword() + ": " + result);
            return result;
        }
        if (n.getNodeType() == Node.AND_NODE) {
            boolean result = (nodeIsTrue(n.getLeft(), keywords) && nodeIsTrue(n.getRight(), keywords));
            Loghelper.log(this.getClass().getSimpleName(), "AND: " + result);
            return result;
        }
        if (n.getNodeType() == Node.OR_NODE) {
            boolean result = (nodeIsTrue(n.getLeft(), keywords) || nodeIsTrue(n.getRight(), keywords));
            Loghelper.log(this.getClass().getSimpleName(), "OR: " + result);
            return result;
        }
        return false;
    }

    private boolean containsIgnorecase(String needle, List<String> haystack) {
        for (String keyword : haystack) {
            String[] keyWeight = keyword.split(":");
            //Loghelper.log(this.getClass().getSimpleName(), needle + " vs " + keyWeight[0]);
            if (keyWeight[0].equalsIgnoreCase(needle)) {
                return true;
            }
        }
        return false;
    }
}
