import twitter4j.*;
import twitter4j.api.UsersResources;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

public class TestApp
{

	public static void main(String[] args) throws TwitterException {
		ConfigurationBuilder confBuilder = new ConfigurationBuilder();

		confBuilder.setDebugEnabled(true)
				.setOAuthConsumerKey(Config.CONSUMER_KEY)
				.setOAuthConsumerSecret(Config.CONSUMER_SECRET)
				.setOAuthAccessToken(Config.ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(Config.ACCESS_TOKEN_SECRET);

		TwitterFactory twitterFactory = new TwitterFactory(confBuilder.build());

		Twitter twitter = twitterFactory.getInstance();

		UsersResources userResource = twitter.users();

		ResponseList<User> users = userResource.lookupUsers("Kathimerini_gr");

		for(User user : users) {

		}

		System.out.println("all ok");
	}

}