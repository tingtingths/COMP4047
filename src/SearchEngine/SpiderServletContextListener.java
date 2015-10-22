package SearchEngine;

import Spider.WebSpider;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Date;

/**
 * Created by e4206692 on 10/22/2015.
 */
public class SpiderServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Loghelper.getInstance().log(this.getClass().getSimpleName(), "context initialized... starting spider");

        long startTime = new Date().getTime();
        if (false) {
            WebSpider spider = new WebSpider(); // demo constructor
            spider.startSpider();
        }

        try {
            Thread.sleep(5000); // simulate spider loading...
        } catch (Exception e) {  }
        long endTime = new Date().getTime();
        Loghelper.getInstance().log(this.getClass().getSimpleName(), "spider finish, time taken : " + (endTime - startTime) + " ms");
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
