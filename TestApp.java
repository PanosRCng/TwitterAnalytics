import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;

import twitter4j.RateLimitStatus;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;
import twitter4j.Status;
import twitter4j.api.TrendsResources;
import twitter4j.api.UsersResources;

import TwitterAnalytics.TwitterApi;
import twitter4j.User;
import twitter4j.ResponseList;
import twitter4j.api.TimelinesResources;
import twitter4j.Trends;
import twitter4j.Trend;



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
			query.setResultType(Query.RECENT);
			query.setCount(100);

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


	public void search(String query_string, int max_results)
	{
		try
		{
			Query query = new Query(query_string);
			query.setResultType(Query.RECENT);

			QueryResult result;

			int tweet_counter = 0;

			do
			{
				result = TwitterApi.instance().search(query);

				List<Status> tweets = result.getTweets();

				for(Status tweet : tweets)
				{
					if(tweet.isRetweet())
					{
						continue;
					}

					tweet_counter++;

					System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				}

			}
			while( ((query = result.nextQuery()) != null) && (tweet_counter <max_results) );

			System.out.println("counter is: " + tweet_counter);
		}
		catch(TwitterException te)
		{
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
		}
	}


	public List<Trend> getTopTrends(int woeid, int top)
	{
		try
		{
			TrendsResources trendsResources = TwitterApi.instance().trends();

			Trends trends = trendsResources.getPlaceTrends(woeid);
			Trend[] trendsArray = trends.getTrends();

			return Arrays.asList(trendsArray).subList(0, 10);
		}
		catch(TwitterException te)
		{
			te.printStackTrace();
			System.out.println("Failed to find trends: " + te.getMessage());
		}

		return Collections.emptyList();
	}


	public void getTrends(int woeid)
	{
		List<Trend> top10trends = this.getTopTrends(woeid, 10);

		for(Trend trend : top10trends)
		{
			System.out.println("name: " + trend.getName() + ", query: " + trend.getQuery() + ", url: " + trend.getURL());

			this.search( trend.getQuery(), 100 );
		}
	}



	public static void main(String[] args)
	{
		TestApp testApp = new TestApp();

		//testApp.showRateLimits();
		//testApp.search("#GolGR");
		//testApp.findUsers("Kathimerini_gr");
		testApp.getTrends(23424833);

		System.out.println("all ok");
	}

}
