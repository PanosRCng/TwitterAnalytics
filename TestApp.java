import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import twitter4j.*;

import TwitterAnalytics.TwitterApi;
import TwitterAnalytics.DB;

import static java.lang.Math.max;


public class TestApp
{

	public void search(String query_string)
	{

		Map<Long, Integer> amplifiersStats = new HashMap<Long, Integer>();

		IDs ids;
		long cursor = -1;
		long status_id = -1;

		GeneralFunctions generalFunctions = new GeneralFunctions();

		try
		{
			Query query = new Query(query_string);
			QueryResult result;

			do
			{
				result = TwitterApi.client().search(query);
				List<Status> tweets = result.getTweets();

				String query_temp = "select * from temp";

				PreparedStatement preparedStmt = null;

				try {
					preparedStmt = DB.conn().prepareStatement(query_temp);

					ResultSet rs = preparedStmt.executeQuery();

					boolean yolo = rs.next();
					System.out.println(yolo);

					if(yolo){

						while (rs.next())
						{
							amplifiersStats.put(rs.getLong("userID"), rs.getInt("counter"));

							cursor = max(cursor,rs.getInt("cursorID"));
						}

					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				for(Status tweet : tweets)
				{
					//System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
					System.out.println(tweet.getId());
					//getAmplifiersStats(tweet.getUser().getId());

					do {

						status_id = tweet.getId();

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

				}

			}
			while( (query = result.nextQuery()) != null );

			Map<Long, Integer> sortedMap = generalFunctions.sortByValue(amplifiersStats);

			for(Map.Entry<Long, Integer> entry : sortedMap.entrySet())
			{
				System.out.println(entry.getKey() + " : " + entry.getValue());
			}
		}
		catch(TwitterException te)
		{
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());

			String query = " insert into temp (userID, counter, statusID, cursorID)"
					+ " values (?, ?, ?, ?)";

			try {

				DB.conn().setAutoCommit(false);

				PreparedStatement preparedStmt = DB.conn().prepareStatement(query);

				for(Map.Entry<Long, Integer> entry : amplifiersStats.entrySet())
				{

					System.out.println(entry.getKey() + " : " + entry.getValue());

					preparedStmt.setLong    (1, entry.getKey());
					preparedStmt.setInt    (2, entry.getValue());
					preparedStmt.setLong    (3, status_id);
					preparedStmt.setInt    (4, (int) cursor);

					preparedStmt.addBatch();
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
		TestApp testApp = new TestApp();

		GeneralFunctions generalFunctions = new GeneralFunctions();
		generalFunctions.checkRateLimit();

		//testApp.showRateLimits();
		//testApp.search("#GolGR", "2018-06-05", "2018-06-07");

		testApp.search("Kathimerini_gr");

		System.out.println("all ok");
	}

}