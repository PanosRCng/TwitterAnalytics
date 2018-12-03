package Apps;

import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.HubRetweeter;
import TwitterAnalytics.Models.Retweeter;
import TwitterAnalytics.TwitterApi;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.alg.scoring.HarmonicCentrality;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import twitter4j.*;
import twitter4j.api.TimelinesResources;
import twitter4j.api.UsersResources;

import org.jgrapht.alg.scoring.AlphaCentrality;

import java.util.*;

import static TwitterAnalytics.Services.RetweeterService.getRetweets;

public class GeneralFunctions {

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

                List<Status> statuses = result.getTweets();

                for(Status status : statuses)
                {
                    if(status.isRetweet())
                    {
                        continue;
                    }

                    tweet_counter++;

                    //Tweet tweet = new Tweet(status.getText(), status.getId(), trend_id, new java.sql.Timestamp(status.getCreatedAt().getTime()) );
                    //Hibernate.save(tweet);
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

    public boolean checkRateLimit()
    {

        boolean flag = false;

        try
        {

            Map<String, RateLimitStatus> rateLimit = TwitterApi.client().getRateLimitStatus();

            for (String timeStatus : rateLimit.keySet()) {

                RateLimitStatus timeLeft = rateLimit.get(timeStatus);

                if (timeLeft != null && timeLeft.getRemaining() == 0) {

                    //Make Thread sleep for 15Minutes

                    System.err.println("Rate limit exceeded!!!");

                    flag = true;

                }



            }

        }
        catch(TwitterException twitterException)
        {
            twitterException.printStackTrace();
            System.out.println("Failed : " + twitterException.getMessage());
        }

        return(flag);
    }

    public void storeCentralityResult(String centrality) {

        Graph<Long, DefaultEdge> graph = new DefaultDirectedGraph<Long, DefaultEdge>(DefaultEdge.class);
        Map<Long, Long> map;

        for(Retweeter retweeter : getRetweets(Boolean.FALSE)){

            if (!graph.containsVertex(retweeter.getRetweeter_id())) { ;
                graph.addVertex(retweeter.getRetweeter_id());
            }
            if (!graph.containsVertex(retweeter.getRetweeted_user_id())) {
                graph.addVertex(retweeter.getRetweeted_user_id());
            }
            graph.addEdge(retweeter.getRetweeter_id(), retweeter.getRetweeted_user_id());
        }

        if (centrality.equals("alpha")) {

            AlphaCentrality alphaCentrality = new AlphaCentrality(graph);
            map = GeneralFunctions.sortByValue(alphaCentrality.getScores());

        } else if ((centrality.equals("betweenness"))) {

            BetweennessCentrality betweennessCentrality = new BetweennessCentrality(graph);
            map = GeneralFunctions.sortByValue(betweennessCentrality.getScores());

        } else if ((centrality.equals("closeness"))) {

            ClosenessCentrality closenessCentrality = new ClosenessCentrality(graph);
            map = GeneralFunctions.sortByValue(closenessCentrality.getScores());

        }else if((centrality.equals("harmonic"))) {

            HarmonicCentrality harmonicCentrality = new HarmonicCentrality(graph);
            map = GeneralFunctions.sortByValue(harmonicCentrality.getScores());

        }else{

            AlphaCentrality alphaCentrality = new AlphaCentrality(graph);
            map = GeneralFunctions.sortByValue(alphaCentrality.getScores());

        }

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
            Set<DefaultEdge> userTweets = graph.edgesOf(key);
            System.out.println("Number of edges: " + userTweets.size());
            System.out.println("Location : " + user.getLocation());

            HubRetweeter hubretweeter = new HubRetweeter (user.getName(), user.getScreenName(), (Math.round(value * 100.0) / 100.0), userTweets.size(), user.getLocation());
            Hibernate.save(hubretweeter);

        }
        catch(TwitterException twitterException)
        {
            twitterException.printStackTrace();
            System.out.println("Failed : " + twitterException.getMessage());
        }
    }

    public static Map<Object, Double>sortByValue(Map<Object, Double> unsortMap) {

        List<Map.Entry<Object, Double>> list =
                new LinkedList<Map.Entry<Object, Double>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Object, Double>>() {
            public int compare(Map.Entry<Object, Double> o1,
                               Map.Entry<Object, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<Object, Double> sortedMap = new LinkedHashMap<Object, Double>();
        for (Map.Entry<Object, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
