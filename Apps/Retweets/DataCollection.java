package Apps.Retweets;

import Apps.GeneralFunctions;
import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Retweet;
import TwitterAnalytics.Models.Retweeter;
import TwitterAnalytics.Models.Tweet;
import TwitterAnalytics.Services.TweetService;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TimelinesResources;

import java.util.List;

public class DataCollection {

    ResponseList<Status> retweets;

    public DataCollection()
    {

    }

    public void trackUserTimeLine(long userID, Paging paging, TimelinesResources timelinesResource, boolean storeRetweeters){

        try {
            ResponseList<Status> tweets = timelinesResource.getUserTimeline(userID, paging);

            for(Status tweet : tweets)
            {

                retweets = TwitterApi.client().tweets().getRetweets(tweet.getId());

                for(Status retweet : retweets)
                {

                    String clean_textH = TwitterApi.cleanTweetText(tweet,"tweets");
                    Tweet tweetH = TweetService.createTweet( tweet.getText(), clean_textH, tweet.getId(), new java.sql.Timestamp(tweet.getCreatedAt().getTime()), tweet.isRetweet() );

                    String clean_textY = TwitterApi.cleanTweetText(retweet,"tweets");
                    Tweet tweetY = TweetService.createTweet( retweet.getText(), clean_textY, retweet.getId(), new java.sql.Timestamp(retweet.getCreatedAt().getTime()), retweet.isRetweet() );

                    Retweet retweetY = new Retweet (tweetY,tweetH,tweetY.getTimestamp());
                    Hibernate.save(retweetY);

                    if(storeRetweeters){
                        Retweeter retweeter = new Retweeter (retweet.getUser().getId(), tweet.getUser().getId(), new java.sql.Timestamp(retweet.getCreatedAt().getTime()), Boolean.TRUE);
                        Hibernate.save(retweeter);
                    }

                }

            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }


    }

    public void trackUserTimeLine(GeneralFunctions generalFunctions, String screenName, String since, String until, Boolean storeRetweeters){

        try {

            Query query = new Query("from:"+screenName + " since:" + since + " until:" + until);

            QueryResult results;

            do {

                generalFunctions.checkRateLimitAndDelay();

                results = TwitterApi.client().search(query);

                List<Status> tweets = results.getTweets();

                for (Status tweet : tweets){

                    retweets = TwitterApi.client().tweets().getRetweets(tweet.getId());

                    for(Status retweet : retweets)
                    {

                        String clean_textH = TwitterApi.cleanTweetText(tweet,"tweets");
                        Tweet tweetH = TweetService.createTweet( tweet.getText(), clean_textH, tweet.getId(), new java.sql.Timestamp(tweet.getCreatedAt().getTime()), tweet.isRetweet() );

                        String clean_textY = TwitterApi.cleanTweetText(retweet,"tweets");
                        Tweet tweetY = TweetService.createTweet( retweet.getText(), clean_textY, retweet.getId(), new java.sql.Timestamp(retweet.getCreatedAt().getTime()), retweet.isRetweet() );

                        Retweet retweetY = new Retweet (tweetY,tweetH,tweetY.getTimestamp());
                        Hibernate.save(retweetY);

                        if(storeRetweeters){
                            Retweeter retweeter = new Retweeter (retweet.getUser().getId(), tweet.getUser().getId(), new java.sql.Timestamp(retweet.getCreatedAt().getTime()), Boolean.TRUE);
                            Hibernate.save(retweeter);
                        }
                    }
                }

            } while ((query = results.nextQuery()) != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
