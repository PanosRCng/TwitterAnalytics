package TwitterAnalytics;


import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterFactory;
import twitter4j.Twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class TwitterApi
{

    private Twitter twitter = null;

    private Connection connection;

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

    public void setConnection() {

        System.out.println("Connecting database...");

        try {
            this.connection = DriverManager.getConnection(TwitterAnalytics.Config.TwitterApi.URL,
                    TwitterAnalytics.Config.TwitterApi.USERNAME,
                    TwitterAnalytics.Config.TwitterApi.PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Database connected!");
    }

    public Connection getConnection() {
        return this.connection;
    }

    private static class SingletonHelper
    {
        private static final TwitterApi INSTANCE = new TwitterApi();
    }


    public static Twitter instance()
    {
        return SingletonHelper.INSTANCE.twitter();
    }

    public static TwitterApi twitterApiInstance()
    {
        return SingletonHelper.INSTANCE;
    }

    private Twitter twitter()
    {
        return this.twitter;
    }

}