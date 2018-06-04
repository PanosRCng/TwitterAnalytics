import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterFactory;
import twitter4j.Twitter;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;
import twitter4j.Status;

import java.util.List;



public class TestApp
{

	public static void main(String[] args)
	{
		ConfigurationBuilder confBuilder = new ConfigurationBuilder();

		confBuilder.setDebugEnabled(true)
				.setOAuthConsumerKey(Config.CONSUMER_KEY)
				.setOAuthConsumerSecret(Config.CONSUMER_SECRET)
				.setOAuthAccessToken(Config.ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(Config.ACCESS_TOKEN_SECRET);

		TwitterFactory twitterFactory = new TwitterFactory(confBuilder.build());

		Twitter twitter = twitterFactory.getInstance();

		try
		{
			Query query = new Query("#GolGR");
			QueryResult result;

			do
			{
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();

				for(Status tweet : tweets)
				{
					System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				}

			}
			while( (query = result.nextQuery()) != null );

		}
		catch(TwitterException te)
		{
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}

		System.out.println("all ok");
	}

}
