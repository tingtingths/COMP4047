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
        Loghelper.get().log(this.getClass().getSimpleName(), "context initialized...");

        long startTime = new Date().getTime();
        boolean run = true;
        Loghelper.get().log(this.getClass().getSimpleName(), "Searching spiderResult.txt @ " + System.getProperty("user.dir"));
        if (new File(System.getProperty("user.dir") + File.separator + "spiderResult.txt").exists()) run = false;
        if (run) {
            Loghelper.get().log(this.getClass().getSimpleName(), "running spider");
            System.setProperty("wordnet.database.dir", "/home/ting/Workspace/COMP4047 Project/dict/");
            WebSpider spider = new WebSpider(); // demo constructor
            spider.startSpider();
            spider.start();
            try {
                spider.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        long endTime = new Date().getTime();
        Loghelper.get().log(this.getClass().getSimpleName(), "spider finish, time taken : " + (endTime - startTime) + " ms");
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
