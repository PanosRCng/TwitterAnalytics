import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import twitter4j.*;

import TwitterAnalytics.TwitterApi;
import TwitterAnalytics.DB;
import twitter4j.api.TimelinesResources;

public class TestApp
{

	public void trackUserTimeLine(String query_string, UserRetweeterGraph userRetweeterGraph)
	{

		Multimap<Long, Long> amplifiers = ArrayListMultimap.create();
		Map<Long, Date> statusDate = new HashMap<Long, Date>();

		Multimap<Long, Long> userTweets = ArrayListMultimap.create();

		ResponseList<User> users;
		int counter=0;
		long userID=-1;

		IDs ids;
		long cursor = -1;

		GeneralFunctions generalFunctions = new GeneralFunctions();

		String query_temp = "select * from temp";

		PreparedStatement preparedStmt = null;

		try {
			preparedStmt = DB.conn().prepareStatement(query_temp);

			ResultSet rs = preparedStmt.executeQuery();

			boolean checkDB = rs.next();

			if(checkDB){

				do {
					amplifiers.put(rs.getLong("userID"), rs.getLong("statusID"));
					statusDate.put(rs.getLong("statusID"), rs.getDate("date"));
				}while (rs.next());

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try
		{
			TimelinesResources timelinesResource = TwitterApi.client().timelines();

			do
			{
				users = TwitterApi.client().searchUsers(query_string, -1);

				for (User user : users) {
					counter++;
					userID = user.getId();
					break;
				}

				ResponseList<Status> tweets = timelinesResource.getUserTimeline(userID);

				for(Status tweet : tweets)
				{
					//System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());

					//getAmplifiersStats(tweet.getUser().getId());

					do {

						ids = TwitterApi.client().tweets().getRetweeterIds(tweet.getId(), cursor);

						for (long id : ids.getIDs()) {

							if(!amplifiers.containsValue(userID)) userRetweeterGraph.getInstance().addVertex(userID);
							if(!statusDate.containsKey(tweet.getId())){
								statusDate.put(tweet.getId(), tweet.getCreatedAt());
							}
							if(!amplifiers.containsEntry(id,tweet.getId())){
								amplifiers.put(id,tweet.getId());

								userRetweeterGraph.getInstance().addVertex(id);
								userRetweeterGraph.getInstance().addEdge(id, userID);
							}

							ResponseList<Status> tweetsUser = timelinesResource.getUserTimeline(id);

							String user_tweets = "select * from tweets where userID="+id;

							PreparedStatement ps = null;

							try {
								ps = DB.conn().prepareStatement(user_tweets);

								ResultSet rs = ps.executeQuery();

								boolean checkDB = rs.next();

								if(checkDB){

									do {
										if(!userTweets.containsEntry(rs.getLong("userID"),rs.getLong("statusID"))) userTweets.put(rs.getLong("userID"), rs.getLong("statusID"));
									}while (rs.next());

								}
							} catch (SQLException e) {
								e.printStackTrace();
							}

							user_tweets = "insert into tweets (userID, statusID, date) values (?, ?, ?)";

							for(Status status : tweetsUser)
							{
								if(!userTweets.containsEntry(id,status.getId())){
									try {
										ps = DB.conn().prepareStatement(user_tweets);

										ps.setLong    (1, id);
										ps.setLong    (2, status.getId());
										ps.setDate   (3, new java.sql.Date(status.getCreatedAt().getTime()));

										// execute the preparedstatement
										ps.execute();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
							}

						}

					}while ((cursor = ids.getNextCursor()) != 0);

				}

			}while(users.size() != 0 && counter==0);

			//Map<Long, Integer> sortedMap = generalFunctions.sortByValue(amplifiers);

			//for(Map.Entry<Long, Integer> entry : sortedMap.entrySet())
			//{
				//System.out.println(entry.getKey() + " : " + entry.getValue());
			//}
		}
		catch(TwitterException te)
		{

			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());

			String query = " insert into temp (userID, statusID, pageID, date)"
					+ " values (?, ?, ?, ?)";

			try {

				DB.conn().createStatement().executeUpdate("truncate temp");

				DB.conn().setAutoCommit(false);

				preparedStmt = DB.conn().prepareStatement(query);

				for(Long key : amplifiers.keySet())
				{

					//System.out.println(entry.getKey() + " : " + entry.getValue());

					Collection<Long> values = amplifiers.get(key);

					for(Long value : values){

						preparedStmt.setLong    (1, key);
						preparedStmt.setLong    (2, value);
						preparedStmt.setLong    (3, userID);
						preparedStmt.setDate    (4, new java.sql.Date(statusDate.get(value).getTime()));

						preparedStmt.addBatch();

					}

				}

				// execute the batch
				int[] updateCounts = preparedStmt.executeBatch();

				generalFunctions.checkUpdateCounts(updateCounts);

				// since there were no errors, commit
				DB.conn().commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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

	public static void main(String[] args)
	{

		//StreamTwitterUser streamTwitterUser = new StreamTwitterUser("@sport24");

		TestApp testApp = new TestApp();
		//GeneralFunctions generalFunctions = new GeneralFunctions();

		//generalFunctions.printTweets(2845541223L);

		UserRetweeterGraph userRetweeterGraph = new UserRetweeterGraph();

		while(true){
			GeneralFunctions generalFunctions = new GeneralFunctions();
			boolean checkLimit = generalFunctions.checkRateLimit();

			if(checkLimit) {
				try {
					Thread.sleep(900000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println(userRetweeterGraph.getInstance().toString());

			testApp.trackUserTimeLine("@sport24", userRetweeterGraph);

			System.out.println("all ok");

		}


		//testApp.showRateLimits();
		//testApp.search("olympiacosbc", "2018-05-05", "2018-06-25");

		//String stringUrl = "http://localhost:8888/userTweetGraph/topUsers?k=10";

		//generalFunctions.topUsers(stringUrl);
	}

}