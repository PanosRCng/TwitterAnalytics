import java.util.List;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;
import twitter4j.Status;
import twitter4j.api.UsersResources;

import TwitterAnalytics.TwitterApi;
import twitter4j.User;
import twitter4j.ResponseList;
import twitter4j.api.TimelinesResources;


public class TestApp
{

	public void showRateLimits()
	{
		try
		{
			Map<String, RateLimitStatus> limits = TwitterApi.instance().getRateLimitStatus();

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


	public void findUsers(String search_string)
	{
		try
		{
			UsersResources userResource = TwitterApi.instance().users();

			ResponseList<User> users = userResource.lookupUsers(search_string);

			for(User user : users)
			{
				System.out.println(user.getName());

				this.userTimeline(user.getId());
			}
		}
		catch(TwitterException twitterException)
		{
			twitterException.printStackTrace();
			System.out.println("Failed : " + twitterException.getMessage());
		}
	}


	public void userTimeline(long user_id)
	{
		try
		{
			TimelinesResources timelinesResource = TwitterApi.instance().timelines();

			ResponseList<Status> statuses = timelinesResource.getUserTimeline(user_id);

			for(Status status : statuses)
			{
				System.out.println(status.getText());
			}
		}
		catch(TwitterException twitterException)
		{
			twitterException.printStackTrace();
			System.out.println("Failed : " + twitterException.getMessage());
		}
	}


	public void search(String query_string)
	{
		try
		{
			Query query = new Query(query_string);
			QueryResult result;

			do
			{
				result = TwitterApi.instance().search(query);
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
		}
	}


	public static void main(String[] args)
	{
		TestApp testApp = new TestApp();

		//testApp.showRateLimits();
		//testApp.search("#GolGR");
		testApp.findUsers("Kathimerini_gr");

		System.out.println("all ok");
	}

}
