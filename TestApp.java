import java.util.*;

import TwitterAnalytics.Models.DBModel;
import TwitterAnalytics.Models.Trend;
import TwitterAnalytics.Models.Tweet;
import TwitterAnalytics.Sentimenter.SentimentLexicon.SentimentLexicon;
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

import java.sql.Timestamp;



public class TestApp
{

	public void findUsers(String search_string)
	{
		try
		{
			UsersResources userResource = TwitterApi.client().users();

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
			TimelinesResources timelinesResource = TwitterApi.client().timelines();

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
				result = TwitterApi.client().search(query);

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


	public void search(int trend_id, String query_string, int max_results)
	{
		try
		{
			Query query = new Query(query_string);
			query.setResultType(Query.RECENT);

			QueryResult result;

			int tweet_counter = 0;

			do
			{
				result = TwitterApi.client().search(query);

				List<Status> tweets = result.getTweets();

				for(Status tweet : tweets)
				{
					if(tweet.isRetweet())
					{
						continue;
					}

					tweet_counter++;

					Tweet tweetC = new Tweet(tweet.getText(), tweet.getId(), trend_id, new java.sql.Timestamp(tweet.getCreatedAt().getTime()) );
					tweetC.save();
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



	public static void main(String[] args)
	{
		//TestApp testApp = new TestApp();
		//testApp.showRateLimits();
		//testApp.search("#GolGR");
		//testApp.findUsers("Kathimerini_gr");

		//TestApp2 testApp2 = new TestApp2();

		TrendsApp trendsApp = new TrendsApp();


		//SentimentLexicon sentimentLexicon = SentimentLexicon.load();


		System.out.println("all ok");
	}

}
