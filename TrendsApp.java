import TwitterAnalytics.Models.Hashtag;
import TwitterAnalytics.Tokenizer.Tokenizer;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TrendsResources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TrendsApp
{


    public TrendsApp()
    {
        //this.collect();

        this.analysis();
    }


    public void collect()
    {
        this.getTrends(23424833, 10, 10);
    }


    public void analysis()
    {
        this.trend_analysis();
    }


    private void getTrends(int woeid, int max_trends, int max_tweets_per_trend)
    {
        List<twitter4j.Trend> top10trends = this.getTopTrends(woeid, max_trends);

        for(twitter4j.Trend trend : top10trends)
        {
            TwitterAnalytics.Models.Trend trendC = new TwitterAnalytics.Models.Trend(trend.getName(), trend.getQuery());
            int inserted_id = trendC.save();

            this.search(inserted_id, trend.getQuery(), max_tweets_per_trend );
        }
    }


    private List<Trend> getTopTrends(int woeid, int top)
    {
        try
        {
            TrendsResources trendsResources = TwitterApi.client().trends();

            Trends trends = trendsResources.getPlaceTrends(woeid);
            twitter4j.Trend[] trendsArray = trends.getTrends();

            return Arrays.asList(trendsArray).subList(0, top);
        }
        catch(TwitterException te)
        {
            te.printStackTrace();
            System.out.println("Failed to find trends: " + te.getMessage());
        }

        return Collections.emptyList();
    }


    private void search(int trend_id, String query_string, int max_results)
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

                    TwitterAnalytics.Models.Tweet tweetC = new TwitterAnalytics.Models.Tweet(tweet.getText(), tweet.getId(), trend_id, new java.sql.Timestamp(tweet.getCreatedAt().getTime()) );
                    tweetC.save();

                    for(HashtagEntity hashtagEntity : tweet.getHashtagEntities())
                    {
                        Hashtag hashtag = new Hashtag(hashtagEntity.getText(), trend_id);
                        hashtag.save();
                    }
                }
            }
            while( ((query = result.nextQuery()) != null) && (tweet_counter <max_results) );

            System.out.println("collected: " + tweet_counter + " tweets");
        }
        catch(TwitterException te)
        {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
    }


    private void trend_analysis()
    {
        Tokenizer tokenizer = new Tokenizer();

        /*
        for(TwitterAnalytics.Models.Trend trend : TwitterAnalytics.Models.Trend.all())
        {
            System.out.println("----------------------- ------------------------------------");

            System.out.println(trend.name);


            for(TwitterAnalytics.Models.Tweet tweet : trend.tweets())
            {
                System.out.println("-----------------------");
                System.out.println(tweet.text);
                System.out.println("-----------------------");

                tokenizer.tokenize(tweet.text);

                //break;
            }

            //break;
        }
        */
    }

}
