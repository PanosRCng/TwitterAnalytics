package TwitterAnalytics.Services;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Tweet;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class TweetService
{

    public static Tweet createTweet(String text, long twitter_id, Timestamp timestamp, Boolean retweetFlag)
    {
        Query q = Hibernate.session().createQuery("from Tweet where twitter_id = :twitter_id");
        q.setParameter("twitter_id", twitter_id);
        List tweetsResults = q.getResultList();

        if(tweetsResults.size() > 0)
        {
            return (Tweet) tweetsResults.get(0);
        }

        Tweet tweet = new Tweet(text, twitter_id, timestamp, retweetFlag);
        Hibernate.save(tweet);

        return tweet;
    }


    public static Tweet createTweet(String text, String clean_text, long twitter_id, Timestamp timestamp, Boolean retweetFlag)
    {

        Query q = Hibernate.session().createQuery("from Tweet where twitter_id = :twitter_id");
        q.setParameter("twitter_id", twitter_id);
        List tweetsResults = q.getResultList();

        if(tweetsResults.size() > 0)
        {
            return (Tweet) tweetsResults.get(0);
        }

        Tweet tweet = new Tweet(text, clean_text, twitter_id, timestamp, retweetFlag);
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


    public static List<Long> removeTweetsByDateRange(Date fromDate, Date toDate)
    {
        List<Long> toDeleteIds = new ArrayList<>();

        try
        {
            NativeQuery query = Hibernate.session().createSQLQuery("SELECT id from tweets where created_at between :fromDate and :toDate");
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            List<Integer> results = query.getResultList();

            for(Integer id: results)
            {
                toDeleteIds.add(id.longValue());
            }
        }
        catch (Exception e)
        {
            //
        }

        if(toDeleteIds.size() > 0)
        {
            try
            {
                Transaction transaction = Hibernate.session().getTransaction();
                transaction.begin();

                NativeQuery query = Hibernate.session().createSQLQuery("delete from trends_tweets where tweet_id IN (:ids)");
                query.setParameterList("ids", toDeleteIds);
                query.executeUpdate();

                NativeQuery query2 = Hibernate.session().createSQLQuery("delete from tweets where id IN (:ids)");
                query2.setParameterList("ids", toDeleteIds);
                query2.executeUpdate();

                transaction.commit();
            }
            catch (Exception e)
            {
                //
            }
        }

        return toDeleteIds;
    }

}
