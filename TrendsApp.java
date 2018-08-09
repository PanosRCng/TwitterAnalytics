import TwitterAnalytics.InvertedIndex.InvertedIndex;
import TwitterAnalytics.Models.Hashtag;
import TwitterAnalytics.Models.TrendSentiment;
import TwitterAnalytics.TextAnalysis.Sentimenter.Sentimenter;
import TwitterAnalytics.TextAnalysis.Stemmer.Stemmer;
import TwitterAnalytics.TextAnalysis.Tokenizer.Tokenizer;
import TwitterAnalytics.TextAnalysis.Utils;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TrendsResources;

import java.util.*;



public class TrendsApp implements RateLimitStatusListener
{


    public TrendsApp()
    {
        //TwitterApi.client().addRateLimitStatusListener(this);
        //this.collect();

        this.analysis();
    }


    public void onRateLimitReached(RateLimitStatusEvent event)
    {
        System.out.println("Rate limit reached, app will back off now");

        this.backoff();
    }


    public void onRateLimitStatus(RateLimitStatusEvent event)
    {
        //
    }


    public void collect()
    {
        while(true)
        {
            this.getTrends(23424833, 10, 100);

            this.wait(15);
        }
    }


    public void analysis()
    {
        this.trend_analysis();
    }


    private void backoff()
    {
        this.wait(15);

        this.collect();
    }


    private void wait(int minutes)
    {
        try
        {
            Thread.sleep(minutes * 60 *1000);
        }
        catch(InterruptedException ex)
        {
            System.out.println("Sleep interrupted");
        }
    }


    private void getTrends(int woeid, int max_trends, int max_tweets_per_trend)
    {
        List<twitter4j.Trend> top10trends = this.getTopTrends(woeid, max_trends);

        for(twitter4j.Trend trend : top10trends)
        {
            TwitterAnalytics.Models.Trend trendC = new TwitterAnalytics.Models.Trend(trend.getName(), trend.getQuery());
            int inserted_id = trendC.save();

            this.search(inserted_id, trend.getName(), trend.getQuery(), max_tweets_per_trend );
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


    private void search(int trend_id, String trend_name, String query_string, int max_results)
    {
        InvertedIndex invertedIndex = InvertedIndex.open(trend_name);
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

                    TwitterAnalytics.Models.Tweet tweetC = new TwitterAnalytics.Models.Tweet(tweet.getText(), clean_text, tweet.getId(), trend_id, new java.sql.Timestamp(tweet.getCreatedAt().getTime()) );
                    long tweet_id = tweetC.save();

                    for(HashtagEntity hashtagEntity : tweet.getHashtagEntities())
                    {
                        Hashtag hashtag = new Hashtag(hashtagEntity.getText(), trend_id);
                        hashtag.save();
                    }

                    Vector<String> tokens = tokenizer.tokenize(clean_text);

                    StringBuilder sb = new StringBuilder();
                    for(String token : tokens)
                    {
                        sb.append(token + " ");
                    }

                    invertedIndex.insert(Long.toString(tweet_id), sb.toString());
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

        invertedIndex.close();
    }


    private void trend_analysis()
    {
        Tokenizer tokenizer = new Tokenizer();

        for(TwitterAnalytics.Models.Trend trend : TwitterAnalytics.Models.Trend.all())
        {

            System.out.println("----------------------- ------------------------------------");
            System.out.println(trend.name);

            Vector<Vector<Double>> t_matrix = new Vector<>();

            for(TwitterAnalytics.Models.Tweet tweet : trend.tweets())
            {

                //System.out.println("-----------------------");
                //System.out.println(tweet.text);

                Vector<String> tokens = tokenizer.tokenize(tweet.clean_text);

                Vector<String> stems = Stemmer.stem( Utils.lowercase(tokens) );

                Vector<Double> t_vector = Sentimenter.sentimentVector( stems );

                if(t_vector == null)
                {
                    continue;
                }

                t_matrix.add( t_vector );
            }

            if(t_matrix.size() == 0)
            {
                continue;
            }

            Vector<Double> s = Sentimenter.trendVector(t_matrix);

            TrendSentiment trendSentiment = new TwitterAnalytics.Models.TrendSentiment(trend.entry_id, s.get(0), s.get(1), s.get(2), s.get(3), s.get(4), s.get(5));
            int inserted_id = trendSentiment.save();


            InvertedIndex invertedIndex = InvertedIndex.open(trend.name);

            for(String term : invertedIndex.topNerms(10))
            {
                System.out.println(term);
            }
        }

    }

}
