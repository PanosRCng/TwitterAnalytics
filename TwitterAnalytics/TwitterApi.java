package TwitterAnalytics;


import TwitterAnalytics.ConfigManager.Config;
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


}
