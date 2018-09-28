import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Trend;
import TwitterAnalytics.Models.Tweet;
import TwitterAnalytics.Models.Hashtag;
import TwitterAnalytics.Models.TrendSentiment;
import TwitterAnalytics.Services.HTrendService;
import TwitterAnalytics.TextAnalysis.Tokenizer.Tokenizer;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TrendsResources;
import TwitterAnalytics.TextAnalysis.Sentimenter.Sentimenter;
import TwitterAnalytics.TextAnalysis.Stemmer.Stemmer;
import TwitterAnalytics.TextAnalysis.Utils;

import java.util.*;



public class TrendsApp implements RateLimitStatusListener
{


    public TrendsApp()
    {
        TwitterApi.client().addRateLimitStatusListener(this);
        this.collect();

        //this.analysis();
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
            this.getTrends(23424833, 10, 10);

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
            Trend trendH = new Trend(trend.getName(), trend.getQuery());
            Hibernate.save(trendH);

            this.search(trendH.getId(), trend.getName(), trend.getQuery(), max_tweets_per_trend );
        }
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


    private void search(long trend_id, String trend_name, String query_string, int max_results)
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

                    Tweet tweetH = new Tweet(tweet.getText(), clean_text, tweet.getId(), trend_id, new java.sql.Timestamp(tweet.getCreatedAt().getTime()) );
                    Hibernate.save(tweetH);
                    long tweet_id = tweetH.getId();

                    for(HashtagEntity hashtagEntity : tweet.getHashtagEntities())
                    {
                        Hashtag hashtag = new Hashtag(hashtagEntity.getText(), trend_id);
                        Hibernate.save(hashtag);
                    }

                    Vector<String> tokens = tokenizer.tokenize(clean_text);

                    StringBuilder sb = new StringBuilder();
                    for(String token : tokens)
                    {
                        sb.append(token + " ");
                    }

                    //invertedIndex.insert(Long.toString(tweet_id), sb.toString());
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

        //invertedIndex.close();
    }


    private void trend_analysis()
    {
        Tokenizer tokenizer = new Tokenizer();


        for(Trend trend : HTrendService.getAll())
        {
            System.out.println("----------------------- ------------------------------------");
            System.out.println(trend.getName());


            Vector<Vector<Double>> t_matrix = new Vector<>();


            for(Tweet tweet : trend.tweets())
            {
                //System.out.println("-----------------------");
                //System.out.println(tweet.text);

                Vector<String> tokens = tokenizer.tokenize(tweet.getCleanText());

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

            TrendSentiment trendSentiment = new TrendSentiment(trend.getId(), s.get(0), s.get(1), s.get(2), s.get(3), s.get(4), s.get(5));
            Hibernate.save(trendSentiment);

            /*
            InvertedIndex invertedIndex = InvertedIndex.open(trend.getName());

            for(String term : invertedIndex.topNerms(10))
            {
                System.out.println(term);
            }
            */
        }

    }

}
