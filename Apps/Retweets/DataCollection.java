package Apps.Retweets;

import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Retweet;
import TwitterAnalytics.Models.Retweeter;
import TwitterAnalytics.Models.Tweet;
import TwitterAnalytics.Services.TweetService;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TimelinesResources;

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
                    Tweet tweetH = TweetService.createTweet( tweet.getText(), clean_textH, tweet.getId(), new java.sql.Timestamp(tweet.getCreatedAt().getTime()) );

                    String clean_textY = TwitterApi.cleanTweetText(retweet,"tweets");
                    Tweet tweetY = TweetService.createTweet( retweet.getText(), clean_textY, retweet.getId(), new java.sql.Timestamp(retweet.getCreatedAt().getTime()) );

                    Retweet retweetY = new Retweet (tweetY,tweetH,tweetY.getTimestamp());
                    Hibernate.save(retweetY);

                    if(storeRetweeters){
                        Retweeter retweeter = new Retweeter (retweet.getUser().getId(), tweet.getUser().getId(), new java.sql.Timestamp(retweet.getCreatedAt().getTime()));
                        Hibernate.save(retweeter);
                    }

                }

            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }


    }
}
