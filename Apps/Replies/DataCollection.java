package Apps.Replies;

import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Reply;
import TwitterAnalytics.Models.Tweet;
import TwitterAnalytics.Services.TweetService;
import TwitterAnalytics.TextAnalysis.Sentimenter.Sentimenter;
import TwitterAnalytics.TextAnalysis.Stemmer.Stemmer;
import TwitterAnalytics.TextAnalysis.Tokenizer.Tokenizer;
import TwitterAnalytics.TextAnalysis.Utils;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TimelinesResources;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DataCollection {

    public DataCollection()
    {

    }


    public void collectionOverTime(String pagename, Map<Status,Integer> repliesCount, Paging paging, Map<Status,Long> tweets){

        System.out.print("size of array : "+tweets.size());

        TimelinesResources timelinesResource = TwitterApi.client().timelines();

        try {

            User user = TwitterApi.client().showUser(pagename);

            ResponseList<Status> tweetsFetched = timelinesResource.getUserTimeline(user.getId(), paging);

            int counter = 0;
            for(Status tweet : tweetsFetched) {

                if(!repliesCount.containsKey(tweet)) repliesCount.put(tweet,0);
                getReplies(pagename, tweet, tweets);

                counter = counter + 1;
            }

            System.out.println("Counter : "+counter);

        } catch(TwitterException e){
            e.printStackTrace();
        }

        System.out.println("yoloooooo");
        System.out.println(tweets.size());

    }

    public void getReplies(String screenName, Status tweetInput, Map<Status,Long> replies) {

        Tokenizer tokenizer = new Tokenizer(); //, Status tweetInput

        try {
            Query query = new Query("to:"+screenName + " since_id:" + tweetInput.getId());

            QueryResult results;

            do {
                results = TwitterApi.client().search(query);

                List<Status> tweets = results.getTweets();

                for (Status tweet : tweets){
                    if (tweet.getInReplyToStatusId()==tweetInput.getId()) {

                        String clean_textH = TwitterApi.cleanTweetText(tweet,"tweets");

                        Tweet tweetH = TweetService.createTweet( tweet.getText(), clean_textH, tweet.getId(), new java.sql.Timestamp(tweet.getCreatedAt().getTime()) );

                        String clean_textY = TwitterApi.cleanTweetText(tweet,"tweets");
                        Tweet tweetY = TweetService.createTweet( tweetInput.getText(), clean_textY, tweetInput.getId(), new java.sql.Timestamp(tweetInput.getCreatedAt().getTime()) );

                        Reply reply = new Reply (tweetH,tweetY);
                        Hibernate.save(reply);

                        replies.put(tweet,0L);

                        System.out.println("Size "+replies.size());

                        System.out.println("Reply : "+tweet.getText());
                        System.out.println("TweetInput : "+tweetInput.getText());

                    }
                }
            } while ((query = results.nextQuery()) != null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
