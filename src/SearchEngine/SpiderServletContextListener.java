package SearchEngine;

import Spider.WebSpider;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.Date;

/**
 * Created by e4206692 on 10/22/2015.
 */
public class SpiderServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Loghelper.get().log(this.getClass().getSimpleName(), "context initialized... starting spider");

        long startTime = new Date().getTime();
        boolean run = true;
        if (new File("C:\\Users\\e4206692\\Desktop\\glassfish4\\glassfish\\domains\\domain1\\config\\result.txt").exists()) run = false;
        Loghelper.get().log(this.getClass().getSimpleName(), "run spider : " + run);
        if (run) {
            System.setProperty("wordnet.database.dir", "C:\\Users\\e4206692\\Desktop\\COMP4047\\dict");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    WebSpider spider = new WebSpider(); // demo constructor
                    spider.startSpider();
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        long endTime = new Date().getTime();
        Loghelper.get().log(this.getClass().getSimpleName(), "spider finish, time taken : " + (endTime - startTime) + " ms");
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
