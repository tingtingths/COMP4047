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
        Loghelper.log(this.getClass().getSimpleName(), "context initialized...");

        long startTime = new Date().getTime();
        boolean run = true;

        if (!new File(Settings.workingDir).exists()) {
            new File(Settings.workingDir).mkdir();
        }

        Loghelper.log(this.getClass().getSimpleName(), "Searching spiderResult.txt @ " + Settings.workingDir);
        if (new File(Settings.workingDir + "spiderResult.txt").exists()) run = false;
        if (run) {
            Loghelper.log(this.getClass().getSimpleName(), "running spider");
            System.setProperty("wordnet.database.dir", Settings.dictDir);
            WebSpider spider = new WebSpider(); // demo constructor
            spider.startSpider();
            spider.start();
            try {
                spider.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        long endTime = new Date().getTime();
        Loghelper.log(this.getClass().getSimpleName(), "spider finish, time taken : " + (endTime - startTime) + " ms");
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
