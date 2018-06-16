import TwitterAnalytics.TwitterApi;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;

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

    public void checkRateLimit()
    {
        try
        {

            RateLimitStatus status = TwitterApi.client().getRateLimitStatus().get("/application/rate_limit_status");

            int secondsUntilReset = status.getSecondsUntilReset();

            if(secondsUntilReset>0 & secondsUntilReset<=899) {
                System.out.println("Sorry dude no remaining rate limit. Try again in "+secondsUntilReset+" seconds");
                System.exit(0);
            }

        }
        catch(TwitterException twitterException)
        {
            twitterException.printStackTrace();
            System.out.println("Failed : " + twitterException.getMessage());
        }
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
}
