package TwitterAnalytics;


import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterFactory;
import twitter4j.Twitter;



public class TwitterApi
{

    private Twitter twitter = null;


    private TwitterApi()
    {
        ConfigurationBuilder confBuilder = new ConfigurationBuilder();

        confBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(TwitterAnalytics.Config.TwitterApi.CONSUMER_KEY)
                .setOAuthConsumerSecret(TwitterAnalytics.Config.TwitterApi.CONSUMER_SECRET)
                .setOAuthAccessToken(TwitterAnalytics.Config.TwitterApi.ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(TwitterAnalytics.Config.TwitterApi.ACCESS_TOKEN_SECRET);

        TwitterFactory twitterFactory = new TwitterFactory(confBuilder.build());

        this.twitter = twitterFactory.getInstance();
    }


    private static class SingletonHelper
    {
        private static final TwitterApi INSTANCE = new TwitterApi();
    }


    public static Twitter instance()
    {
        return SingletonHelper.INSTANCE.twitter();
    }


    private Twitter twitter()
    {
        return this.twitter;
    }

}
