package TwitterAnalytics.Services;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Hashtag;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HashtagService
{

    public static Hashtag createHashtag(String name)
    {
        Query q = Hibernate.session().createQuery("from Hashtag where name = :name");
        q.setParameter("name", name);
        List hashtagsResults = q.getResultList();

        if(hashtagsResults.size() > 0)
        {
            return (Hashtag) hashtagsResults.get(0);
        }

        Hashtag hashtag = new Hashtag(name);
        Hibernate.save(hashtag);

        return hashtag;
    }


    public static List<Long> removeHashtagsByDateRange(Date fromDate, Date toDate)
    {
        List<Long> toDeleteIds = new ArrayList<>();

        try
        {
            NativeQuery query = Hibernate.session().createSQLQuery("SELECT id from hashtags where created_at between :fromDate and :toDate");
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

                NativeQuery query = Hibernate.session().createSQLQuery("delete from trends_hashtags where hashtag_id IN (:ids)");
                query.setParameterList("ids", toDeleteIds);
                query.executeUpdate();

                NativeQuery query2 = Hibernate.session().createSQLQuery("delete from hashtags where id IN (:ids)");
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
