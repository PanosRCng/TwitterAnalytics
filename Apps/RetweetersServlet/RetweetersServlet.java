package Apps.RetweetersServlet;

import TwitterAnalytics.TwitterApi;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

import static TwitterAnalytics.Services.RetweeterService.getDistinctRetweetersSelectedDates;

public class RetweetersServlet extends HttpServlet {

    private static class DataObject {
        private User user;
        private int Number_Retweets;

        public DataObject(User user, int Number_Retweets) {
            this.user = user;
            this.Number_Retweets = Number_Retweets;
        }
    }

    private static final Joiner JOINER = Joiner.on(",\n");

    public RetweetersServlet() {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        resp.setHeader("Access-Control-Allow-Methods", "GET");
        resp.setContentType("application/json");

        List<Object> results = getDistinctRetweetersSelectedDates(Boolean.TRUE, "2018-12-29", "2018-12-31");

        List<String> entries = new ArrayList<String>(results.size());

        Gson gson = new Gson();

        User user;
        UsersResources userResource = TwitterApi.client().users();

        for(int i=0;i<results.size();i++){

            try {
                JsonArray tempObject = gson.toJsonTree(results.get(i)).getAsJsonArray();


                entries.add(gson.toJson(new DataObject(userResource.showUser(tempObject.get(0).getAsLong()),
                                                       tempObject.get(1).getAsInt())));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }

        resp.getWriter().println("[\n" + JOINER.join(entries) + "\n]");
    }
}
