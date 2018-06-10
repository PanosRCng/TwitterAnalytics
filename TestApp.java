import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import twitter4j.*;
import twitter4j.api.UsersResources;

import TwitterAnalytics.TwitterApi;
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

				//this.userStats(user, false);
				this.userStats(user, true);

				//this.userTimeline(user.getId());
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

				this.amplifiers(status.getId());
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

	public void search(String query_string, String since, String until)
	{

		Date date=new Date();

		String sinceDate= new SimpleDateFormat(since).format(date);
		String untilDate= new SimpleDateFormat(until).format(date);

		try
		{
			Query query = new Query(query_string);
			QueryResult result;

			query.setSince(sinceDate);
			query.setUntil(untilDate);

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

	public void amplifiers(long status_id)
	{

		IDs ids;
		long cursor = -1;

		try {

			do {

				ids = TwitterApi.instance().tweets().getRetweeterIds(status_id, cursor);

				for (long id : ids.getIDs()) {
					System.out.println(TwitterApi.instance().users().showUser(id).getScreenName());
				}

			}while ((cursor = ids.getNextCursor()) != 0);

		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	public void userStats(User user, boolean insertInTable){

		System.out.println("Number of statuses : " + user.getStatusesCount());
		System.out.println("Number of favourites : " + user.getFavouritesCount());
		System.out.println("Number of followers : " + user.getFollowersCount());
		System.out.println("Number of friends : " + user.getFriendsCount());

		if(insertInTable){

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			final String stringDate= dateFormat.format(new Date());
			final java.sql.Date sqlDate=  java.sql.Date.valueOf(stringDate);

			String query = " insert into userStats (userID, numStatus, numFavourite, numFollowers, numFriends, submissionDate)"
					+ " values (?, ?, ?, ?, ?, ?)";

			PreparedStatement preparedStmt = null;
			try {
				preparedStmt = TwitterApi.twitterApiInstance().getConnection().prepareStatement(query);

				preparedStmt.setInt    (1, (int) user.getId());
				preparedStmt.setInt    (2, user.getStatusesCount());
				preparedStmt.setInt    (3, user.getFavouritesCount());
				preparedStmt.setInt    (4, user.getFollowersCount());
				preparedStmt.setInt    (5, user.getFriendsCount());
				preparedStmt.setDate   (6, sqlDate);

				// execute the preparedstatement
				preparedStmt.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args)
	{
		TestApp testApp = new TestApp();

		//testApp.showRateLimits();
		//testApp.search("#GolGR", "2018-06-05", "2018-06-07");

		TwitterApi.twitterApiInstance().setConnection();

		testApp.findUsers("Kathimerini_gr");

		Connection connection = TwitterApi.twitterApiInstance().getConnection();
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("all ok");
	}

}