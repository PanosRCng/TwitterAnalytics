package Apps.Replies;

import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Reply;
import TwitterAnalytics.Models.Tweet;
import TwitterAnalytics.Services.TweetService;
import TwitterAnalytics.TextAnalysis.Tokenizer.Tokenizer;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TimelinesResources;

import java.util.List;

public class DataCollection {

    public DataCollection()
    {

    }


    public void collectionOverTime(String pagename, Paging paging){

        TimelinesResources timelinesResource = TwitterApi.client().timelines();

        try {

            User user = TwitterApi.client().showUser(pagename);

            ResponseList<Status> tweetsFetched = timelinesResource.getUserTimeline(user.getId(), paging);

            for(Status tweet : tweetsFetched) {

                getReplies(pagename, tweet);

            }

        } catch(TwitterException e){
            e.printStackTrace();
        }

    }

    public void getReplies(String screenName, Status tweetInput) {

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
                        Tweet tweetH = TweetService.createTweet( tweet.getText(), clean_textH, tweet.getId(), new java.sql.Timestamp(tweet.getCreatedAt().getTime()), tweet.isRetweet() );

                        String clean_textY = TwitterApi.cleanTweetText(tweetInput,"tweets");
                        Tweet tweetY = TweetService.createTweet( tweetInput.getText(), clean_textY, tweetInput.getId(), new java.sql.Timestamp(tweetInput.getCreatedAt().getTime()), tweetInput.isRetweet() );

                        Reply reply = new Reply (tweetH,tweetY,tweetH.getTimestamp());
                        Hibernate.save(reply);

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
