package Apps.RetweetersServlet;

import Apps.RetweetersServlet.RetweetersServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import java.util.Date;

public class RetweetersServletApp {

    public RetweetersServletApp() {

        Server jettyServer = new Server(8888);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        jettyServer.setHandler(context);

        context.addServlet(new ServletHolder(new RetweetersServlet()), "/retweeters");

        System.out.println(String.format("%tc: Starting service on port %d", new Date(), 8888));

        try {
            jettyServer.start();
            jettyServer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jettyServer.destroy();
        }
    }
}
