import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;

import twitter4j.*;
import twitter4j.api.UsersResources;

import TwitterAnalytics.TwitterApi;
import twitter4j.api.TimelinesResources;
import TwitterAnalytics.DB;


public class TestApp
{

	public void showRateLimits()
	{
		try
		{
			Map<String, RateLimitStatus> limits = TwitterApi.client().getRateLimitStatus();

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
			UsersResources userResource = TwitterApi.client().users();

			ResponseList<User> users = userResource.lookupUsers(search_string);

			for(User user : users)
			{
				System.out.println(user.getName());

				//this.userStats(user, false);
				//this.userStats(user, true);

				this.printUserStatsByDate(user);

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

	public void getAmplifiersStats(long user_id)
	{
		try
		{
			TimelinesResources timelinesResource = TwitterApi.client().timelines();

			ResponseList<Status> statuses = timelinesResource.getUserTimeline(user_id);

			Map<Long, Integer> amplifiersStats = new HashMap<Long, Integer>();

			for(Status status : statuses)
			{
				//System.out.println(status.getText());

				amplifiersStats = this.amplifiers(status.getId(), amplifiersStats);
			}

			Map<Long, Integer> sortedMap = sortByValue(amplifiersStats);

			for(Map.Entry<Long, Integer> entry : sortedMap.entrySet())
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


	public void search(String query_string)
	{
		try
		{
			Query query = new Query(query_string);
			QueryResult result;

			do
			{
				result = TwitterApi.client().search(query);
				List<Status> tweets = result.getTweets();

				for(Status tweet : tweets)
				{
					//System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());

					getAmplifiersStats(tweet.getUser().getId());
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

	public Map<Long, Integer> amplifiers(long status_id, Map<Long, Integer> amplifiersStats)
	{

		IDs ids;
		long cursor = -1;

		try {

			do {

				ids = TwitterApi.client().tweets().getRetweeterIds(status_id, cursor);

				for (long id : ids.getIDs()) {
					//System.out.println(TwitterApi.client().users().showUser(id).getScreenName());
					if(amplifiersStats.containsKey(id)){
						amplifiersStats.replace(id, amplifiersStats.get(id)+1);
					}else {
						amplifiersStats.put(id, 1);
					}
				}

			}while ((cursor = ids.getNextCursor()) != 0);

		} catch (TwitterException e) {

			e.printStackTrace();

			String query = " insert into temp (userID, counter, cursorID)"
					+ " values (?, ?, ?)";

			try {

				PreparedStatement preparedStmt = DB.conn().prepareStatement(query);

				for(Map.Entry<Long, Integer> entry : amplifiersStats.entrySet())
				{

					System.out.println(entry.getKey() + " : " + entry.getValue());

					preparedStmt.setInt    (1, entry.getKey().intValue());
					preparedStmt.setInt    (2, entry.getValue());
					preparedStmt.setInt    (3, (int) cursor);

					preparedStmt.addBatch();
				}

				// execute the batch
				int[] updateCounts = preparedStmt.executeBatch();

				checkUpdateCounts(updateCounts);

				// since there were no errors, commit
				DB.conn().commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		return(amplifiersStats);
	}

	public static void checkUpdateCounts(int[] updateCounts) {
		for (int i=0; i<updateCounts.length; i++) {
			if (updateCounts[i] >= 0) {
				System.out.println("OK; updateCount="+updateCounts[i]);
			}
			else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
				System.out.println("OK; updateCount=Statement.SUCCESS_NO_INFO");
			}
			else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
				System.out.println("Failure; updateCount=Statement.EXECUTE_FAILED");
			}
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
				preparedStmt = DB.conn().prepareStatement(query);

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

	public void printUserStatsByDate(User user){

		String query = " select * from userStats where userID=? order by submissionDate";

		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = DB.conn().prepareStatement(query);

			preparedStmt.setInt(1, (int) user.getId());

			ResultSet rs = preparedStmt.executeQuery();

			while (rs.next())
			{
				int numStatuses = rs.getInt("numStatus");
				int numFriends = rs.getInt("numFriends");
				int numFavourites = rs.getInt("numFavourite");
				int numFollowers = rs.getInt("numFollowers");
				Date dateSubmitted = rs.getDate("submissionDate");
				// print the results
				System.out.format("Date : %s, Number of Statuses : %s, Number of friends : %s, " +
								"Number of Favourites : %s, Number of Followers : %s\n",
						dateSubmitted, numStatuses, numFriends, numFavourites, numFollowers);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static Map<Long, Integer>sortByValue(Map<Long, Integer> unsortMap) {

		List<Map.Entry<Long, Integer>> list =
				new LinkedList<Map.Entry<Long, Integer>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Long, Integer>>() {
			public int compare(Map.Entry<Long, Integer> o1,
							   Map.Entry<Long, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<Long, Integer> sortedMap = new LinkedHashMap<Long, Integer>();
		for (Map.Entry<Long, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static void main(String[] args)
	{
		TestApp testApp = new TestApp();

		//testApp.showRateLimits();
		//testApp.search("#GolGR", "2018-06-05", "2018-06-07");

		testApp.search("Kathimerini_gr");

		System.out.println("all ok");
	}

}