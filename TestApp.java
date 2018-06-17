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

		int status_counter = 0;
		int amplifiers_counter = 0;

		String query_temp = "select * from temp";

		PreparedStatement preparedStmt = null;

		try {
			preparedStmt = DB.conn().prepareStatement(query_temp);

			ResultSet rs = preparedStmt.executeQuery();

			boolean checkDB = rs.next();
			System.out.println(checkDB);

			if(checkDB){

				{
					amplifiersStats.put(rs.getLong("userID"), rs.getInt("counter"));

					System.out.println("cursor : "+(int)cursor);
					System.out.println("cursor in database: "+rs.getInt("cursorID"));
					System.out.println("cursor in database plus 1: "+(rs.getInt("cursorID")+1));
					cursor = (long) max((int)cursor,rs.getInt("cursorID")+1);
				}while (rs.next());

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

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

					//getAmplifiersStats(tweet.getUser().getId());

					status_counter++;

					do {

						status_id = tweet.getId();

						ids = TwitterApi.client().tweets().getRetweeterIds(status_id, cursor);

						for (long id : ids.getIDs()) {
							amplifiers_counter++;
							System.out.println(TwitterApi.client().users().showUser(id).getId());
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

			System.out.println("Status counter : "+status_counter);
			System.out.println("Amplifiers counter : "+amplifiers_counter);
			System.out.println("Cursor : "+(int)cursor);

			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());

			String query = " insert into temp (userID, counter, statusID, cursorID)"
					+ " values (?, ?, ?, ?)";

			try {

				DB.conn().createStatement().executeUpdate("truncate temp");

				DB.conn().setAutoCommit(false);

				preparedStmt = DB.conn().prepareStatement(query);

				for(Map.Entry<Long, Integer> entry : amplifiersStats.entrySet())
				{

					//System.out.println(entry.getKey() + " : " + entry.getValue());

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

			testApp.search("olympiacosbc");

			System.out.println("all ok");

		}


		//testApp.showRateLimits();
		//testApp.search("#GolGR", "2018-06-05", "2018-06-07");


	}

}