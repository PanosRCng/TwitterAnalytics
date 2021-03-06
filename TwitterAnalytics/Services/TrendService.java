package TwitterAnalytics.Services;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Trend;
import TwitterAnalytics.Models.Tweet;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;


public class TrendService
{

    public static Trend createTrend(String name, String query)
    {
        Query q = Hibernate.session().createQuery("from Trend where name = :name");
        q.setParameter("name", name);
        List trendsResults = q.getResultList();

        if(trendsResults.size() > 0)
        {
            return (Trend) trendsResults.get(0);
        }

        Trend trend = new Trend(name, query);
        Hibernate.save(trend);

        return trend;
    }


    public static List<Trend> getAll()
    {
        try
        {
            return Hibernate.session().createCriteria(Trend.class).list();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }


    public static void addTweet(long trend_id, long tweet_id)
    {
        try
        {
            Transaction transaction = Hibernate.session().getTransaction();
            transaction.begin();

            NativeQuery query = Hibernate.session().createSQLQuery("insert ignore into trends_tweets(trend_id, tweet_id) values(:trend_id, :tweet_id)");
            query.setParameter("trend_id", trend_id);
            query.setParameter("tweet_id", tweet_id);

            query.executeUpdate();
            transaction.commit();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }


    public static void addHashtag(long trend_id, long hashtag_id)
    {
        try
        {
            Transaction transaction = Hibernate.session().getTransaction();
            transaction.begin();

            NativeQuery query = Hibernate.session().createSQLQuery("insert ignore into trends_hashtags(trend_id, hashtag_id) values(:trend_id, :hashtag_id)");
            query.setParameter("trend_id", trend_id);
            query.setParameter("hashtag_id", hashtag_id);

            query.executeUpdate();
            transaction.commit();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }


    public static List<Tweet> tweets(Long trend_id)
    {
        try
        {
            NativeQuery query = Hibernate.session().createSQLQuery("SELECT tweets.* FROM trends JOIN trends_tweets tt ON trends.id = tt.trend_id JOIN tweets ON tt.tweet_id = tweets.id where trends.id = :trend_id");
            query.setParameter("trend_id", trend_id);
            query.addEntity(Tweet.class);

            return query.getResultList();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }


    public static List<Tweet> tweetsByDateRange(Long trend_id, Date fromDate, Date toDate)
    {
        try
        {
            NativeQuery query = Hibernate.session().createSQLQuery("SELECT tweets.* FROM trends JOIN trends_tweets tt ON trends.id = tt.trend_id JOIN tweets ON tt.tweet_id = tweets.id where trends.id = :trend_id and tweets.created_at between :fromDate and :toDate");
            query.setParameter("trend_id", trend_id);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            query.addEntity(Tweet.class);

            return query.getResultList();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }


    public static List<Long> removeTrendsByDateRange(Date fromDate, Date toDate)
    {
        List<Long> toDeleteIds = new ArrayList<>();

        try
        {
            NativeQuery query = Hibernate.session().createSQLQuery("SELECT id from trends where created_at between :fromDate and :toDate");
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

                NativeQuery query = Hibernate.session().createSQLQuery("delete from trends where id IN (:ids)");
                query.setParameterList("ids", toDeleteIds);
                query.executeUpdate();

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
