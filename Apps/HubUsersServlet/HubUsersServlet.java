package Apps.HubUsersServlet;


import Apps.GeneralFunctions;
import TwitterAnalytics.TwitterApi;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.AlphaCentrality;
import org.jgrapht.graph.DefaultEdge;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.api.UsersResources;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HubUsersServlet extends HttpServlet {

    private static final Joiner JOINER = Joiner.on(",\n");

    public HubUsersServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        resp.setHeader("Access-Control-Allow-Methods", "GET");
        resp.setContentType("application/json");

        GeneralFunctions generalFunctions = new GeneralFunctions();

        int k = 10;
        String p = req.getParameter("k");
        if (p != null) {
            try {
                k = Integer.parseInt(p);
            } catch (NumberFormatException e) {
                // Just eat it, don't need to worry.
            }
        }

        Graph<Long, DefaultEdge> graph = generalFunctions.createHubUsersTree();
        AlphaCentrality alphaCentrality = new AlphaCentrality(graph);
        Map<Long, Double> map = GeneralFunctions.sortByValue(alphaCentrality.getScores());

        User user = null;
        UsersResources userResource = TwitterApi.client().users();

        List<String> entries = new ArrayList<String>(k);

        Gson gson = new Gson();

        int counter = 0;

        for (Map.Entry<Long, Double> entry : map.entrySet())
        {
            try {

                user = userResource.showUser(entry.getKey());

                entries.add(gson.toJson(user));

                counter++;

                if(counter==10) break;

            }catch (TwitterException e) {
                e.printStackTrace();
            }

        }

        resp.getWriter().println("[\n" + JOINER.join(entries) + "\n]");

    }
}
