package TwitterAnalytics.Services;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Tweet;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



public class TweetService
{

    public static Tweet createTweet(String text, long twitter_id, Timestamp timestamp)
    {
        Query q = Hibernate.session().createQuery("from Tweet where twitter_id = :twitter_id");
        q.setParameter("twitter_id", twitter_id);
        List tweetsResults = q.getResultList();

        if(tweetsResults.size() > 0)
        {
            return (Tweet) tweetsResults.get(0);
        }

        Tweet tweet = new Tweet(text, twitter_id, timestamp);
        Hibernate.save(tweet);

        return tweet;
    }


    public static Tweet createTweet(String text, String clean_text, long twitter_id, Timestamp timestamp)
    {
        Query q = Hibernate.session().createQuery("from Tweet where twitter_id = :twitter_id");
        q.setParameter("twitter_id", twitter_id);
        List tweetsResults = q.getResultList();

        if(tweetsResults.size() > 0)
        {
            return (Tweet) tweetsResults.get(0);
        }

        Tweet tweet = new Tweet(text, clean_text, twitter_id, timestamp);
        Hibernate.save(tweet);

        return tweet;
    }


    public static List<Tweet> getAll()
    {
        try
        {
            return Hibernate.session().createCriteria(Tweet.class).list();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }

}
