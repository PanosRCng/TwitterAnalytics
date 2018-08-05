package TwitterAnalytics;


import TwitterAnalytics.ConfigManager.Config;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Map;
import java.util.Vector;

public class TwitterApi
{

    private Twitter twitter = null;


    private TwitterApi()
    {
        ConfigurationBuilder confBuilder = new ConfigurationBuilder();

        confBuilder.setDebugEnabled(true).setTweetModeExtended(true)
                .setOAuthConsumerKey(Config.twitter_api().CONSUMER_KEY)
                .setOAuthConsumerSecret(Config.twitter_api().CONSUMER_SECRET)
                .setOAuthAccessToken(Config.twitter_api().ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(Config.twitter_api().ACCESS_TOKEN_SECRET);

        TwitterFactory twitterFactory = new TwitterFactory(confBuilder.build());

        this.twitter = twitterFactory.getInstance();

    }


    private static class SingletonHelper
    {
        private static final TwitterApi INSTANCE = new TwitterApi();
    }


    public static Twitter client()
    {
        return SingletonHelper.INSTANCE.twitter;
    }

    public static void showRateLimits()
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


    public static boolean rateLimitsExided()
    {
        try
        {
            Map<String ,RateLimitStatus> rateLimit = TwitterApi.client().getRateLimitStatus();

            for(String timeStatus : rateLimit.keySet())
            {
                RateLimitStatus timeLeft = rateLimit.get(timeStatus);

                if(timeLeft != null && timeLeft.getRemaining() == 0)
                {
                    System.err.println("Rate limit exceeded!!!");

                    return true;
                }
            }

        }
        catch(TwitterException twitterException)
        {
            twitterException.printStackTrace();
            System.out.println("Failed : " + twitterException.getMessage());
        }

        return false;
    }


    public static String cleanTweetText(Status tweet, String category)
    {
        Vector<String> to_remove = new Vector<String>();

        for(HashtagEntity hashtagEntity : tweet.getHashtagEntities())
        {
            to_remove.add( "#" + hashtagEntity.getText() );
        }

        for(UserMentionEntity userMentionEntity : tweet.getUserMentionEntities())
        {
            to_remove.add( "@" + userMentionEntity.getScreenName() );
        }

        for(URLEntity urlEntity : tweet.getURLEntities())
        {
            to_remove.add( urlEntity.getText() );
        }

        for(MediaEntity mediaEntity : tweet.getMediaEntities())
        {
            to_remove.add( mediaEntity.getText() );
        }

        for(SymbolEntity symbolEntity : tweet.getSymbolEntities())
        {
            to_remove.add( symbolEntity.getText() );
        }

        String text;

        if(category.equals("retweets")){
            text = tweet.getRetweetedStatus().getText();
        }else{
            text = tweet.getText();
        }

        for(String d : to_remove)
        {
            text = text.replace(d, " ");
        }

        return text;
    }

}