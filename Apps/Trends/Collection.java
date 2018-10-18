package Apps.Trends;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.InvertedIndex.InvertedIndex;
import TwitterAnalytics.Models.Hashtag;
import TwitterAnalytics.Models.Trend;
import TwitterAnalytics.Models.TrendsList;
import TwitterAnalytics.Models.Tweet;
import TwitterAnalytics.Services.HashtagService;
import TwitterAnalytics.Services.TrendService;
import TwitterAnalytics.Services.TweetService;
import TwitterAnalytics.TextAnalysis.Tokenizer.Tokenizer;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TrendsResources;

import java.util.*;


public class Collection extends TimerTask implements RateLimitStatusListener
{
    private Map<Integer, Trend> trendsList;


    public Collection()
    {
        TwitterApi.client().addRateLimitStatusListener(this);
    }


    public void onRateLimitReached(RateLimitStatusEvent event)
    {
        System.out.println("Rate limit reached, app will back off now");
    }


    public void onRateLimitStatus(RateLimitStatusEvent event)
    {
        //
    }

    private void getTrends(int woeid, int max_trends, int max_tweets_per_trend)
    {
        List<twitter4j.Trend> top10trends = this.getTopTrends(woeid, max_trends);

        this.getTrendsList(top10trends);

        for(Trend trend : this.trendsList.values())
        {
            this.search(trend, trend.getName(), trend.getQuery(), max_tweets_per_trend );
        }
    }


    private void getTrendsList(List<twitter4j.Trend> top10trends)
    {
        this.trendsList = new HashMap<>();

        int counter = 1;

        for(twitter4j.Trend trend : top10trends)
        {
            TwitterAnalytics.Models.Trend trendH = TrendService.createTrend(trend.getName(), trend.getQuery());

            this.trendsList.put(counter++, trendH);
        }

        TrendsList trendsListH = new TrendsList();
        trendsListH.setTimestamp(new Date());
        trendsListH.setTrend_id_1( this.trendsList.get(1).getId() );
        trendsListH.setTrend_id_2( this.trendsList.get(2).getId() );
        trendsListH.setTrend_id_3( this.trendsList.get(3).getId() );
        trendsListH.setTrend_id_4( this.trendsList.get(4).getId() );
        trendsListH.setTrend_id_5( this.trendsList.get(5).getId() );
        trendsListH.setTrend_id_6( this.trendsList.get(6).getId() );
        trendsListH.setTrend_id_7( this.trendsList.get(7).getId() );
        trendsListH.setTrend_id_8( this.trendsList.get(8).getId() );
        trendsListH.setTrend_id_9( this.trendsList.get(9).getId() );
        trendsListH.setTrend_id_10( this.trendsList.get(10).getId() );

        Hibernate.save( trendsListH );
    }


    private List<twitter4j.Trend> getTopTrends(int woeid, int top)
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


    private void search(Trend trend, String trend_name, String query_string, int max_results)
    {
        //InvertedIndex invertedIndex = InvertedIndex.open(trend_name);
        Tokenizer tokenizer = new Tokenizer();

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

                    String clean_text = TwitterApi.cleanTweetText( tweet );

                    Tweet tweetH = TweetService.createTweet( tweet.getText(), clean_text, tweet.getId(), new java.sql.Timestamp(tweet.getCreatedAt().getTime()) );
                    trend.addTweet(tweetH);

                    for(HashtagEntity hashtagEntity : tweet.getHashtagEntities())
                    {
                        Hashtag hashtagH = HashtagService.createHashtag(hashtagEntity.getText());
                        trend.addHashtag(hashtagH);
                    }

                    Vector<String> tokens = tokenizer.tokenize(clean_text);

                    StringBuilder sb = new StringBuilder();
                    for(String token : tokens)
                    {
                        sb.append(token + " ");
                    }

                    //invertedIndex.insert(Long.toString(tweetH.getId()), sb.toString());
                }
            }
            while( ((query = result.nextQuery()) != null) && (tweet_counter <max_results) );

            System.out.println(trend.getName() + ": collected: " + tweet_counter + " tweets");
        }
        catch(TwitterException te)
        {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }

        //invertedIndex.close();
    }



    @Override
    public void run()
    {
        System.out.println(new Date().toString() + ": COLLECTING");

        this.getTrends(23424833, 10, 100);
    }


}
