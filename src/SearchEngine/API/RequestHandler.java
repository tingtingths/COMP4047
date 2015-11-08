package SearchEngine.API;

import SearchEngine.Loghelper;
import SearchEngine.Node;
import SearchEngine.Settings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class listen on PATH "/search", and is responsible for handling user search request.
 */
public class RequestHandler extends HttpServlet {

    // Pattern for parsing use input
    private String pat = "\\(([^()]+|\\(.+\\)) (AND|OR) ([^()]+|\\(.+\\))\\)"; // search for ((?) (AND or OR ?) (?)), ? = capture
    private Pattern nodePattern = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
    long startTime = 0;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // do nothing... we don't handle POST request
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // setup response charset
        response.setCharacterEncoding("UTF-8");

        String query = request.getQueryString();
        // start measuring time
        startTime = new Date().getTime();
        // parse input
        String qString = query.split("&")[0].split("=")[1].replace("%20", " ");
        Node initNode = parseNodes(qString);
        // debug building, the correct result should be same as the user input
        Loghelper.log(this.getClass().getSimpleName(), "Build string from nodes...");
        Loghelper.log(this.getClass().getSimpleName(), "Root type: " + initNode.getNodeType() + ", " + buildNodeStr(initNode));

        // get json
        String json = processJson(initNode);

        // return json to client
        PrintWriter out = response.getWriter();
        out.write(json);
    }

    /**
     * Each node represent an AND/OR group, with left and right child.
     * Parsing and processing will be done recursively.
     * E.g.
     * (A OR B), ((A OR B) AND C) ...etc.
     * @param q - String representing the tree
     * @return Root node of the tree
     */
    // Tokenize the user query and build nodes
    private Node parseNodes(String q) {
        Matcher m = nodePattern.matcher(q);
        Node n = null;

        if (m.matches()) { // if the string is a node pattern
            String type = m.group(2); // AND? OR?
            String left = m.group(1);
            String right = m.group(3);

            if (type.equalsIgnoreCase("AND"))
                n = new Node(Node.AND_NODE, parseNodes(left), parseNodes(right));
            if (type.equalsIgnoreCase("OR"))
                n = new Node(Node.OR_NODE, parseNodes(left), parseNodes(right));
        } else { // it is a simple keyword
            n = new Node(Node.STR_NODE, q);
        }
        return n;
    }

    /**
     * Construct a string from tree, use for debug.
     * @param n - a tree node
     * @return String representing the tree
     */
    private String buildNodeStr(Node n) {
        if (n.getNodeType() == Node.STR_NODE)
            return n.getKeyword();
        if (n.getNodeType() == Node.AND_NODE)
            return "[" + buildNodeStr(n.getLeft()) + " AND " + buildNodeStr(n.getRight()) + "]";
        if (n.getNodeType() == Node.OR_NODE)
            return "[" + buildNodeStr(n.getLeft()) + " OR " + buildNodeStr(n.getRight()) + "]";
        return "null";
    }

    /**
     * Search for url that match with the tree.
     * @param n - Root node of the tree
     * @return String of results in JSON format
     */
    private String processJson(Node n) {
        String filePath = "";
        // local data format -> domain;url;title;/ k1:n/ k2:k ...
        String json = "[";
        boolean firstMatch = true;

        // read WebSpider result file
        File f = new File(Settings.workingDir + "spiderResult.txt");
        String line = "";
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            while ((line = r.readLine()) != null) { // for every url
                boolean match = false;
                String[] s = line.split(";"); // s[0] - domain, s[1] - url, s[2] - title, s[3] - keywords

                // construct keyword list
                List<String> keywords = Arrays.asList(s[3].split("/ "));

                // boolean node checking keywords and title
                int weight = nodeIsTrue(n, keywords, s[2], 0);
                if (weight > 0) {
                    match = true;
                }
                // if the url match, add it into the json
                if (match) {
                    Loghelper.log(this.getClass().getSimpleName(), s[2]);
                    json += "{\"domain\":\"" + s[0] + "\", \"url\":\"" + s[1] + "\", \"title\":\"" + s[2] + "\", \"weight\":\"" + weight + "\"},";
                }
            }
            r.close();
        } catch (IOException e) {
        }
        long endTime = new Date().getTime();

        // include the processing time into the json
        json += "{\"ms\" : \"" + (endTime - startTime) + "\"}";

        return json + "]";
    }

    /**
     * Check if the page match with the tree
     * @param n - Root node of the tree
     * @param keywords - keywords of the page
     * @param title - title of the page
     * @param weight - the current weight of the page
     * @return weight, 0 - no match, &gt;0 - match
     */
    private int nodeIsTrue(Node n, List<String> keywords, String title, int weight) {
        if (n.getNodeType() == Node.STR_NODE) {
            // search for match, ignore case
            weight += containsIgnorecase(n.getKeyword(), keywords, title);

            return weight;
        }
        if (n.getNodeType() == Node.AND_NODE) {
            int wLeft = nodeIsTrue(n.getLeft(), keywords, title, weight);
            int wRight = nodeIsTrue(n.getRight(), keywords, title, weight);

            if (wLeft > 0 && wRight > 0) {
                weight += wLeft + wRight;
            }

            return weight;
        }
        if (n.getNodeType() == Node.OR_NODE) {
            weight += nodeIsTrue(n.getLeft(), keywords, title, weight) + nodeIsTrue(n.getRight(), keywords, title, weight);

            return weight;
        }

        return weight;
    }

    /**
     * Search for match using a keyword in a node.
     * @param needle - the keyword
     * @param haystack - the keywords of the page to perform searching
     * @param title - title of the page
     * @return weight, 0 - no match, &gt;0 - match
     */
    private int containsIgnorecase(String needle, List<String> haystack, String title) {
        Pattern needlePat = Pattern.compile(needle, Pattern.CASE_INSENSITIVE);
        int titleWeight = 0;

        if (needlePat.matcher(title).find()) titleWeight += 10;

        for (String keyword : haystack) {
            if (!keyword.trim().isEmpty()) {
                String[] keyWeight = keyword.split(":");

                if (needlePat.matcher(keyWeight[0]).find()) {
                    //Loghelper.log(this.getClass().getSimpleName(), "Match: " + keyWeight[0]);
                    if (keyWeight.length > 1) return Integer.valueOf(keyWeight[1]) + titleWeight;
                    return 1 + titleWeight;
                }
            }
        }
        return titleWeight;
    }
}
