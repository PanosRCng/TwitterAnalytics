package Apps;

import TwitterAnalytics.TwitterApi;
import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;

import java.util.Map;

public class GeneralFunctions {

    public boolean checkRateLimit()
    {

        boolean flag = false;

        try
        {

            Map<String, RateLimitStatus> rateLimit = TwitterApi.client().getRateLimitStatus();

            for (String timeStatus : rateLimit.keySet()) {

                RateLimitStatus timeLeft = rateLimit.get(timeStatus);

                if (timeLeft != null && timeLeft.getRemaining() == 0) {

                    //Make Thread sleep for 15Minutes

                    System.err.println("Rate limit exceeded!!!");

                    flag = true;

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
}
