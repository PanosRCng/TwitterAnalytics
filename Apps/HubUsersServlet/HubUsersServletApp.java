package Apps.HubUsersServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.Date;

public class HubUsersServletApp {

    public HubUsersServletApp() {

        Server jettyServer = new Server(8888);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        jettyServer.setHandler(context);

        context.addServlet(new ServletHolder(new HubUsersServlet()), "/hubUsers");

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
