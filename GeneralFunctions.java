import TwitterAnalytics.DB;
import TwitterAnalytics.TwitterApi;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class GeneralFunctions {

    public static void checkUpdateCounts(int[] updateCounts) {
        for (int i=0; i<updateCounts.length; i++) {
            if (updateCounts[i] >= 0) {
                System.out.println("OK; updateCount="+updateCounts[i]);
            }
            else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
                System.out.println("OK; updateCount=Statement.SUCCESS_NO_INFO");
            }
            else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                System.out.println("Failure; updateCount=Statement.EXECUTE_FAILED");
            }
        }
    }

    public void showRateLimits()
    {
        try
        {
            Map<String, RateLimitStatus> limits = TwitterApi.client().getRateLimitStatus();

            for(Map.Entry<String, RateLimitStatus> entry : limits.entrySet())
            {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
        catch(TwitterException twitterException)
        {
            twitterException.printStackTrace();
            System.out.println("Failed : " + twitterException.getMessage());
        }
    }

    public boolean checkRateLimit()
    {

        boolean flag = false;
        try
        {

            //RateLimitStatus status = TwitterApi.client().getRateLimitStatus().get("/application/rate_limit_status");

            //int secondsUntilReset = status.getSecondsUntilReset();

           // if(secondsUntilReset>0 & secondsUntilReset<=899) {
               // System.out.println("Sorry dude no remaining rate limit. Try again in "+secondsUntilReset+" seconds");
               // System.exit(0);
           // }

            Map<String ,RateLimitStatus> rateLimit = TwitterApi.client().getRateLimitStatus();

            for (String timeStatus : rateLimit.keySet()) {

                RateLimitStatus timeLeft = rateLimit.get(timeStatus);

                if (timeLeft != null && timeLeft.getRemaining() == 0) {

                    //Make Thread sleep for 15Minutes

                    System.err.println("Rate limit exceeded!!!");

                    flag = true;

                    //System.exit(0);

                   // Thread.sleep(900000);

                    //System.out.println("Continue after sleep!!!");

                }



            }

        }
        catch(TwitterException twitterException)
        {
            twitterException.printStackTrace();
            System.out.println("Failed : " + twitterException.getMessage());
        }

        return(flag);
    }


    public static Map<Long, Integer>sortByValue(Map<Long, Integer> unsortMap) {

        List<Map.Entry<Long, Integer>> list =
                new LinkedList<Map.Entry<Long, Integer>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Long, Integer>>() {
            public int compare(Map.Entry<Long, Integer> o1,
                               Map.Entry<Long, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<Long, Integer> sortedMap = new LinkedHashMap<Long, Integer>();
        for (Map.Entry<Long, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static void topUsers(String stringUrl){

        URL url;
        TwitterUser twitterUser = new TwitterUser();

        try {
            url = new URL(stringUrl);
            URLConnection uc;
            uc = url.openConnection();

            uc.setRequestProperty("X-Requested-With", "Curl");

            String userpass = "admin" + ":" + "admin";
            String basicAuth = "Basic " + new String(userpass.getBytes());
            uc.setRequestProperty("Authorization", basicAuth);

            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));

            JSONObject obj;
            String line = null;
            while ((line = reader.readLine()) != null) {

                try {
                    obj = new JSONObject(line);
                    long id_str = Long.parseLong(obj.getString("id_str"));
                    System.out.println(id_str);
                    twitterUser.findUsers(id_str);
                    twitterUser.printGraphUserTweets(id_str);
                    int cnt = Integer.parseInt(obj.getString("cnt"));
                    System.out.println("Count: "+cnt);
                } catch (JSONException e) {
                    //e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printTweets(long pageID){

        TwitterUser twitterUser = new TwitterUser();

        String query = " select DISTINCT userID from temp where pageID=?";

        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = DB.conn().prepareStatement(query);

            preparedStmt.setLong(1, pageID);

            ResultSet rs = preparedStmt.executeQuery();

            while (rs.next())
            {

                twitterUser.printGraphUserTweets(rs.getLong("userID"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
