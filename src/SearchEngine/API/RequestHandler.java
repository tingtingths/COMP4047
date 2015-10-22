package SearchEngine.API;

import SearchEngine.Loghelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by Ting on 12/10/2015.
 */
// Path = "/search"
public class RequestHandler extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // do nothing... we don't handle POST request
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getQueryString();
        String keyword = parseQuery(query);
        Loghelper.getInstance().log(this.getClass().getSimpleName(), query);

        try {
            // wait for 3 seconds to simulate the process
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // get json
        long startTime = new Date().getTime();
        String json = processJson(keyword);
        long endTime = new Date().getTime();

        // return json to client
        PrintWriter out = response.getWriter();
        out.write("Greetings from " + this.getClass().getSimpleName() + "\n");
        out.write("process time : " + (endTime - startTime) + " ms\n");
        out.write("Query string : " + keyword + "\n");
        out.write("json : " + json);
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

    private String processJson(String keyword) {
        String json = "";

        return json;
    }
}
