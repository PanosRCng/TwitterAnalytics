import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import twitter4j.*;

import TwitterAnalytics.TwitterApi;
import TwitterAnalytics.DB;

import static java.lang.Math.max;


public class TestApp
{

	public void search(String query_string)
	{

		Multimap<Long, Long> amplifiers = ArrayListMultimap.create();
		Map<Long, Date> statusDate = new HashMap<Long, Date>();

		IDs ids;
		long cursor = -1;
		long pageID = -1;

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

				do {
					amplifiers.put(rs.getLong("userID"), rs.getLong("statusID"));
					statusDate.put(rs.getLong("statusID"), rs.getDate("date"));
					System.out.println("Me exei nevriasei : "+rs.getLong("userID")+" / "+ amplifiers.get(rs.getLong("userID")));
					System.out.println("cursor : "+ cursor);
					System.out.println("cursor in database: "+rs.getLong("cursorID"));
					cursor = max(cursor,rs.getLong("cursorID"));
					System.out.println("cursor used: "+cursor);
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

					pageID = tweet.getUser().getId();

					status_counter++;

					do {

						System.out.println("Cursor given : "+cursor);

						ids = TwitterApi.client().tweets().getRetweeterIds(tweet.getId(), cursor);

						for (long id : ids.getIDs()) {
							amplifiers_counter++;
							System.out.println(TwitterApi.client().users().showUser(id).getId());
							if(!amplifiers.containsEntry(id,tweet.getId())){
								amplifiers.put(id,tweet.getId());
							}
							if(!statusDate.containsKey(tweet.getId())){
								statusDate.put(tweet.getId(), tweet.getCreatedAt());
							}
							System.out.println(id+"........"+amplifiers.get(id));
							System.out.println("Cursor in loops : "+cursor);
						}

					}while ((cursor = ids.getNextCursor()) != 0);

				}

			}
			while( (query = result.nextQuery()) != null );

			//Map<Long, Integer> sortedMap = generalFunctions.sortByValue(amplifiers);

			//for(Map.Entry<Long, Integer> entry : sortedMap.entrySet())
			//{
				//System.out.println(entry.getKey() + " : " + entry.getValue());
			//}
		}
		catch(TwitterException te)
		{

			System.out.println("Status counter : "+status_counter);
			System.out.println("Amplifiers counter : "+amplifiers_counter);
			System.out.println("Cursor : "+cursor);

			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());

			String query = " insert into temp (userID, statusID, pageID, date, cursorID)"
					+ " values (?, ?, ?, ?, ?)";

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
						preparedStmt.setLong    (3, pageID);
						preparedStmt.setDate    (4, new java.sql.Date(statusDate.get(value).getTime()));
						preparedStmt.setLong    (5, cursor);

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