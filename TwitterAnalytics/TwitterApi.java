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
                .setOAuthConsumerKey(Config.instance().twitter_api().CONSUMER_KEY)
                .setOAuthConsumerSecret(Config.instance().twitter_api().CONSUMER_SECRET)
                .setOAuthAccessToken(Config.instance().twitter_api().ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(Config.instance().twitter_api().ACCESS_TOKEN_SECRET);

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
