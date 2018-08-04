import TwitterAnalytics.DB;
import TwitterAnalytics.TextAnalysis.Sentimenter.Sentimenter;
import TwitterAnalytics.TextAnalysis.Stemmer.Stemmer;
import TwitterAnalytics.TextAnalysis.Tokenizer.Tokenizer;
import TwitterAnalytics.TextAnalysis.Utils;
import TwitterAnalytics.TwitterApi;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Longs;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.AlphaCentrality;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.alg.scoring.HarmonicCentrality;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import twitter4j.*;
import twitter4j.api.TimelinesResources;
import twitter4j.api.TweetsResources;
import twitter4j.api.UsersResources;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import TwitterAnalytics.Models.Tweet;

public class TestApp
{

	public void printCentralityResult(String centrality, boolean printMostInfluentialPerson) {

		UserRetweeterGraph graph = new UserRetweeterGraph();

		String query_temp = "select * from retweetertable";

		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = DB.conn().prepareStatement(query_temp);

			ResultSet rs = preparedStmt.executeQuery();

			while (rs.next()) {

				if (!graph.getInstance().containsVertex(rs.getLong("retweeterID"))) {
					graph.getInstance().addVertex(rs.getLong("retweeterID"));
				}
				if (!graph.getInstance().containsVertex(rs.getLong("retweetedUserID"))) {
					graph.getInstance().addVertex(rs.getLong("retweetedUserID"));
				}
				graph.getInstance().addEdge(rs.getLong("retweeterID"), rs.getLong("retweetedUserID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Map<Long, Double> map;

		if (centrality.equals("alpha")) {

			AlphaCentrality alphaCentrality = new AlphaCentrality(graph.getInstance());
			map = GeneralFunctions.sortByValue(alphaCentrality.getScores());
			System.out.println(map);

		} else if ((centrality.equals("betweenness"))) {

			BetweennessCentrality betweennessCentrality = new BetweennessCentrality(graph.getInstance());
			map = GeneralFunctions.sortByValue(betweennessCentrality.getScores());
			System.out.println(map);

		} else if ((centrality.equals("closeness"))) {

			ClosenessCentrality closenessCentrality = new ClosenessCentrality(graph.getInstance());
			map = GeneralFunctions.sortByValue(closenessCentrality.getScores());
			System.out.println(map);

		}else if((centrality.equals("harmonic"))) {

			HarmonicCentrality harmonicCentrality = new HarmonicCentrality(graph.getInstance());
			map = GeneralFunctions.sortByValue(harmonicCentrality.getScores());
			System.out.println(map);

		}else{

			AlphaCentrality alphaCentrality = new AlphaCentrality(graph.getInstance());
			map = GeneralFunctions.sortByValue(alphaCentrality.getScores());
			System.out.println(map);

		}

		if(printMostInfluentialPerson==true){
			try
			{
				UsersResources userResource = TwitterApi.client().users();

				Iterator iter = map.entrySet().iterator();

				Map.Entry<Long, Double> entry = (Map.Entry<Long, Double>) iter.next();

				Long key = entry.getKey();

				User user = userResource.showUser(key);

				System.out.println("Most influential person: " + user.getName() + " - " + user.getScreenName());
				Double value = entry.getValue();
				System.out.println("Score: " + Math.round(value * 100.0) / 100.0);
				Set<DefaultEdge> userTweets = graph.getInstance().edgesOf(key);
				System.out.println("Number of edges: " + userTweets.size());
				System.out.println("Location : " + user.getLocation());

			}
			catch(TwitterException twitterException)
			{
				twitterException.printStackTrace();
				System.out.println("Failed : " + twitterException.getMessage());
			}

		}

		UsersResources userResource = TwitterApi.client().users();
		TweetsResources tweetResource = TwitterApi.client().tweets();

		Tokenizer tokenizer = new Tokenizer();

		Map<Object, Double> wordCount = new HashMap<Object, Double>();

		int counter = 1;

		for (Map.Entry<Long, Double> entry : map.entrySet())
		{

			User user = null;
			try {
				user = userResource.showUser(entry.getKey());


				String user_tweets = "select * from tweets where userID="+user.getId();

				try {
					preparedStmt = DB.conn().prepareStatement(user_tweets);

					ResultSet rs = preparedStmt.executeQuery();

					ArrayList<Long> ids = new ArrayList<Long>();

                    while(rs.next()) ids.add(rs.getLong("statusID"));

                    if(ids.size()>0) {

						long[][] batches = GeneralFunctions.chunkArray(Longs.toArray(ids), 100);

						for(int i=0;i<batches.length;i++) {

							ResponseList<Status> tweets = tweetResource.lookup(batches[i]);

							for (Status tweet : tweets) {

								Vector<String> tokens = tokenizer.tokenize(TwitterApi.cleanTweetText(tweet));

								Vector<String> stems = Stemmer.stem(Utils.lowercase(tokens));

								//Vector<Double> t_vector = Sentimenter.sentimentVector(stems);

								for(String stem : stems){
									double wordCounter = 0;
									if(wordCount.containsKey(stem)) wordCounter = wordCount.get(stem);
									wordCounter = wordCounter + 1;
									wordCount.put(stem, wordCounter);
								}

								/*System.out.println("anger: " + t_vector.get(0));
							System.out.println("disgust: " + t_vector.get(1));
							System.out.println("fear: " + t_vector.get(2));
							System.out.println("happiness: " + t_vector.get(3));
							System.out.println("sadness: " + t_vector.get(4));
							System.out.println("surprise: " + t_vector.get(5));*/
							}
						}

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			} catch (TwitterException e) {
				e.printStackTrace();
			}

			if(counter++==10) break;
		}

		wordCount = GeneralFunctions.sortByValue(wordCount);

		for(Map.Entry<Object, Double> entry : wordCount.entrySet()){
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}

	}

	public void printUserTweetsFromGraph(){

		Graph<Long, DefaultEdge> graph = new DefaultUndirectedGraph<Long, DefaultEdge>(DefaultEdge.class);

		String query_temp = "select * from tweets";

		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = DB.conn().prepareStatement(query_temp);

			ResultSet rs = preparedStmt.executeQuery();

			while (rs.next()){

				boolean flag = false;
				if(!graph.containsVertex(rs.getLong("userID"))){
					graph.addVertex(rs.getLong("userID"));
					flag = true;
				}
				if(!graph.containsVertex(rs.getLong("statusID"))){
					graph.addVertex(rs.getLong("statusID"));
					flag = true;
				}
				if(flag==true) graph.addEdge(rs.getLong("userID"), rs.getLong("statusID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Set<Long> vertices = graph.vertexSet();

		ArrayList<Long> ids = new ArrayList<Long>();

		for (Long vertex : vertices) {
			Set<DefaultEdge> userTweets = graph.edgesOf(vertex);
			for(DefaultEdge edge : userTweets) {

				String tweetID = edge.toString().substring(0, edge.toString().length() - 1).split(":")[1].trim();

				System.out.println(tweetID);

				ids.add(Long.parseLong(tweetID));
			}

			break;
		}

		if(ids.size()>0) {

			long[][] batches = GeneralFunctions.chunkArray(Longs.toArray(ids), 100);

			for(int i=0;i<batches.length;i++) {

				try {
					ResponseList<Status> tweets = TwitterApi.client().tweets().lookup(batches[i]);

					for (Status tweet : tweets) {
						System.out.println(tweet.getText());
					}
				} catch (TwitterException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void trackUserTimeLine(String query_string, UserRetweeterGraph userRetweeterGraph,
								  Multimap<Long, Long> amplifiers, Map<Long, Date> statusDate, Map<Long, Long> statusPageID,
								  Multimap<Long, Long> userTweets, Multimap<Long, Long> userRetweeters, Paging paging)
	{

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
					if(!amplifiers.containsEntry(rs.getLong("userID"),rs.getLong("statusID"))) amplifiers.put(rs.getLong("userID"), rs.getLong("statusID"));
					if(!statusDate.containsKey(rs.getLong("statusID"))) statusDate.put(rs.getLong("statusID"), rs.getDate("date"));
					if(!statusPageID.containsKey(rs.getLong("statusID"))) statusPageID.put(rs.getLong("statusID"), rs.getLong("pageID"));
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

				ResponseList<Status> tweets = timelinesResource.getUserTimeline(userID, paging);

				for(Status tweet : tweets)
				{
					//System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());

					//getAmplifiersStats(tweet.getUser().getId());

					do {

						ids = TwitterApi.client().tweets().getRetweeterIds(tweet.getId(), cursor);

						for (long id : ids.getIDs()) {

							if(!statusDate.containsKey(tweet.getId())){
								statusDate.put(tweet.getId(), tweet.getCreatedAt());
							}
							if(!statusPageID.containsKey(tweet.getId())){
								statusPageID.put(tweet.getId(), userID);
							}
							if(!amplifiers.containsEntry(id,tweet.getId())){
								amplifiers.put(id,tweet.getId());

								if(!userRetweeterGraph.getInstance().containsVertex(userID)) userRetweeterGraph.getInstance().addVertex(userID);

								userRetweeterGraph.getInstance().addVertex(id);
								userRetweeterGraph.getInstance().addEdge(id, userID);

							}

							ResponseList<Status> tweetsUser = timelinesResource.getUserTimeline(id);

							String user_tweets = "select * from tweets where userID="+id;
							String user_retweeters = "select * from retweetertable where retweetedUserID="+id;

							PreparedStatement ps = null;
							PreparedStatement ps2 = null;

							try {
								ps = DB.conn().prepareStatement(user_tweets);
								ps2 = DB.conn().prepareStatement(user_retweeters);

								ResultSet rs = ps.executeQuery();
								ResultSet rs2 = ps2.executeQuery();

								boolean checkDB = rs.next();
								boolean check2DB = rs2.next();

								if(checkDB){

									do {
										if(!userTweets.containsEntry(rs.getLong("userID"),rs.getLong("statusID"))) userTweets.put(rs.getLong("userID"), rs.getLong("statusID"));
									}while (rs.next());

								}

								if(check2DB){

									do {
										if(!userRetweeters.containsEntry(rs2.getLong("retweeterID"),rs2.getLong("retweetedUserID"))) userRetweeters.put(rs2.getLong("retweeterID"),rs2.getLong("retweetedUserID"));
									}while (rs2.next());

								}
							} catch (SQLException e) {
								e.printStackTrace();
							}

							user_tweets = "insert into tweets (userID, statusID, date) values (?, ?, ?)";
							user_retweeters = "insert into retweetertable (retweeterID, retweetedUserID, date) values (?, ?, ?)";

							for(Status status : tweetsUser)
							{
								if(!userTweets.containsEntry(id,status.getId())){
									try {

										userTweets.put(id,status.getId());

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

								IDs retweeter_ids;
								long retweeter_cursor = -1;

								try {

									do {
										retweeter_ids = TwitterApi.client().tweets().getRetweeterIds(status.getId(), retweeter_cursor);

										for (long retweeter_id : retweeter_ids.getIDs()) {
											if (!userRetweeters.containsEntry(retweeter_id, id)) {

												userRetweeters.put(retweeter_id,id);

												try {
													ps = DB.conn().prepareStatement(user_retweeters);

													ps.setLong(1, retweeter_id);
													ps.setLong(2, id);
													ps.setDate(3, new java.sql.Date(status.getCreatedAt().getTime()));

													// execute the preparedstatement
													ps.execute();
												} catch (SQLException e) {
													e.printStackTrace();
												}
											}
										}

									}while ((retweeter_cursor = ids.getNextCursor()) != 0);

								} catch (TwitterException e) {

									e.printStackTrace();

								}
							}

						}

					}while ((cursor = ids.getNextCursor()) != 0);

				}

			}while(users.size() != 0 && counter==0);

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
						preparedStmt.setLong    (3, statusPageID.get(value));
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

	public void trackUserTimeLine(String pagename, Paging paging, Map<Object, Double> stemMap, Map<Object, Double> sentimentMap) {

		TimelinesResources timelinesResource = TwitterApi.client().timelines();

		ResponseList<User> users;
		int counter = 0;
		long userID = -1;

		Tokenizer tokenizer = new Tokenizer();

		try {

			//do {

				/*users = TwitterApi.client().searchUsers(pagename, -1);

				for (User user : users) {
					counter++;
					userID = user.getId();
					break;
				}*/

				userID = TwitterApi.client().showUser(pagename).getId();

				ResponseList<Status> tweets = timelinesResource.getUserTimeline(userID, paging);

				for(Status tweet : tweets) {

					ResponseList<Status> retweets = TwitterApi.client().getRetweets(tweet.getId());

					for(Status retweet : retweets){

						Vector<String> tokens = tokenizer.tokenize(TwitterApi.cleanTweetText(retweet));

						Vector<String> stems = Stemmer.stem(Utils.lowercase(tokens));

						for(String stem : stems){
							double wordCounter = 0;
							if(stemMap.containsKey(stem)) wordCounter = stemMap.get(stem);
							wordCounter = wordCounter + 1;
							stemMap.put(stem, wordCounter);
						}

						Vector<Double> t_vector = Sentimenter.sentimentVector(stems);

						double max = 0;
						int sentiment=-1;

						if(t_vector!=null) {
							for (int i = 0; i < 6; i++) {
								if (t_vector.get(i) > max) sentiment = i;
							}

							double tempCounter = 0;
							if(sentimentMap.containsKey(sentiment)) tempCounter = sentimentMap.get(sentiment);
							tempCounter=tempCounter + 1;

							sentimentMap.put(sentiment, tempCounter);
						}

					}

				}


			//} while (users.size() != 0 && counter == 0) ;

		} catch(TwitterException e){
			e.printStackTrace();
		}
	}

	public void readComments(String pagename, Paging paging, ArrayList<Status> replies){

		TimelinesResources timelinesResource = TwitterApi.client().timelines();

		//ArrayList<Status> replies = null;

		try {

			User user = TwitterApi.client().showUser(pagename);

			ResponseList<Status> tweets = timelinesResource.getUserTimeline(user.getId(), paging);

			int counter = 0;
			for(Status tweet : tweets) {

				//System.out.println("Tweet: "+tweet.getText());

				getReplies(pagename, tweet.getId(), replies);

				/*for(Status reply : replies){
					System.out.println("Reply: "+reply.getText());
				}*/
				counter = counter + 1;
			}

			System.out.println("Counter : "+counter);

		} catch(TwitterException e){
			e.printStackTrace();
		}

	}

	public void getReplies(String screenName, long tweetID, ArrayList<Status> replies) {
		//ArrayList<Status> replies = new ArrayList<Status>();
		Tokenizer tokenizer = new Tokenizer();

		try {
			Query query = new Query(screenName);
			QueryResult results;

			do {
				results = TwitterApi.client().search(query);
				//System.out.println("Results: " + results.getTweets().size());
				List<Status> tweets = results.getTweets();

				for (Status tweet : tweets)
					if (tweet.getInReplyToStatusId() == tweetID) {
						//Vector<String> tokens = tokenizer.tokenize(TwitterApi.cleanTweetText(tweet));

						//Vector<String> stems = Stemmer.stem(Utils.lowercase(tokens));
						replies.add(tweet);
						System.out.println("Size "+replies.size());
						System.out.println(tweet.getCreatedAt());
						System.out.println(tweet.getText());
						/*Vector<Double> t_vector = Sentimenter.sentimentVector(stems);
						System.out.println(t_vector);
						if(t_vector!=null) {
							System.out.println("anger: " + t_vector.get(0));
							System.out.println("disgust: " + t_vector.get(1));
							System.out.println("fear: " + t_vector.get(2));
							System.out.println("happiness: " + t_vector.get(3));
							System.out.println("sadness: " + t_vector.get(4));
							System.out.println("surprise: " + t_vector.get(5));
						}*/
					}
			} while ((query = results.nextQuery()) != null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sentimentOverTime(String pagename, String category,
								  Paging paging, ArrayList<Status> tweets){

		Tokenizer tokenizer = new Tokenizer();

		Map<String,Map<String, Vector<Double>>> sentimentOverTime = new HashMap<String, Map<String, Vector<Double>>>();
System.out.print("size of array : "+tweets.size());
		//try {
			if(category.equals("replies")) {

				readComments(pagename, paging, tweets);

			}else if(category.equals("retweets")){

				//tweets.addAll(search(pagename, false));

			}
		//}catch (Exception e) {

			System.out.println("yoloooooo");
		    System.out.println(tweets.size());

			Vector<Double> vector;

			for (Status tweet : tweets) {

				Vector<String> tokens = tokenizer.tokenize(TwitterApi.cleanTweetText(tweet));

				Vector<String> stems = Stemmer.stem(Utils.lowercase(tokens));

				Vector<Double> t_vector = Sentimenter.sentimentVector(stems);

				if(t_vector==null) System.out.println("NULL vector");

				if(t_vector!=null) {
					if (!sentimentOverTime.containsKey(tweet.getCreatedAt())) {
						Map<String, Vector<Double>> map2 = new HashMap<String, Vector<Double>>();
						vector = new Vector();
						vector.add(t_vector.get(1));
						map2.put("disgust", vector);
						vector = new Vector();
						vector.add(t_vector.get(2));
						map2.put("fear", vector);
						vector = new Vector();
						vector.add(t_vector.get(3));
						map2.put("happiness", vector);
						vector = new Vector();
						vector.add(t_vector.get(4));
						map2.put("sadness", vector);
						vector = new Vector();
						vector.add(t_vector.get(5));
						map2.put("surprise", vector);

						String df = DateFormat.getDateInstance().format(tweet.getCreatedAt());
						sentimentOverTime.put(df, map2);
					} else {
						Map<String, Vector<Double>> map2 = sentimentOverTime.get(tweet.getCreatedAt());

						vector = map2.get("disgust");
						vector.add(t_vector.get(1));
						map2.put("disgust", vector);
						vector = map2.get("fear");
						vector.add(t_vector.get(2));
						map2.put("fear", vector);
						vector = map2.get("happiness");
						vector.add(t_vector.get(3));
						map2.put("happiness", vector);
						vector = map2.get("sadness");
						vector.add(t_vector.get(4));
						map2.put("sadness", vector);
						vector = map2.get("surprise");
						vector.add(t_vector.get(5));
						map2.put("surprise", vector);

						String df = DateFormat.getDateInstance().format(tweet.getCreatedAt());
						sentimentOverTime.put(df, map2);
					}
				}

			}

			for (Map.Entry<String, Map<String, Vector<Double>>> entryDate : sentimentOverTime.entrySet()) {

				System.out.println("Date : " + entryDate.getKey());

				for (Map.Entry<String, Vector<Double>> entrySentiment : entryDate.getValue().entrySet()) {
					double sum = 0;

					Vector<Double> values = entrySentiment.getValue();

					for (int i = 0; i < values.size(); i++) sum = sum + values.get(i);

					//calculate average value
					double average = sum / values.size();

					System.out.println(entrySentiment.getKey()+" : " + average);
				}

				System.out.println("######################");
			}

		//}

	}

	public static void myCode(){

		//StreamTwitterUser streamTwitterUser = new StreamTwitterUser("@Eurohoopsnet");

		TestApp testApp = new TestApp();
		//GeneralFunctions generalFunctions = new GeneralFunctions();

		//generalFunctions.printTweets(2845541223L);

		/*UserRetweeterGraph userRetweeterGraph = new UserRetweeterGraph();
//
		Multimap<Long, Long> amplifiers = ArrayListMultimap.create();
		Map<Long, Date> statusDate = new HashMap<Long, Date>();
		Map<Long, Long> statusPageID = new HashMap<Long, Long>();
//
		Multimap<Long, Long> userTweets = ArrayListMultimap.create();
		Multimap<Long, Long> userRetweeters = ArrayListMultimap.create();
		Paging paging;
		int pageno;
*/
//		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		context.setContextPath("/");
//
//		Server jettyServer = new Server(8888);
//		jettyServer.setHandler(context);
//
//		context.addServlet(new ServletHolder(new TopUsersServlet(512445285)),
//				"/userTweetGraph/topUsers");
//
//		System.out.println(String.format("%tc: Starting service on port %d", new Date(), 8888));
//		try {
//			jettyServer.start();
//			jettyServer.join();
//		} finally {
//			jettyServer.destroy();
//		}

		/*Map<String, Integer> users = new HashMap();
		users.put("@Eurohoopsnet", 1);
		users.put("@sport24", 1);
		users.put("@EuroLeague", 1);
		Iterator<Map.Entry<String, Integer>> iter = users.entrySet().iterator();
		PrintStream o = null;
		try {
			o = new PrintStream(new File("A.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Assign o to output stream
		System.setOut(o);
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
			Map.Entry<String, Integer> pair = iter.next();
			pageno = pair.getValue();
			paging = new Paging(pageno++, 1000);
			testApp.trackUserTimeLine(pair.getKey(), userRetweeterGraph, amplifiers,
					statusDate, statusPageID, userTweets, userRetweeters, paging);
			if(pageno==1000) pageno=1;
			users.put(pair.getKey(), pageno);
			if(!iter.hasNext()) iter = users.entrySet().iterator();
			System.out.println("User tweets: " + userTweets.size());
			System.out.println("UserRetweeters: " + userRetweeters.size());
			System.out.println("all ok\n");
		}*/


		//testApp.showRateLimits();
		//testApp.search("olympiacosbc", "2018-05-05", "2018-06-25");

		//String stringUrl = "http://localhost:8888/userTweetGraph/topUsers?k=10";

		//generalFunctions.topUsers(stringUrl);

		//testApp.printCentralityResult("alpha", false);

		Paging paging;
		int pageno = 1;

		//Map<Object, Double> stemMap = new HashMap<Object, Double>();
		//Map<Object, Double> sentimentMap = new HashMap<Object, Double>();

		ArrayList<Status> tweets = new ArrayList<Status>();

		while(true) {

			GeneralFunctions generalFunctions = new GeneralFunctions();
			boolean checkLimit = generalFunctions.checkRateLimit();
			if(checkLimit) {
				try {
					Thread.sleep(900000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			paging = new Paging(pageno++, 1000);

			//testApp.trackUserTimeLine("@pyrosvestiki", paging, stemMap, sentimentMap);
			//testApp.search("@pyrosvestiki", false);

			testApp.sentimentOverTime("@olympiacos_org","replies", paging, tweets);
			System.out.println("Pageno : "+pageno);
			if(pageno==1000) pageno=1;

			/*stemMap = GeneralFunctions.sortByValue(stemMap);

			for(Map.Entry<Object, Double> entry : stemMap.entrySet()){
				System.out.println(entry.getKey());
				System.out.println(entry.getValue());
			}

			System.out.println("anger: " + sentimentMap.get(0));
			System.out.println("disgust: " + sentimentMap.get(1));
			System.out.println("fear: " + sentimentMap.get(2));
			System.out.println("happiness: " + sentimentMap.get(3));
			System.out.println("sadness: " + sentimentMap.get(4));
			System.out.println("surprise: " + sentimentMap.get(5));*/

		}

		/*Paging paging;
		int pageno = 1;

		while(true) {

			GeneralFunctions generalFunctions = new GeneralFunctions();
			boolean checkLimit = generalFunctions.checkRateLimit();
			if (checkLimit) {
				try {
					Thread.sleep(900000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			paging = new Paging(pageno++, 1000);

			testApp.readComments("@pyrosvestiki", paging);

			if (pageno == 1000) pageno = 1;

		}*/
	}

	public void findUsers(String search_string)
	{
		try
		{
			UsersResources userResource = TwitterApi.client().users();

			User user = userResource.showUser(search_string);

			System.out.println(user.getName());

			this.userTimeline(user.getId());
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


	public ArrayList<Status> search(String query_string, boolean getRetweet)
	{

		ArrayList<Status> tweets = null;

		try
		{
			Query query = new Query(query_string);
			query.setResultType(Query.RECENT);
			query.setCount(100);

			QueryResult result;

			do
			{
				result = TwitterApi.client().search(query);

				tweets = (ArrayList<Status>) result.getTweets();
				System.out.println(tweets.size());
				Iterator<Status> it = tweets.iterator();
				while (it.hasNext()) {
					Status status = it.next();
					if(!getRetweet) {
						if (status.isRetweet()) {
							it.remove();
						}
					}else{
						if (!status.isRetweet()) {
							it.remove();
						}
					}
				}
				System.out.println(tweets.size());
				/*for(Status tweet : tweets)
				{
					System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				}*/

			}
			while( (query = result.nextQuery()) != null );
		}
		catch(TwitterException te)
		{
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
		}

		return tweets;
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

	public static void main(String[] args) throws Exception
	{

		myCode();

	}

}